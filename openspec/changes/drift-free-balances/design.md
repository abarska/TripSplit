# Design

## Context

See proposal.md (Why) for the problem. The relevant current state:

- `TripItemRepository` writes an item together with `BalanceCalculator` deltas into `participant_balances` (`amountUsd`, BigDecimal). On delete it recomputes the deltas from the current rate and the current `ExpenseParticipantCrossRef` participants (current multiplicators) and applies their negation.
- `TripExpense.amount`, `TripPayment.amount` and `ExchangeRate.rate` are `Double`. `CurrencySyncWorker` receives rates as strings (`Map<String, String>`) and parses them to `Double`, using `0.0` when parsing fails.
- Participants and currencies are already soft-deleted (`INACTIVE`).
- `TripRepository.saveTrip` is annotated with `androidx.room.Transaction`, but that annotation only has an effect on DAO methods, so the trip, participant and currency writes are not actually atomic today.
- The database is at v1 with `exportSchema = false` and `fallbackToDestructiveMigration(false)`. Room is 2.8.5, and the converters include `BigDecimalConverter` (which stores values as plain strings).

## Goals / Non-Goals

**Goals:**
- Balances are a pure function of stored rows: there is no stored total and no reversal arithmetic.
- All money arithmetic is in `Long` minor units, with `BigDecimal` used only for rates and the single rounding step in each conversion.
- A trip-currency switch is deterministic, atomic, and recomputed from each item's original amount.

**Non-Goals:**
- Editing existing expenses or payments (known issue 5). The rule for that later change is already decided and recorded here: editing keeps the item's stored `rateToTrip` and does **not** re-freeze it from current rates. Shares are re-allocated from the edited amount and sharers.
- Settle-up suggestions (known issue 8) and a manual rate override. The override is explicitly rejected.
- Multiple payers per expense.
- Using rates from the expense's date: only the latest rate is stored, so a backdated expense is converted at the rate on the day it is saved.

## Decisions

### 1. Schema (DB v2)

```kotlin
Trip(
    id, title, tag, status, createdAt,
    defaultCurrency: String,                 // NEW, one of the trip's ACTIVE TripCurrency codes
)

TripExpense(
    id, tripId, paidById, category, timestamp,
    amountMinor: Long,                       // original amount, in the original currency's minor units
    currency: String,                        // original currency
    rateToTrip: BigDecimal,                  // original -> trip currency; stored as a string via BigDecimalConverter
    tripAmountMinor: Long,                   // frozen total in the trip currency's minor units
)

ExpenseShare(                                // replaces ExpenseParticipantCrossRef
    expenseId, participantId,                // PK (expenseId, participantId)
    weight: Int,                             // multiplicator snapshot
    shareMinor: Long,                        // in trip currency; sum over the expense = tripAmountMinor
)

TripPayment(
    id, tripId, fromUserId, toUserId, timestamp,
    amountMinor: Long, currency: String,
    rateToTrip: BigDecimal, tripAmountMinor: Long,
)

ExchangeRate(currencyCode, baseCurrency, rate: BigDecimal, date)   // rate: Double -> BigDecimal
// ParticipantBalance: removed
```

Foreign keys: `Trip` keeps `CASCADE` to its children (deleting a trip deletes everything), and `ExpenseShare.expenseId` uses `CASCADE`. All foreign keys to `TripParticipant` (`paidById`, `fromUserId`, `toUserId`, `ExpenseShare.participantId`) change from `CASCADE` to `NO_ACTION`, Room's default. Participants are only ever made `INACTIVE`, so an accidental hard delete of a participant with history fails instead of silently rewriting history. `NO_ACTION` is used instead of `RESTRICT` because SQLite checks `NO_ACTION` at the end of the statement: deleting a trip cascades to its participants *and* to its expenses and payments, and that has to succeed whatever order the cascade runs in. `RESTRICT` would fail mid-cascade. Every participant foreign key column gets an index, which also fixes the KSP warnings in known issue 15.

The total is stored in `tripAmountMinor` even though it equals the sum of the shares. It is the payer's credit, and storing it keeps the balance query a simple `UNION ALL`. The Overview also uses it directly.

### 2. Money math: `MoneyMath` (a pure Kotlin object that replaces `BalanceCalculator`)

- `fractionDigits(code)` returns `Currency.getInstance(code).defaultFractionDigits`, falling back to 2 when the value is negative or the code is unknown.
- `parse(input, code): Long?` parses with `BigDecimal`. It rejects a non-positive amount or more decimal places than `fractionDigits` (the spec scenario). It never goes through `Double`.
- `format(minor, code)` is used for display. It replaces the current `formatAsCurrency` path for amounts.
- `crossRate(fromUsdRate, toUsdRate) = toUsdRate / fromUsdRate` with `MathContext.DECIMAL128`. CurrencyFreaks rates are "units per 1 USD", and USD is 1.
- `convert(amountMinor, from, to, rate)` returns `amountMinor` unchanged when `from == to`. Otherwise it returns `amountMinor × rate × 10^(digits(to) − digits(from))`, rounded `HALF_EVEN` to a `Long`.

**Alternative considered:** `BigDecimal` columns for amounts. They are rejected because `SUM` in SQLite would act on strings, and integer minor units make zero-sum a property of the arithmetic rather than a matter of care.

### 3. Share allocation: `ShareAllocator.allocate(total, sharers, payerId)`

Each share is `floor(total × weight / Σweights)`, computed in `Long` (products fit easily because totals are far below 2^63 / Σweights). The leftover, `total − Σfloors`, is between 0 and the number of sharers minus 1. All of it is added to a single recipient: the payer if sharing, otherwise the sharer with the lowest participant id (the earliest added). This was the user's choice over a largest-remainder method that spreads cents. It is simpler and still exact, and the payer is the person who actually handed over the full amount.

### 4. Freezing a rate on save

`TripItemRepository.saveExpense/savePayment` runs inside `db.withTransaction`. It loads the trip's `defaultCurrency`. If the item's currency is the trip currency, the rate is `1`. Otherwise it looks up both USD rates (through `ExchangeRateDao.getRateForCurrency`, with USD always available). If either is missing it returns `MissingExchangeRate` before writing anything. It then computes `rateToTrip` and `tripAmountMinor`, allocates the shares, and inserts the item and its shares. The weights come from the participants' current multiplicators at save time.

### 5. Deriving balances

There is one `BalanceDao` query over `trip_participants LEFT JOIN (UNION ALL of: payer +tripAmountMinor, sharer −shareMinor, payment from +, payment to −) GROUP BY participantId`. It returns `participantId, name, status, balanceMinor` for **every** participant of the trip, both active and inactive, with `COALESCE(…, 0)`. Room's invalidation tracker re-emits the `Flow` whenever `trip_expenses`, `expense_shares`, `trip_payments` or `trip_participants` change.

**Alternatives considered:** keeping a cache table (option C in the earlier discussion) is rejected, because at trip scale (hundreds of rows) the query costs well under 1 ms and a cache only adds failure modes. Doing the sum in Kotlin over the loaded entities is also rejected: it works, but SQL keeps it to one round trip and one source.

### 6. Changing the trip currency

This happens in `TripRepository.saveTrip`, which is rewritten to use `db.withTransaction` and so fixes the ineffective `@Transaction` described above. When `defaultCurrency` changes from A to B and the trip has items:

1. Look up the USD rates for A and B. If either is missing, abort the whole save with a `MissingExchangeRate` result. `AddTripViewModel` shows the existing "rates not available" snackbar and stays on the screen.
2. Compute `switch = crossRate(A, B)`.
3. For each expense and payment: if `currency == B`, set `rateToTrip = 1`. Otherwise set `rateToTrip = rateToTrip × switch` (`DECIMAL128`). Then set `tripAmountMinor = convert(amountMinor, currency, B, rateToTrip)`.
4. For each expense, re-allocate the shares from the stored weights with `ShareAllocator`, then update the rows.

Because step 3 always converts from the **original** amount using a composed rate, cents never compound across repeated switches. Only the rate's 34 significant digits accumulate, which is negligible. Items entered in B are exact by construction, as the spec requires.

### 7. UI

- **Add Trip:** the currency chips get a "default" marker (a selected or starred state). Tapping a chip's marker makes it the default. The default chip has no remove action. A new trip preselects the home currency, which is already pre-added there.
- **Add Expense / Add Payment:** the currency is preselected to the trip's `defaultCurrency`. Payment participants come from **all** trip participants, so removed ones can still settle up. Expense payer and sharers stay limited to active participants.
- **Balances:** `TsBalanceItem` shows `format(balanceMinor, tripCurrency)`, plus "≈ <home value>" when the home currency differs and both rates are present. Removed participants get a "removed" label. `BalanceViewModel` reads the home currency through `CurrencyPreference`, the same default-resolving source that Settings uses, which fixes known issue 16 here. It also converts with `BigDecimal` rates, not `BigDecimal(Double)`, which fixes known issue 20.
- **Expense and payment rows:** show the original amount and currency. When the currency differs from the trip currency, they also show the frozen trip-currency total.
- **Overview:** sums `tripAmountMinor` per category in the trip currency. The rate-related `UnavailableData` and `MissingCurrencies` states are removed, which also fixes the `!!` in known issue 21.

## Risks / Trade-offs

- [Wiping data on upgrade loses any test trips] → The app is unreleased. The destructive fallback is limited to version 1 only (`fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`), so a later missing migration fails loudly instead of silently wiping data.
- [The FX risk falls on whoever is owed after the save date] → This is the accepted product decision. The approximate home-currency value makes current-rate effects visible without changing balances.
- [A backdated expense gets today's rate] → This is documented in Non-Goals. The rate is only as good as the latest sync.
- [A trip-currency switch rewrites every item in the trip] → It is one transaction over at most a few hundred rows. It is deterministic and covered by tests (exact zero sum, exact round-trip for items entered in the trip currency).
- [Leftover cents always go to the payer] → The amount is at most (number of sharers − 1) minor units per expense, as the user chose.
- [The sync can return an unparseable or zero rate, which would cause a division by zero in `crossRate`] → `CurrencySyncWorker` skips entries whose rate does not parse to a positive `BigDecimal`, so the currency then counts as "missing".
- [The stricter participant foreign keys could break an existing hard-delete path] → `TripParticipantDao.deleteParticipant` and `deleteParticipantsByTripId` are removed. Trip deletion still works, because every child of the trip is removed in the same cascading statement and `NO_ACTION` is checked only at its end. An instrumented DAO test in task 1.4 verifies this.

## Migration Plan

1. Bump `TripSplitDatabase` to version 2, set `exportSchema = true`, and add the KSP argument `room.schemaLocation = $projectDir/schemas`. Commit the generated `2.json`.
2. Replace `fallbackToDestructiveMigration(false)` with `fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`.
3. Rollback: none is needed for an unreleased app. Reinstalling the previous APK requires clearing app data.
4. Before the first release: decide whether to keep or drop the v1 fallback. Every version after 2 must ship a `Migration` with a Room migration test.

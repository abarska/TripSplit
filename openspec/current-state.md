# TripSplit: current state (baseline before OpenSpec-driven changes)

Snapshot of `main` at commit `cb59870`, based on reading the code. It was not built or run. Items marked *(inferred)* come from reading code paths, not from observed behaviour.

## 1. What it is

A single-module, offline-first Android app (Kotlin, Jetpack Compose, Material 3) for splitting shared trip expenses between friends. Package `com.anabars.tripsplit`, minSdk 24, target/compile SDK 36, version 1.0. About 285 commits from 2025-05 to 2025-09. It is a one-person project with no backend. All trip data is local (Room).

## 2. Tech stack

| Area | Choice |
|---|---|
| UI | Compose BOM 2025.08, Material 3, Navigation Compose 2.9.3, Google Fonts |
| Architecture | MVVM with a partial MVI style (`onIntent`, `UiState`, `UiEffect`). ViewModels talk to repositories, repositories talk to DAOs |
| DI | Hilt 2.57, `hilt-work`, `hilt-navigation-compose` |
| Persistence | Room 2.7.2 (DB v1, `exportSchema=false`, `fallbackToDestructiveMigration(false)`), DataStore Preferences |
| Network | Retrofit 3 + Moshi to CurrencyFreaks (`v2.0/rates/latest`) |
| Background | WorkManager (custom `Configuration.Provider` with `HiltWorkerFactory`) |
| Build | AGP 8.12, Kotlin 2.0.21, KSP, version catalog, core-library desugaring |
| Tests | JUnit4, Mockito-Kotlin, Truth, coroutines-test |

**Build prerequisites (not in the repo):** `CURRENCY_FREAKS_API_KEY` in `local.properties`, because the Gradle build fails if it is missing. `app/google-services.json` is gitignored.

## 3. Domain model (Room, 8 tables)

- **Trip**: `id`, `title`, `tag` (string, default `OTHER`), `status` (PLANNED, STARTED, FINISHED, ARCHIVED), `ownerId`, `createdAt`.
- **TripParticipant**: `name`, `status` (ACTIVE or INACTIVE), `multiplicator` (int, default 1, a share weight), `tripId`. Participants are soft-deleted (made INACTIVE) so that history stays valid.
- **TripCurrency**: `code`, `tripId`, `status` (ACTIVE or INACTIVE), also soft-deleted.
- **TripExpense**: `paidById`, `amount` (Double), `currency`, `category` (Transportation, Accommodation, Food, Activities, Miscellaneous), `timestamp`, `tripId`.
- **ExpenseParticipantCrossRef**: many-to-many link recording who an expense was split between.
- **TripPayment**: a settlement transfer. `fromUserId`, `toUserId` (exactly one payee), `amount`, `currency`, `timestamp`.
- **ParticipantBalance**: `(tripId, participantId)` as the primary key, with a running `amountUsd` (BigDecimal). It is **denormalised and updated incrementally**, not derived from expenses.
- **ExchangeRate**: `currencyCode` as the primary key, with `rate` relative to USD, the `baseCurrency` and a date. It is global, not per trip. The whole table is replaced on each sync.

All foreign keys cascade on delete. Converters exist for `BigDecimal`, `Date` and `ExpenseCategory`.

## 4. Implemented features

**Trips list (start screen).** Shows PLANNED, STARTED and FINISHED trips, grouped by status. The sort direction can be toggled. Each row has an archive button. A FAB opens the Add Trip screen.

**Add / Edit Trip.**
- Fields are title (auto-capitalised), status, participants and currencies.
- Participants have a name and a multiplicator (a "pays for N" weight). They can be added, edited and removed. Duplicate names trigger a warning dialog.
- Currencies are chosen from a searchable list of ISO codes derived from available locales.
- The user's "home" currency is pre-added to new trips.
- A back press with unsaved changes asks whether to save or discard.
- Removing a participant or currency only marks it INACTIVE. Re-adding it reactivates the existing row.
- Editing is reached from the Trip Overview toolbar.

**Trip Details.** A horizontal pager with a Material 3 bottom `NavigationBar` and three tabs.
- *Expenses*: grouped by date, newest first, with delete. The FAB adds a new one.
- *Payments*: the same layout for settlement payments.
- *Balances*: net balance per participant, converted to the user's home currency. The currency chips shown below the list are **display-only** (their selection callback is empty).
- The toolbar has an info action that opens Trip Overview.

**Add Expense / Add Payment.** Both screens share a base (`AddItemScreenBase`), with separate portrait and landscape layouts.
- An expense has a date, amount, currency, category, payer and a set of participants (all selected by default).
- A payment has a date, amount, currency, a payer (from) and one payee (to).
- Validation covers a positive amount, a payer, and at least one participant or payee. Errors show in a snackbar and highlight the card.
- Unsaved-changes detection triggers a discard dialog.

**Delete expense / payment.** The balance effect is reversed and the item is removed, all in one DB transaction.

**Trip Overview.** A summary card and a category pie chart with a legend, in portrait and landscape layouts. It converts every expense to USD using the stored rates. It shows loading, "rates unavailable" and "missing rates for X" states.

**Archive.** Lists ARCHIVED trips. Tapping one opens its details. Trips are archived from the Trips list.

**Settings.** A single setting: local (home) currency. It is stored in DataStore and defaults to the SIM country's currency, then the locale's, then `UAH`.

**Exchange-rate sync.** `CurrencySyncScheduler` enqueues a unique one-time work on startup (`KEEP`, network required, 10-minute exponential backoff) and a unique periodic daily work at about 01:00 (1-hour backoff). The worker fetches rates for every currency code found in the available locales and replaces the table. On error it logs and retries.

**Navigation.** Routes are `trips`, `add_trip[/{tripId}]`, `trip_details/{tripId}`, `trip_overview/{tripId}`, `add_expense/{tripId}/{useCase}`, `add_payment/{tripId}/{useCase}`, `settings` and `archive`. A modal drawer links Trips, Settings and Archive. A `SharedViewModel` drives the toolbar title, toolbar actions, FAB visibility, the selected tab and snackbars from the current route.

## 5. Balance logic (the core)

- Everything is stored in **USD**. `amountUsd = amount / rate(currency)`, rounded to 2 decimals (HALF_UP) per delta.
- For an expense, the payer gets `+amount`. Each participant gets `-(amount / Σmultiplicators) × their multiplicator`. Deltas are grouped per participant.
- For a payment, `from` gets `+amount` and `to` gets `-amount`.
- Saving an item writes the item, its cross-refs and its balance deltas in one transaction (`db.withTransaction`). Deleting recalculates the deltas and applies their negation.
- The Balances tab multiplies the USD balances by the home-currency rate for display.

## 6. Tests

- `BalanceCalculatorTest` has 4 real tests, covering expense split, multiplicators, payments and exchange-rate conversion.
- `TripItemRepositoryTest` has mocks and setup but **no test cases**.
- `ExampleUnitTest` and `ExampleInstrumentedTest` are template stubs.
- There are no ViewModel, DAO or UI tests.

## 7. Observations, risks and gaps

These are things to be aware of before changing anything.

1. **Firebase is configured but unused.** The `google-services` plugin and the `firebase-auth` and `firebase-firestore` dependencies are present, but no code uses them. `Trip.ownerId` is also never set. This looks like a planned sync or sharing feature that was never built.
2. **Crash risk when exchange rates are missing.** `ExchangeRateDao.getExchangeRateForCurrency` returns a non-null `ExchangeRate`. Saving or deleting a non-USD item before the first sync would throw. `BalanceViewModel` has the same problem when the home currency is not USD, and it does not catch the error *(inferred)*.
3. **Balance drift.** Balances are incremental and use the **current** exchange rate at save and at delete. Deleting an old expense later reverses a *different* USD amount than was originally added. Changing a participant's multiplicator after expenses exist also makes later reversals inconsistent. There is no "recompute from scratch" path *(inferred)*.
4. **Rounding.** Per-delta rounding means the shares of an expense may not sum exactly to zero, which can leave cent-level residue *(inferred)*.
5. **No editing of existing expenses or payments.** Only add and delete exist, even though the `UseCase` and route design leaves room for edit. `TripDao.deleteTrip` and `deleteAllTrips` have no UI path. Archiving is available, but unarchiving is only possible by editing the trip status *(inferred)*.
6. **Editing a trip resets some fields.** `AddTripViewModel.saveTrip` builds a fresh `Trip(title, status)` and does `copy(id=…)`, which discards `tag`, `ownerId` and `createdAt`. The tag is also never selectable in the UI.
7. **Amounts are `Double`** in `TripExpense` and `TripPayment`, while balances use `BigDecimal`.
8. **The "who paid" model is single-payer** for both expenses and payments. There is no settle-up suggestion, so the app records payments but does not tell users who owes whom.
9. **Home-currency balances are not reactive.** `BalanceViewModel` reads the home currency and rate once at init, so a change in Settings does not update an open screen *(inferred)*.
10. **Hardcoded strings.** There is a leftover debug log tag (`"marysya"`) and an unused `under_construction` string. Some UI strings may be non-localised.
11. **Release config.** `isMinifyEnabled = false`, Room schema export is off, and there are no migrations. The first schema change will either destroy data or need a migration.
12. **The CurrencyFreaks API key** is compiled into `BuildConfig`, so it ships inside the APK.
13. **The snackbar collection is duplicated.** Both `MainActivity` and `CollectNavigationEffects` collect `uiEffect`. Each has its own `SnackbarHostState`, and only the one in `MainActivity` is attached to the Scaffold *(inferred)*.

## 8. Recent direction (git log)

The latest work is UI and navigation refactoring: moving Overview from a bottom tab to a toolbar action, migrating `TabRow` to `NavigationBar`, extracting navigation helpers, and making the nav args the single source of truth for `tripId`. Before that came balance calculation, delete and recalculation, and the first tests.

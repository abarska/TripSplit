# Proposal

## Why

Balances are kept as running USD totals. Every save adds a delta, and every delete subtracts a delta that is recomputed with the current exchange rate and the current multiplicators. Rounding is done separately per delta. As a result, balances drift away from the expense history and may not sum to zero (`current-state.md` items 7.3, 7.4 and 7.7). Every later feature (editing, settle-up, overview) depends on balances being exactly right, so this needs fixing first, while the app is unreleased and its database can still be wiped.

## What Changes

- **Trip default currency.** Each trip gets a default currency, chosen from its currencies, that all its balances are kept in. It can be changed at any time. Existing amounts are then converted at the rate on the day of the switch, except items originally entered in the new currency, which keep their exact amount.
- **Frozen conversion.** When an expense or payment is saved, its original amount and currency, the rate to the trip currency, and the converted total are stored. Later rate syncs never change a saved item. A trip-currency switch recomputes the stored values from the originals rather than from the previous conversion.
- **Snapshotted, exact splits.** Each expense stores every sharer's weight and share in integer minor units of the trip currency. The shares always sum exactly to the converted total. Any leftover minor units go to the payer if they share the expense, otherwise to the earliest-added sharer.
- **Derived balances.** Balances are computed from expenses, shares and payments by a query. **BREAKING:** the `participant_balances` table and the incremental delta logic are removed. Deleting an item removes its rows, with nothing to reverse, so deleting no longer needs an exchange rate.
- **Integer money.** Amounts are stored as `Long` minor units, with the scale taken from the currency, and rates as decimal strings. **BREAKING:** `Double` amounts and the `Double` exchange rate are removed.
- **Balances display.** Balances are shown in the trip currency, which is exact and stable. If the home currency differs, an approximate home-currency value is also shown, converted at the current rate.
- **Removed participants.** A removed (inactive) participant's past shares keep counting. They no longer appear in new expenses, but they stay on the Balances tab, marked as removed, and can still pay or be paid.
- **Trip Overview** totals use the frozen trip-currency amounts and no longer need exchange rates.
- **Database.** The version goes to 2 with a one-time destructive migration. **BREAKING:** existing local data is wiped. Room schema export is turned on so that later versions can have tested migrations.

## Capabilities

### New Capabilities
- `trip-balances`: how expenses and payments are recorded in a trip's currency (frozen conversion, exact weighted split, leftover allocation), how balances are derived and displayed, and how removed participants are treated.
- `trip-default-currency`: choosing a trip's default currency, and converting existing items when it changes.

### Modified Capabilities
- `exchange-rate-availability`: saving now needs a rate for both the item's currency and the trip currency. Deleting no longer needs any rate. The Balances tab always shows trip-currency balances, and a missing rate hides only the approximate home-currency value.

## Impact

- **Room entities:** `Trip` (new `defaultCurrency`), `TripExpense` and `TripPayment` (`amountMinor`, `rateToTrip`, `tripAmountMinor`), `ExpenseParticipantCrossRef` replaced by `ExpenseShare`, `ParticipantBalance` removed, `ExchangeRate.rate` changed from `Double` to `String`. `TripSplitDatabase` goes to v2 with `exportSchema = true`, and `DatabaseModule` gets the destructive fallback for the move from v1.
- **Code:** `BalanceCalculator` is replaced by a pure split and conversion helper, and `BalanceDao`, `BalanceRepository`, `TripItemRepository` and `TripRepository` are reworked. The ViewModels affected are `AddTripViewModel` (default currency picker), `AddItemViewModel` (decimal input to minor units), `BalanceViewModel` and `TripOverviewViewModel`. The affected UI is the Add Trip screen, the Balances tab, and the expense and payment rows. The currency sync worker changes because rates are now stored as strings.
- **Tests:** `BalanceCalculatorTest` is rewritten, and new tests cover allocation, currency switching, the balance query and the ViewModels.
- **Data:** every existing local trip is lost on upgrade. This is acceptable because the app is unreleased.
- **Known-issues list:** this resolves items 3, 4 and 7, and partly 11, 15 and 21 (see the auto-memory list).

# Tasks

## 1. Database v2 schema

- [x] 1.1 Change the entities as in design §1. Add `Trip.defaultCurrency`. Give `TripExpense` and `TripPayment` `amountMinor`, `rateToTrip` and `tripAmountMinor`. Replace `ExpenseParticipantCrossRef` with `ExpenseShare(expenseId, participantId, weight, shareMinor)`. Remove `ParticipantBalance`, and change `ExchangeRate.rate` to `BigDecimal`. Set the participant foreign keys to `NO_ACTION` and index every participant foreign key column. Remove `TripParticipantDao.deleteParticipant` and `deleteParticipantsByTripId`. Verify that `./gradlew :app:compileDebugKotlin` compiles, apart from call sites that later groups replace, which may be stubbed with `TODO()` in this commit only, and that the KSP foreign-key-index warnings are gone
- [x] 1.2 Bump `TripSplitDatabase` to version 2 with `exportSchema = true`. Add the KSP argument `room.schemaLocation` pointing at `app/schemas`, and replace the fallback with `fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`. Verify that `app/schemas/.../2.json` is generated and committed
- [x] 1.3 Make `CurrencySyncWorker` store rates as `BigDecimal` and skip entries that don't parse to a positive value, and remove the `"marysya"` log tag. Make `ExchangeRateDao.getRateForCurrency` and `getRateFlowForCurrency` return `BigDecimal?`, with USD returning `ONE`. Verify with a unit test of the parsing step (a valid rate is stored, while `"abc"` and `"0"` are skipped)
- [x] 1.4 Add an in-memory Room instrumented test (androidTest) that inserts a trip with participants, an expense with shares and a payment, then deletes the trip. Verify that it passes on the Galaxy A05s and that every child table is empty afterwards

## 2. Money math and share allocation

- [x] 2.1 Add `MoneyMath` (`fractionDigits`, `parse`, `format`, `crossRate`, `convert`) as in design §2. Verify with unit tests: EUR has 2 digits, JPY 0 and KWD 3. `parse("12.345", EUR)` is rejected, while `parse("12.34", EUR)` gives 1234. 100.00 USD at 0.9137 gives 9137 EUR minor units. A same-currency `convert` is the identity. Halves round to even
- [x] 2.2 Add `ShareAllocator.allocate` as in design §3. Verify with unit tests covering every spec scenario in the "Expense split is snapshotted and exact" and "Leftover minor units go to a defined participant" requirements, plus a property-style test that the shares always sum to the total over randomized totals and weights
- [x] 2.3 Delete `BalanceCalculator`, `BalanceDelta` and `BalanceCalculatorTest`. Verify that `./gradlew :app:testDebugUnitTest` passes

## 3. Recording expenses and payments

- [x] 3.1 Rewrite `TripItemRepository.saveExpense` and `savePayment` (design §4): load the trip currency, freeze `rateToTrip` and `tripAmountMinor`, snapshot the weights, allocate the shares, and return `MissingExchangeRate` before any write when a needed rate is absent. Verify with `TripItemRepositoryTest` cases for each scenario in the modified "Saving requires an available exchange rate" requirement and for "Foreign-currency expense" and "Expense in the trip currency"
- [x] 3.2 Make `deleteExpenseById` and `deletePaymentById` delete the rows only, with no rate lookup, and remove `MissingExchangeRate` handling from the delete path in `TripItemViewModel`. Verify with a repository test that a delete with an empty rate table succeeds
- [x] 3.3 Update `ExpenseWithParticipants` to relate through `ExpenseShare`, and add `@Transaction` to `getExpenseWithParticipantsById`. Verify that the Expenses tab still lists the sharers on the device

## 4. Derived balances

- [x] 4.1 Replace `BalanceDao` with the derived query from design §5, which returns `participantId`, `name`, `status` and `balanceMinor` for every participant, and adapt `BalanceRepository`. Verify with an instrumented in-memory Room test of the spec's "Delete after rate and weight changes" scenario: A −15.00, B 0.00 and C +15.00, summing to zero. Also test "Payment settles a debt", and that a removed participant still appears with their balance

## 5. Trip default currency

- [x] 5.1 Rewrite `TripRepository.saveTrip` to use `db.withTransaction` instead of the ineffective `@Transaction`, and persist `defaultCurrency`. Verify with repository unit tests that the default currency is saved and that a failure partway through the save writes nothing
- [x] 5.2 Implement the currency switch (design §6) inside `saveTrip`, returning `MissingExchangeRate` with nothing written when either rate is missing. Verify with unit tests of the spec scenarios "Switch with mixed items" (77.66, 20.00 and 255.00 GBP), "Shares and balances follow the switch", "Switching back keeps items entered in the trip currency exact", "Switch without a rate" and "Switch on an empty trip"
- [x] 5.3 Update `AddTripViewModel` and the Add Trip UI: add a default marker to the currency chips, preselect the home currency for a new trip, block removing the default currency, and show the "rates not available" snackbar when the switch is refused. Verify with ViewModel unit tests, and on the device by creating a trip, making EUR the default, and confirming that EUR can't be removed

## 6. Add Expense / Add Payment

- [ ] 6.1 Preselect the trip's `defaultCurrency` in `AddItemViewModel`. Parse the amount with `MoneyMath.parse` and show a validation error for too many decimals. Offer all participants, including removed ones, as payment payer and payee, and only active participants for expenses. Verify with ViewModel unit tests for the preselection, the "Too many decimals entered" scenario and the participant lists

## 7. Balances tab

- [ ] 7.1 Rework `BalanceViewModel` to show trip-currency balances (always available) plus an approximate home-currency value, using `CurrencyPreference` for the home currency and `BigDecimal` rates. Values are hidden when a rate is missing and appear when it arrives. Verify with unit tests for each scenario in the modified "Balances display tolerates a missing home-currency rate" requirement and in "Balances are shown in the trip currency with an approximate home-currency value"
- [ ] 7.2 Update `TsBalanceItem` and `TripBalancesTab` to show the trip-currency value, the "≈ home" value and a "removed" label. Verify on the device in airplane mode on a fresh install (EUR balances shown without approximate values, no crash) and after a sync (approximate values appear)

## 8. Item rows and Trip Overview

- [ ] 8.1 Show the original amount and currency in `TsExpenseItemRow` and `TsPaymentItemRow`, plus the frozen trip-currency total when it differs. Update `FakePreviewData`. Verify in the Compose previews and on the device
- [ ] 8.2 Make `TripOverviewViewModel` sum `tripAmountMinor` per category in the trip currency, and remove the `UnavailableData` and `MissingCurrencies` states and their UI. Verify with a ViewModel unit test, and on the device with an empty rate table (the Overview still shows totals)

## 9. End-to-end check and docs

- [ ] 9.1 On the Galaxy A05s, in one trip: add a USD expense in an EUR trip, wait for or force a rate sync, change a multiplicator, delete the expense, and confirm that the balances return exactly to their previous values and sum to zero. Then switch the trip to GBP and back to EUR, and confirm that the EUR-entered items show their exact original amounts
- [ ] 9.2 Update `openspec/current-state.md`: sections 3 and 5, mark items 7.3, 7.4 and 7.7 as resolved and 7.11 as partly resolved, and update section 6 for the new tests. Update the Purpose of `openspec/specs/exchange-rate-availability/spec.md` so that it no longer says amounts are converted "to USD". Verify that both match the implemented behaviour

## Workflow follow-up

- After merging, archive the change with `/opsx:archive` and check that the main specs `trip-balances` and `trip-default-currency` are created and that `exchange-rate-availability` is updated.
- Update the auto-memory known-issues list (items 3, 4 and 7 resolved; 11, 15, 16, 20 and 21 partly or fully resolved).

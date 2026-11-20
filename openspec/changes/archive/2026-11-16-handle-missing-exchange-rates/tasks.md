# Tasks

## 1. Repository and DAO

- [x] 1.1 Make `ExchangeRateDao.getExchangeRateForCurrency` return `ExchangeRate?`; verify `./gradlew :app:compileDebugKotlin` succeeds
- [x] 1.2 Add a sealed result type and change the `TripItemRepository` save/delete methods to return it, returning `MissingExchangeRate` before any DB write; verify with new `TripItemRepositoryTest` cases (missing rate on save and on delete leaves DAOs untouched; USD succeeds with an empty rate table; non-USD with a stored rate succeeds)

## 2. Save and delete feedback

- [x] 2.1 Add the new string resource in `strings.xml` and update `AddItemViewModel.saveItem` to show a snackbar and stay on the form on `MissingExchangeRate`; verify with a ViewModel unit test that `ShowSnackBar` is emitted and `NavigateBack` is not
- [x] 2.2 Add a snackbar effect flow to `TripItemViewModel` for refused deletes and collect it in the trip-details screen; verify with a ViewModel unit test, and on the Galaxy A05s by deleting a non-USD item with an empty rate table (no crash, item remains, message shown)

## 3. Balances tab

- [x] 3.1 Rework `BalanceViewModel` to combine balances, the rate flow and the home currency, expose a rates-available flag, and not throw when the home rate is missing; verify with unit tests for a missing rate, a rate arriving later, and a USD home currency
- [x] 3.2 Show `placeholder_no_exchange_rates_available` in `TripBalancesTab` when rates are unavailable; verify on the Galaxy A05s with a fresh install in airplane mode (no crash, placeholder shown, balances appear after reconnecting and syncing)

## 4. Docs

- [x] 4.1 Mark item 7.2 resolved in `openspec/current-state.md`; verify the entry matches the implemented behavior

# Design

## Context

All conversion goes through `TripItemRepository.getExchangeRateForCurrency` (USD short-circuits to 1.0, everything else calls the DAO) and, separately, `BalanceViewModel.loadBalances`, which calls the DAO directly. The DAO returns a non-null entity, so Room throws for a missing row. Callers (`AddItemViewModel.saveItem`, `TripItemViewModel.delete*`) launch in `viewModelScope` without a catch. `AddItemViewModel` already has a snackbar effect (`AddItemUiEffect.ShowSnackBar`); `TripItemViewModel` has no effect channel. See proposal.md for motivation.

## Goals / Non-Goals

**Goals:**
- A missing rate is an expected, typed outcome, not an exception.
- Refused operations are atomic: no partial writes.
- The Balances screen recovers by itself after a sync.

**Non-Goals:**
- Fallback or cached rates, manual rate entry, storing the rate per item (balance drift, 7.3).
- Reacting to home-currency changes in Settings (7.9).

## Decisions

1. **DAO returns `ExchangeRate?`.** Room supports nullable single-row returns. Alternative: wrap each caller in try/catch. Rejected: it hides the real cause and is easy to forget.
2. **Repository save/delete signal refusal via a result, not an exception.** `savePayment`, `saveExpenseWithParticipants`, `deleteExpenseById` and `deletePaymentById` return a small sealed result (`Success` / `MissingExchangeRate`). Alternative: a custom exception caught in the ViewModels. A result type makes the failure explicit at the call site and is easy to assert in tests. For saves the rate is resolved before `db.withTransaction` opens; for deletes it is resolved inside the transaction and the block returns early before any write, so nothing is mutated.
3. **User feedback.** `AddItemViewModel` emits the existing `ShowSnackBar` with a new string and does not navigate back. `TripItemViewModel` gets a `SharedFlow` of snackbar string ids that the trip-details screen forwards to the existing `onShowSnackbar` (same pattern as `AddItemScreenBase`). Alternative: disable Save when no rate exists. Rejected: needs reactive rate state in the form and hides the reason.
4. **BalanceViewModel observes rates.** Replace the one-shot DAO read with a `combine` of the balances flow and `exchangeRateDao.getExchangeRatesFlow()`; look up the home rate in the list (USD = 1.0). UI state gains a rates-available flag and the tab shows the existing `placeholder_no_exchange_rates_available` text when false. Alternative: only null-check once at init. Rejected: the user would stay on the placeholder after the sync completes. The home currency is still read once, so 7.9 stays untouched.

## Risks / Trade-offs

- [Delete blocked for a non-USD item whose rate is missing] → Rates are only replaced wholesale by sync, so this is rare; the message says it will work once rates load.
- [Changing repository return types touches callers and tests] → Only two ViewModels call them; update them in the same change.
- [Collecting the whole rate list in BalanceViewModel] → The table is small and the flow emits only on sync.

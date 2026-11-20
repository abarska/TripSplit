# Proposal

## Why

`ExchangeRateDao.getExchangeRateForCurrency` is declared to return a non-null `ExchangeRate`, but the table is empty until the first successful sync (and a currency may be absent from a later sync). Saving or deleting a non-USD expense or payment in that state throws inside a coroutine and crashes the app. `BalanceViewModel` has the same flaw when the home currency is not USD (the default for most users), so the Balances tab can crash on first launch while offline. See `openspec/current-state.md` section 7.2.

## What Changes

- Make the "rate for one currency" lookup nullable so a missing rate is representable instead of an exception.
- Saving or deleting an expense or payment whose currency has no stored rate is refused without touching the database, and the user is told why via a snackbar.
- The Balances tab no longer crashes when the home-currency rate is missing; it shows the existing "exchange rates not available" placeholder and recovers automatically once rates are synced.
- Add unit tests for the refused-save, refused-delete and missing-home-rate paths.

## Capabilities

### New Capabilities
- `exchange-rate-availability`: how balance-affecting actions (save, delete, display) behave when a required exchange rate is not available.

### Modified Capabilities

None. `openspec/specs/` holds no active specs yet.

## Impact

- `ExchangeRateDao`, `TripItemRepository`, `BalanceViewModel`, `AddItemViewModel`, `TripItemViewModel`, `TripBalancesTab`, `strings.xml`.
- `TripItemRepositoryTest` (currently has no cases) gets real tests.
- No schema change, no migration, no new dependencies.
- Out of scope: balance drift (7.3), making the home currency reactive to Settings (7.9), manual rate entry.

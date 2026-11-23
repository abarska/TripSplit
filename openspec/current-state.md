# TripSplit: current state (after the `update-dependencies` change)

Snapshot of branch `update-dependencies` at commit `a3b266a`, the last code commit of the change's 13 commits on top of `main` at `ef06a50` (whose code is unchanged from `cb59870`, the earlier baseline). The tech stack, build and domain sections were updated from the build files and the change's tasks, which were smoke-tested on a real Galaxy A05s (SM-A057G, Android 15). The remaining sections still come from reading the code at `cb59870` and were not re-verified. Items marked *(inferred)* come from reading code paths, not from observed behaviour.

## 1. What it is

A single-module, offline-first Android app (Kotlin, Jetpack Compose, Material 3) for splitting shared trip expenses between friends. Package `com.anabars.tripsplit`, minSdk 24, target SDK 36, compile SDK 37, version 1.0. About 299 commits from 2025-05 to 2026-11. It is a one-person project with no backend. All trip data is local (Room).

## 2. Tech stack

| Area | Choice |
|---|---|
| UI | Compose BOM 2026.09.00, Material 3, Navigation Compose 2.10.2, Google Fonts |
| Architecture | MVVM with a partial MVI style (`onIntent`, `UiState`, `UiEffect`). ViewModels talk to repositories, repositories talk to DAOs |
| DI | Hilt 2.60.1, `androidx.hilt` 1.4.0 (`hilt-work`, `hilt-lifecycle-viewmodel-compose`) |
| Persistence | Room 2.8.5 (DB v2, `exportSchema=true` with schemas in `app/schemas`, `fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`), DataStore Preferences 1.2.1 |
| Network | Retrofit 3.0.0 + Moshi 1.15.2 to CurrencyFreaks (`v2.0/rates/latest`) |
| Background | WorkManager 2.12.0 (custom `Configuration.Provider` with `HiltWorkerFactory`) |
| Build | AGP 9.4.1 (built-in Kotlin), Kotlin 2.4.21, KSP 2.3.12, Gradle 9.8.1, version catalog, core-library desugaring |
| Tests | JUnit4, Mockito 5.24 and Mockito-Kotlin 6.4, Truth 1.4.5, coroutines-test 1.11 |

**Build prerequisites (not in the repo):** `CURRENCY_FREAKS_API_KEY` in `local.properties`, because the Gradle build fails if it is missing. `google-services.json` is no longer required (Firebase was removed). The `INTERNET` permission is now declared explicitly in the manifest, because it used to be merged in from Firebase and Play Services.

## 3. Domain model (Room, 8 tables, DB v2)

- **Trip**: `id`, `title`, `tag` (string, default `OTHER`), `status` (PLANNED, STARTED, FINISHED, ARCHIVED), `createdAt`, `defaultCurrency` (the trip currency, one of the trip's active currencies).
- **TripParticipant**: `name`, `status` (ACTIVE or INACTIVE), `multiplicator` (int, default 1, a share weight), `tripId`. Participants are soft-deleted (made INACTIVE) so that history stays valid.
- **TripCurrency**: `code`, `tripId`, `status` (ACTIVE or INACTIVE), also soft-deleted. The trip's default currency cannot be removed.
- **TripExpense**: `paidById`, `amountMinor` (Long, in the original currency's minor units), `currency` (original), `rateToTrip` (BigDecimal, frozen at save), `tripAmountMinor` (Long, the frozen total in the trip currency), `category` (Transportation, Accommodation, Food, Activities, Miscellaneous), `timestamp`, `tripId`.
- **ExpenseShare**: `(expenseId, participantId)` as the primary key, with `weight` (a snapshot of the multiplicator) and `shareMinor` (in the trip currency). The shares of an expense always sum to its `tripAmountMinor`. It replaces `ExpenseParticipantCrossRef`.
- **TripPayment**: a settlement transfer. `fromUserId`, `toUserId` (exactly one payee), `amountMinor`, `currency`, `rateToTrip`, `tripAmountMinor`, `timestamp`.
- **ExchangeRate**: `currencyCode` as the primary key, with `rate` (BigDecimal, units per 1 USD), the `baseCurrency` and a date. It is global, not per trip. The whole table is replaced on each sync, and entries that don't parse to a positive rate are skipped.

`ParticipantBalance` no longer exists: balances are derived (section 5). Foreign keys from a trip to its children, and from `ExpenseShare` to its expense, cascade on delete. Foreign keys to `TripParticipant` are `NO_ACTION` and indexed, so a hard delete of a participant with history fails. Converters exist for `BigDecimal`, `Date` and `ExpenseCategory`. The v1 to v2 step is destructive (an install made before this change loses its data), and every later version needs a real migration.

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

- Money is `Long` minor units. `MoneyMath` handles `fractionDigits`, `parse` (rejects too many decimals), `format`, `crossRate` and `convert` (`HALF_EVEN`, a single rounding step). No amount goes through `Double`.
- On save, the item's `rateToTrip` and `tripAmountMinor` are frozen from the stored USD rates (cross rate to the trip currency, `1` when the item is in the trip currency). If a needed rate is missing, the save returns `MissingExchangeRate` before anything is written.
- `ShareAllocator` splits an expense: each share is `floor(total × weight / Σweights)`, and the leftover minor units go to the payer if they share, otherwise to the sharer with the lowest participant id. Shares and weights are snapshotted in `ExpenseShare`, so later multiplicator changes don't affect them.
- Balances are **derived**, not stored. One `BalanceDao` query sums the payer's `+tripAmountMinor`, each sharer's `−shareMinor`, and payments (`from` +, `to` −) for every participant, active or removed, and Room re-emits it when the underlying tables change. Balances always sum to zero.
- Deleting an expense or payment deletes its rows only. It needs no rate, and the balances follow.
- Changing the trip currency (inside `TripRepository.saveTrip`, in one `db.withTransaction`) recomputes every item's `rateToTrip` and `tripAmountMinor` from its original amount and re-allocates the shares. It is refused with `MissingExchangeRate`, writing nothing, if either rate is missing. Items entered in the new trip currency come out exact.
- The Balances tab shows the trip-currency value, plus an approximate "≈ home" value when the home currency (from `CurrencyPreference`) differs and both rates are present. Overview sums `tripAmountMinor` per category in the trip currency and doesn't depend on rates.

## 6. Tests

- Unit tests (JUnit4): `MoneyMathTest`, `ShareAllocatorTest` (including a randomized sum-to-total test), `CurrencySyncWorkerTest` (rate parsing), `TripItemRepositoryTest`, `TripRepositoryTest` (default currency, atomic save, currency switch scenarios), and ViewModel tests for `AddItemViewModel`, `AddTripViewModel`, `BalanceViewModel`, `TripItemViewModel` and `TripOverviewViewModel`.
- Instrumented tests (in-memory Room, run on the Galaxy A05s): `BalanceDaoTest` (the "delete after rate and weight changes" scenario, payments, removed participants) and `TripDeletionCascadeTest` (deleting a trip empties every child table).
- The drift-free scenario (USD expense in an EUR trip, rate sync, multiplicator change, delete, then a switch to GBP and back to EUR) was also verified by hand on the device.
- `ExampleUnitTest` and `ExampleInstrumentedTest` are still template stubs. There are no DAO tests beyond the two above and no UI tests.

## 7. Observations, risks and gaps

These are things to be aware of before changing anything.

1. ~~**Firebase is configured but unused.**~~ **Resolved:** the `google-services` plugin, the Firebase dependencies and `Trip.ownerId` were removed in the `update-dependencies` change.
2. ~~**Crash risk when exchange rates are missing.**~~ **Resolved:** in the `handle-missing-exchange-rates` change, `ExchangeRateDao.getExchangeRateForCurrency` returns `ExchangeRate?`; saving or deleting a non-USD item without a stored rate is refused before any write (typed `MissingExchangeRate` result) and a snackbar explains why. `BalanceViewModel` observes the rate flow and the Balances tab shows `placeholder_no_exchange_rates_available` until rates sync.
3. ~~**Balance drift.**~~ **Resolved** in the `drift-free-balances` change: balances are derived from frozen per-item rates and share snapshots (section 5). Previously: balances are incremental and use the **current** exchange rate at save and at delete. Deleting an old expense later reverses a *different* USD amount than was originally added. Changing a participant's multiplicator after expenses exist also makes later reversals inconsistent. There is no "recompute from scratch" path *(inferred)*.
4. ~~**Rounding.**~~ **Resolved** in the `drift-free-balances` change: shares are allocated exactly in minor units and always sum to the item total. Previously: per-delta rounding meant the shares of an expense may not sum exactly to zero, which can leave cent-level residue *(inferred)*.
5. **No editing of existing expenses or payments.** Only add and delete exist, even though the `UseCase` and route design leaves room for edit. `TripDao.deleteTrip` and `deleteAllTrips` have no UI path. Archiving is available, but unarchiving is only possible by editing the trip status *(inferred)*.
6. **Editing a trip resets some fields.** `AddTripViewModel.saveTrip` builds a fresh `Trip(title, status)` and does `copy(id=…)`, which discards `tag` and `createdAt`. The tag is also never selectable in the UI.
7. ~~**Amounts are `Double`** in `TripExpense` and `TripPayment`, while balances use `BigDecimal`.~~ **Resolved** in the `drift-free-balances` change: amounts are `Long` minor units and rates are `BigDecimal`.
8. **The "who paid" model is single-payer** for both expenses and payments. There is no settle-up suggestion, so the app records payments but does not tell users who owes whom.
9. ~~**Home-currency balances are not reactive.**~~ **Resolved:** on the `handle-missing-exchange-rates` branch, after the change was archived, `BalanceViewModel` builds its state with `stateIn` from the balances flow and the home-currency preference, and watches that currency's rate through `ExchangeRateDao.getRateFlowForCurrency`. A change in Settings or a rate sync updates an open Balances tab. The fallback is still `BASE_CURRENCY` (USD) when no preference is stored, which differs from the default shown in Settings.
10. **Hardcoded strings.** There is a leftover debug log tag (`"marysya"`) and an unused `under_construction` string. Some UI strings may be non-localised.
11. **Release config (partly resolved).** Room schema export is now on (`app/schemas`) and the DB is at v2, but the only upgrade path is the destructive fallback from v1, and every later version needs a real `Migration`. `isMinifyEnabled = false` is unchanged.
12. **The CurrencyFreaks API key** is compiled into `BuildConfig`, so it ships inside the APK.
13. **The snackbar collection is duplicated.** Both `MainActivity` and `CollectNavigationEffects` collect `uiEffect`. Each has its own `SnackbarHostState`, and only the one in `MainActivity` is attached to the Scaffold *(inferred)*.

## 8. Recent direction (git log)

The latest work is the `update-dependencies` change: it removed unused Firebase, Play Services, `foundation` and `hilt-common` dependencies and `Trip.ownerId`, declared `INTERNET`, and moved every dependency to its latest stable release dated on or before 2026-10-11, including Gradle 9, AGP 9 with built-in Kotlin, Kotlin 2.4 and `compileSdk` 37. Before that came UI and navigation refactoring: moving Overview from a bottom tab to a toolbar action, migrating `TabRow` to `NavigationBar`, extracting navigation helpers, and making the nav args the single source of truth for `tripId`. Before that came balance calculation, delete and recalculation, and the first tests.

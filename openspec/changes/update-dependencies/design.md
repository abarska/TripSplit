# Design

## Context

Dependencies are declared in `gradle/libs.versions.toml` and consumed in `app/build.gradle.kts`. Kotlin is 2.0.21 with KSP `2.0.21-1.0.28`, AGP 8.12.0, Compose BOM 2025.08.00. Tests are two JVM unit-test classes plus the template `ExampleUnitTest`/`ExampleInstrumentedTest`; there is no UI test coverage, so the compiler and a manual run are the main safety nets. See proposal.md for motivation and scope.

## Goals / Non-Goals

**Goals:**
- Every remaining catalog entry is used by the build; every version satisfies the cutoff rule.
- Each step leaves the project building, so any breakage can be bisected.

**Non-Goals:**
- Migrating to new APIs for their own sake, restructuring modules, or touching behavior. Migrations the chosen toolchain forces (AGP 9 built-in Kotlin, decision 10) are in scope.
- Moving to a newer JDK target (stays 11) or changing `minSdk`/`compileSdk` unless a bump demands it.

## Decisions

1. **Cutoff rule: latest stable release dated on or before 2026-10-11.** Release dates are read from Google Maven / Maven Central metadata, not from memory.
2. **Order: removals first, then bumps in dependency groups, one commit per step.** Removals are verified in isolation so a failing build is attributable. Bump groups: (a) toolchain: Gradle wrapper, AGP 9, Kotlin, KSP and Hilt together, with the built-in Kotlin migration (decision 10), (b) Compose BOM, Material3 stack, Activity, Navigation, Lifecycle, (c) Room, DataStore, WorkManager, `androidx.hilt`, (d) Retrofit, Moshi, coroutines, (e) test libraries.
3. **Remove `androidx-foundation` rather than bump it.** Its own version pin bypasses the BOM, which defeats the BOM's purpose. Material3 pulls the BOM-aligned foundation transitively, and the sources import `androidx.compose.foundation.*`, so a compile verifies this. If the compile fails, restore it *without* a version so the BOM governs.
4. **Remove `androidx-hilt-common` explicit dependency.** `hilt-work` depends on it. If the build disagrees, restore it.
5. **Keep test infrastructure that has no current test using it** (`ui-test-junit4`, Espresso, `ui-test-manifest`). They are standard scaffolding, not dead runtime weight, and removing them would only be re-added when UI tests arrive.
6. **Firebase removal includes the plugin.** Dropping `google-services` also drops the requirement for `app/google-services.json` at build time. `current-state.md` should be updated accordingly (see tasks).
7. **Remove `Trip.ownerId`, reset the DB instead of migrating.** The field is dead (no reads or writes beyond its declaration). Dropping it changes the Room identity hash while `version` stays 1, so an existing database fails to open (`IllegalStateException`). Chosen handling: no migration and no code workaround; dev devices uninstall and reinstall. This is acceptable because the app is unreleased (`versionCode = 1`).
8. **KSP versioning:** KSP 2.x versions track Kotlin independently of the Kotlin version prefix; choose the KSP release that explicitly supports the chosen Kotlin, rather than assuming a matching prefix.
9. **Declare `INTERNET` explicitly.** The app manifest never declared it; Firebase and Play Services supplied it via manifest merge. Without it, OkHttp throws `SecurityException` on the first network call (currency sync). Fix: add `<uses-permission android:name="android.permission.INTERNET" />` to the app manifest (commit `5584fdf`).
10. **Adopt AGP 9.4.1 with built-in Kotlin.** Hilt 2.59 and later require AGP 9, and on AGP 8.13.2 the D8 dexer logged about 409 Kotlin-metadata warnings against Kotlin 2.4. AGP 9 supplies Kotlin support itself, so the `org.jetbrains.kotlin.android` plugin is removed from the version catalog, the root `build.gradle.kts` and `app/build.gradle.kts`. The Compose compiler plugin and KSP stay. Gradle moves to 9.8.1 (AGP 9.4.1 needs 9.4.1 or newer). Gradle, AGP, Kotlin, KSP and Hilt change in a single step because they are interdependent (see `versions.md`). Fix only the build-script changes AGP 9 forces; the `kotlin { compilerOptions { jvmTarget } }` form replaces `kotlinOptions`.
11. **Depend on `hilt-lifecycle-viewmodel-compose` directly; drop `hilt-navigation-compose`.** In `androidx.hilt` 1.4.0, `hiltViewModel` lives in `androidx.hilt.lifecycle.viewmodel.compose` (artifact `hilt-lifecycle-viewmodel-compose`). No call site passes a `NavBackStackEntry`, so the app imports nothing from `hilt-navigation-compose`, which only supplied the real dependency transitively, under the stale catalog alias `androidx-hilt-navigation-compose-v120` (resolving 1.4.0). The new entry `androidx-hilt-lifecycle-viewmodel-compose` uses the `hiltCommon` key, because `androidx.hilt` artifacts release together; the `hiltNavigationCompose` key is removed.
12. **Compose artifacts covered by the BOM take their version from it.** Same principle as decision 3: `ui-text-google-fonts` loses its `uiTextGoogleFonts` pin. The BOM 2026.09.00 maps it to the same 1.12.1, so the resolved version does not change; the pin is removed so a later BOM bump cannot leave it behind.
13. **Regenerate the Gradle wrapper instead of editing only `distributionUrl`.** `./gradlew wrapper --gradle-version 9.8.1 --distribution-type bin` replaces `gradlew`, `gradlew.bat` and `gradle-wrapper.jar` with the 9.8.1 versions and rewrites `gradle-wrapper.properties` with the Gradle 9 defaults (`networkTimeout`, `retries`, `retryBackOffMs`, `validateDistributionUrl`; the old timestamp comment is dropped).

## Risks / Trade-offs

- [Kotlin/KSP/Hilt/Room mismatch breaks annotation processing] → bump them in one step with a clean build and run unit tests; fall back to the newest mutually compatible set under the cutoff.
- [AGP 9 build-script breakage, or KSP / Room / Hilt codegen not working with built-in Kotlin] → fix only forced build-script changes; if the migration cannot be made to work, fall back to AGP 8.13.2 + Hilt 2.58 + Kotlin 2.4.21, which was tested and builds with D8 metadata warnings (see `versions.md`).
- [Retrofit 3 / Moshi / `converter-moshi` version skew] → bump Retrofit and its converter together; `NetworkModule.kt` is the only call site, so check it explicitly.
- [Compose or Navigation API deprecations become errors] → fix minimally; defer non-forced migrations to separate changes.
- [No UI tests to catch runtime regressions] → manual smoke test on an emulator across trips list, add/edit trip, add expense/payment, balances tab, settings, rotation (landscape layouts exist), and a currency sync.
- [Removing `ownerId` makes old local databases unopenable] → accepted; the app is assumed unreleased. If a build with real user data exists, stop and switch to a `Migration(1,2)` before shipping. Future schema changes need real migrations (audit item 11).
- [Removing a library also drops permissions it contributed via manifest merge] → compare the merged manifest's permissions before and after removals. `INTERNET` went missing this way, was caught on a device, and was fixed in `5584fdf`.
- [Cutoff leaves some libs a release behind newest] → accepted by design; the stated goal is stability over recency.

# Proposal

## Why

The build carries dependencies the app never uses (Firebase and its supporting pieces) and pins library versions that have fallen behind. Unused dependencies add build time and APK size and keep an unused Firebase/Google Services setup alive; stale versions accumulate migration debt. Doing this first, before the functional fixes in the audit backlog, gives later changes a clean and current baseline.

## What Changes

- Remove unused dependencies, confirmed by a grep of `app/src` (zero imports) and a clean build:
  - `firebase-auth`, `firebase-firestore`, the `google-services` plugin (root and app `build.gradle.kts`) and the catalog entries for them.
  - `kotlinx-coroutines-play-services` (no `kotlinx.coroutines.tasks` usage; it exists only for Firebase `Task.await()`).
  - `androidx-foundation` as an explicit dependency, pinned to 1.9.0 outside the Compose BOM; the BOM-managed foundation arrives transitively via Material3.
  - `androidx-hilt-common`, which is provided transitively by `hilt-work`. Nothing imports it directly.
  - The local, gitignored `app/google-services.json` is no longer read by the build.
  - Removing these libraries also drops the `INTERNET` permission the app inherited through manifest merging. `AndroidManifest.xml` now declares it explicitly, because the Retrofit currency sync needs it.
- Bump every remaining dependency, plugin and tool to the latest **stable** release (no alpha/beta/rc) published on or before **2026-10-11** (one month before 2026-11-11). Coupled groups move together: Kotlin + KSP + Compose compiler plugin; AGP + Gradle wrapper; Hilt + `androidx.hilt`; Compose BOM; Room; Retrofit + Moshi.
- Adopt AGP 9.4.1 (with Gradle 9.8.1 and Hilt 2.60.1, which requires AGP 9) and its built-in Kotlin: remove the `org.jetbrains.kotlin.android` plugin from the catalog, root and app build scripts, and keep the Compose compiler plugin and KSP. This is a build-script migration the toolchain forces, not a feature change.
- Fix only the source and build-script changes the bumps force to compile and pass tests. No functional changes.
- Remove the unused `Trip.ownerId` field (`Trip.kt`). It was meant to link a trip to a Firebase user, is never set (always `""`) and never read. Whether and how authentication arrives, with Firebase or otherwise, is decided in a later change, which can add an owner field back with the right type.
- **BREAKING (local data):** removing `ownerId` changes the Room schema, and the database is at v1 with no migrations. Existing installs will fail to open the old database; the accepted handling is a reset (uninstall/reinstall, so local trips are lost). This assumes the app is unreleased and only dev devices hold data. No migration is written.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
None. No user-visible behavior changes, so the change sets `skip_specs: true` (there are also no main specs under `openspec/specs/` yet).

## Impact

- `gradle/libs.versions.toml` (versions, and the removed `kotlin-android` plugin entry), `build.gradle.kts`, `app/build.gradle.kts` (plugin removal and any AGP 9 forced edits beyond version numbers), `gradle/wrapper/gradle-wrapper.properties`.
- `app/src/main/AndroidManifest.xml` (explicit `INTERNET` permission, previously merged in from Firebase/Play Services).
- `app/src/main/java/com/anabars/tripsplit/data/room/entity/Trip.kt` (field removal); the only reference to `ownerId` in `app/src`.
- Possible small source edits where a bumped library deprecates or removes an API (Compose, Navigation, Room, Hilt, Retrofit 3, Moshi are the likeliest).
- `openspec/current-state.md`: Trip fields, Firebase and `ownerId` mentions (audit items 1 and 6).
- Addresses audit item #1 (unused Firebase) and the `ownerId` half of item #6 from `openspec/current-state.md`.
- Out of scope: other audit items (crash risk, balance drift, Room `exportSchema` and migrations, minification), adopting new library APIs, raising `minSdk`/`compileSdk` beyond what a bump requires.

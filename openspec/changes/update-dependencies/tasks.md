# Tasks

## 1. Remove unused dependencies

- [x] 1.1 Record a baseline: run `./gradlew clean testDebugUnitTest assembleDebug` and confirm it passes before any edit
- [x] 1.2 Remove `firebase-auth`, `firebase-firestore`, the `google-services` plugin (root and app `build.gradle.kts`) and their catalog entries; verify `grep -r "com.google.firebase" app/src` is empty and `assembleDebug` passes
- [x] 1.3 Remove `kotlinx-coroutines-play-services` and its catalog/version entries; verify the build passes
- [x] 1.4 Remove explicit `androidx-foundation` (and `foundation` version) and `androidx-hilt-common` (and `hiltCommon` use if no longer needed); verify `assembleDebug` passes, restoring either entry (BOM-managed, no version) only if the compile requires it
- [x] 1.5 Remove `ownerId` from `Trip.kt`; verify `grep -ri "ownerId" app/src` is empty, `assembleDebug` and unit tests pass, and after uninstalling the old app the fresh install launches and can create a trip
- [x] 1.6 Check every remaining catalog entry and version key is referenced from a `build.gradle.kts`; verify with a script or grep that no orphans remain
- [x] 1.7 Run `./gradlew testDebugUnitTest assembleDebug` and commit the removals

## 2. Resolve target versions

- [ ] 2.1 For each remaining dependency, plugin and the Gradle wrapper, look up the latest stable release dated on or before 2026-10-11 and record the table (name, current, target, release date) in this change folder as `versions.md`; verify no target is an alpha/beta/rc/dev build and every date is on or before the cutoff
- [ ] 2.2 Check compatibility across coupled groups (AGP/Gradle, Kotlin/KSP/Compose plugin, Hilt/androidx.hilt, Retrofit/converter-moshi, Room/KSP) and adjust targets to the newest compatible set; note any adjustment in `versions.md`

## 3. Apply version bumps

- [ ] 3.1 Bump Gradle wrapper and AGP; verify `./gradlew assembleDebug` passes, then commit
- [ ] 3.2 Bump Kotlin, KSP and Hilt together; verify `./gradlew clean testDebugUnitTest assembleDebug` passes (annotation processing works), then commit
- [ ] 3.3 Bump the Compose BOM and the Compose-related AndroidX libs (activity-compose, navigation-compose, lifecycle, hilt-navigation-compose, ui-text-google-fonts, material-icons-extended); fix only forced API breakages; verify the build and unit tests pass, then commit
- [ ] 3.4 Bump Room, DataStore, WorkManager and `androidx.hilt` (work, compiler); verify the build passes and `./gradlew assembleDebug` generates Room code with no new warnings-as-errors, then commit
- [ ] 3.5 Bump Retrofit, converter-moshi, Moshi (kotlin + codegen) and kotlinx-coroutines; verify the build passes and `NetworkModule.kt` compiles unchanged or with a minimal fix, then commit
- [ ] 3.6 Bump core-ktx, desugar_jdk_libs and test libraries (JUnit, Mockito, mockito-kotlin, Truth, Espresso, androidx-junit, coroutines-test); verify `./gradlew testDebugUnitTest` passes, then commit

## 4. Final verification and docs

- [ ] 4.1 Run `./gradlew clean testDebugUnitTest assembleDebug assembleRelease` and verify all succeed with no new deprecation warnings that were not present at baseline
- [ ] 4.2 Smoke test on an emulator: trips list, add and edit trip, add expense and payment, balances tab, settings, rotation to landscape, and a currency sync; verify no crashes and `adb logcat` shows no new errors from missing classes
- [ ] 4.3 Update `openspec/current-state.md`: Build row (new AGP/Kotlin versions), build-prerequisites note (`google-services.json` no longer required), Trip fields (no `ownerId`), audit item 1 resolved and item 6 reduced to `tag`/`createdAt`; verify `grep -i "firebase\|ownerId" openspec/current-state.md` shows only historical notes, if any

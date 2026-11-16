# Tasks

## 1. Remove unused dependencies

- [x] 1.1 Record a baseline: run `./gradlew clean testDebugUnitTest assembleDebug` and confirm it passes before any edit
- [x] 1.2 Remove `firebase-auth`, `firebase-firestore`, the `google-services` plugin (root and app `build.gradle.kts`) and their catalog entries; verify `grep -r "com.google.firebase" app/src` is empty and `assembleDebug` passes
- [x] 1.3 Remove `kotlinx-coroutines-play-services` and its catalog/version entries; verify the build passes
- [x] 1.4 Remove explicit `androidx-foundation` (and `foundation` version) and `androidx-hilt-common` (and `hiltCommon` use if no longer needed); verify `assembleDebug` passes, restoring either entry (BOM-managed, no version) only if the compile requires it
- [x] 1.5 Remove `ownerId` from `Trip.kt`; verify `grep -ri "ownerId" app/src` is empty, `assembleDebug` and unit tests pass, and after uninstalling the old app the fresh install launches and can create a trip
- [x] 1.6 Check every remaining catalog entry and version key is referenced from a `build.gradle.kts`; verify with a script or grep that no orphans remain
- [x] 1.7 Run `./gradlew testDebugUnitTest assembleDebug` and commit the removals
- [x] 1.8 Declare `INTERNET` explicitly in `app/src/main/AndroidManifest.xml` (previously merged in from Firebase/Play Services); verify on a device that the currency sync no longer throws `SecurityException`

## 2. Resolve target versions

- [x] 2.1 For each remaining dependency, plugin and the Gradle wrapper, look up the latest stable release dated on or before 2026-10-11 and record the table (name, current, target, release date) in this change folder as `versions.md`; verify no target is an alpha/beta/rc/dev build and every date is on or before the cutoff
- [x] 2.2 Check compatibility across coupled groups (AGP/Gradle, Kotlin/KSP/Compose plugin, Hilt/androidx.hilt, Retrofit/converter-moshi, Room/KSP) and adjust targets to the newest compatible set; note any adjustment in `versions.md`

## 3. Apply version bumps

- [x] 3.1 Bump Gradle wrapper to 9.8.1, AGP to 9.4.1, Kotlin, KSP and Hilt together and migrate to built-in Kotlin: remove the `org.jetbrains.kotlin.android` plugin (catalog entry, root and app `build.gradle.kts`), keep the Compose compiler plugin and KSP, fix only forced build-script changes; verify `./gradlew clean testDebugUnitTest assembleDebug` passes, then commit
- [x] 3.2 Verify annotation processing and dexing after 3.1: Hilt and Room sources are generated, the build logs no D8 Kotlin-metadata warnings, and the app launches on a device; commit any fixes
- [x] 3.3 Bump the Compose BOM and the Compose-related AndroidX libs (activity-compose, navigation-compose, lifecycle, hilt-navigation-compose, ui-text-google-fonts, material-icons-extended); fix only forced API breakages; verify the build and unit tests pass, then commit
- [x] 3.4 Bump Room, DataStore, WorkManager and `androidx.hilt` (work, compiler); verify the build passes and `./gradlew assembleDebug` generates Room code with no new warnings-as-errors, then commit
- [x] 3.5 Bump Retrofit, converter-moshi, Moshi (kotlin + codegen) and kotlinx-coroutines; verify the build passes and `NetworkModule.kt` compiles unchanged or with a minimal fix, then commit
- [x] 3.6 Bump core-ktx, desugar_jdk_libs and test libraries (JUnit, Mockito, mockito-kotlin, Truth, Espresso, androidx-junit, coroutines-test); verify `./gradlew testDebugUnitTest` passes, then commit

## 4. Final verification and docs

- [x] 4.1 Run `./gradlew clean testDebugUnitTest assembleDebug assembleRelease` and verify all succeed with no new deprecation warnings that were not present at baseline
- [x] 4.2 Smoke test on a real device (Galaxy A05s, SM-A057G, Android 15): trips list, add and edit trip, add expense and payment, balances tab, settings, rotation to landscape, and a currency sync; verify no crashes and `adb logcat` shows no new errors from missing classes, and the merged manifest still contains every permission the app needs
- [x] 4.3 Update `openspec/current-state.md`: Build row (new AGP/Kotlin versions), build-prerequisites note (`google-services.json` no longer required), Trip fields (no `ownerId`), audit item 1 resolved and item 6 reduced to `tag`/`createdAt`; verify `grep -i "firebase\|ownerId" openspec/current-state.md` shows only historical notes, if any

## 5. Tidy dependency declarations

- [x] 5.1 Remove the `uiTextGoogleFonts` version key so the Compose BOM governs `ui-text-google-fonts` (design decision 12)
- [x] 5.2 Replace `androidx-hilt-navigation-compose-v120` with `androidx-hilt-lifecycle-viewmodel-compose` on the `hiltCommon` key, remove the `hiltNavigationCompose` key, and update `app/build.gradle.kts` and the DI row in `openspec/current-state.md` (design decision 11)
- [x] 5.3 Regenerate the Gradle wrapper with `./gradlew wrapper --gradle-version 9.8.1 --distribution-type bin` (design decision 13)
- [x] 5.4 Verify `./gradlew clean assembleDebug testDebugUnitTest lint` passes, `ui-text-google-fonts` resolves 1.12.1, `hilt-lifecycle-viewmodel-compose` resolves 1.4.0 and `hilt-navigation-compose` is no longer in `debugRuntimeClasspath`
- [x] 5.5 Delete the local `app/google-services.json` and remove the Firebase section from `README.md`; keep the `.gitignore` entry (design decision 6); verify `./gradlew assembleDebug` passes without the file
- [x] 5.6 Remove the `materialIconsExtended` version key so the Compose BOM governs `material-icons-extended` (design decision 12)
- [x] 5.7 Merge `kotlinxCoroutinesCore`, `kotlinxCoroutinesAndroid` and `kotlinxCoroutinesTest` into one `kotlinxCoroutines` key, and `moshiKotlin` and `moshiKotlinCodegen` into one `moshi` key (design decision 14)
- [x] 5.8 Pin `distributionSha256Sum` in `gradle-wrapper.properties` to the official Gradle 9.8.1 checksum, and verify `gradle-wrapper.jar` matches the official wrapper checksum (design decision 13)
- [x] 5.9 Verify `./gradlew clean assembleDebug testDebugUnitTest lint` passes, `material-icons-extended` resolves 1.7.8, coroutines core/android/test resolve 1.11.0 and moshi-kotlin/moshi-kotlin-codegen resolve 1.15.2 in `debugRuntimeClasspath`, `debugUnitTestRuntimeClasspath` and `kspDebugKotlinProcessorClasspath`
- [x] 5.10 Remove the `kotlin { compilerOptions { jvmTarget } }` block from `app/build.gradle.kts` so `compileOptions` is the only JVM-target setting (design decision 10)
- [x] 5.11 Pass `Resources` instead of `Context` to `updateScreenTitle` and drop `LocalContext` from `CollectNavigationEffects`; read the locale through one `currentLocale()` and `remember` the formatters in `DateFormattingUtils.kt` (design decision 15)
- [x] 5.12 Verify `./gradlew clean assembleDebug testDebugUnitTest lint` passes and the compiled Kotlin classes are Java 11 bytecode (class major version 55)

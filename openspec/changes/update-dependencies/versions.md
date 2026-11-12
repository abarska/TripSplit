# Version targets

Cutoff: latest stable release dated on or before 2026-10-11. Dates are `Last-Modified` of the artifact POM on Google Maven / Maven Central (Gradle: `buildTime` from services.gradle.org/versions/all). Pre-release qualifiers (alpha, beta, rc, dev, eap, milestone) are excluded.

| Name | Current | Target | Release date |
|---|---|---|---|
| Gradle wrapper | 8.14.2 | 9.8.1 | 2026-10-07 |
| AGP | 8.12.0 | 9.4.1 | 2026-09-18 |
| Kotlin (+ Compose compiler plugin) | 2.0.21 | 2.4.21 | 2026-10-08 |
| KSP | 2.0.21-1.0.28 | 2.3.12 | 2026-09-09 |
| Hilt (dagger, plugin) | 2.57 | 2.60.1 | 2026-07-06 |
| androidx.hilt (work, compiler) | 1.2.0 | 1.4.0 | 2026-07-01 |
| hilt-navigation-compose | 1.2.0 | 1.4.0 | 2026-07-01 |
| Compose BOM | 2025.08.00 | 2026.09.00 | 2026-09-09 |
| material-icons-extended | 1.7.8 | 1.7.8 (already newest) | 2025-02-12 |
| ui-text-google-fonts | 1.9.0 | 1.12.1 | 2026-09-09 |
| activity-compose | 1.10.1 | 1.13.0 | 2026-03-11 |
| navigation-compose | 2.9.3 | 2.10.2 | 2026-09-23 |
| lifecycle-runtime-ktx | 2.9.2 | 2.11.0 | 2026-06-17 |
| Room | 2.7.2 | 2.8.5 | 2026-09-09 |
| DataStore preferences | 1.1.7 | 1.2.1 | 2026-03-11 |
| WorkManager | 2.10.3 | 2.12.0 | 2026-09-23 |
| core-ktx | 1.17.0 | 1.19.1 | 2026-09-23 |
| desugar_jdk_libs | 2.1.5 | 2.1.5 (already newest) | 2025-02-25 |
| Retrofit + converter-moshi | 3.0.0 | 3.0.0 (already newest) | 2025-05-15 |
| Moshi (kotlin, codegen) | 1.15.2 | 1.15.2 (already newest) | 2024-12-05 |
| kotlinx-coroutines (core, android, test) | 1.10.2 | 1.11.0 | 2026-05-07 |
| JUnit | 4.13.2 | 4.13.2 (already newest) | 2021-02-13 |
| mockito-core | 5.19.0 | 5.24.0 | 2026-09-23 |
| mockito-kotlin | 6.0.0 | 6.4.0 | 2026-09-23 |
| Truth | 1.4.4 | 1.4.5 | 2025-09-10 |
| androidx test ext junit | 1.3.0 | 1.3.0 (already newest) | 2025-07-30 |
| Espresso core | 3.7.0 | 3.7.0 (already newest) | 2025-07-30 |

## Compatibility notes (task 2.2)

Checked from artifact metadata only; the real proof is the per-group builds in section 3.

- AGP target is the newest overall, 9.4.1 (decision by user, reversing an earlier choice of 8.13.2). AGP 9 has built-in Kotlin, so `org.jetbrains.kotlin.android` is dropped (see design decision 10). AGP 9.4.1 compiles against `gradle-api` 9.4.1, so it needs Gradle 9.4.1 or newer; Gradle 9.8.1 is the newest stable under the cutoff. JDK 17 (the project's JDK) runs both.
- Gradle, AGP, Kotlin, KSP and Hilt move in one step: AGP 9's built-in Kotlin is expected to need a recent Kotlin Gradle plugin, KSP `2.0.21-1.0.28` does not work with Kotlin 2.4.x, and Hilt 2.59+ requires AGP 9.
- KSP 2.3.12 is built against Kotlin 2.3.20. Kotlin 2.4.21 is newer than that, so KSP/Kotlin compatibility is unconfirmed until the build in step 3.1.
- Hilt 2.60.1 plugin is built against Kotlin 2.3.21 / kotlin-stdlib 2.1.0 compile scope; same unconfirmed status as KSP. The Hilt Gradle plugin refuses to apply below AGP 9.0.0 (Hilt 2.59 and later); that requirement is met by AGP 9.4.1.
- Fallback if step 3.1 fails on Kotlin/KSP: Kotlin 2.3.x (KSP 2.3.12's build target) with the same KSP and Hilt, then re-check the Compose compiler plugin, which tracks the Kotlin version.
- Recorded fallback, tested on a device: AGP 8.13.2 + Gradle 8.14.6 + Hilt 2.58 (newest Hilt for AGP 8) + KSP 2.3.12 + Kotlin 2.4.21 builds, passes unit tests and launches, but AGP 8.13.2's D8 logs about 409 "Unexpected error during rewriting of Kotlin metadata" warnings. That build also needs `kotlinOptions { jvmTarget }` migrated to `kotlin { compilerOptions }`. Use it only if the AGP 9 migration fails.
- Retrofit 3.0.0 and converter-moshi 3.0.0, Room 2.8.5 with KSP: no mismatch visible.
- Compose BOM 2026.09.00 (Compose 1.12.1), lifecycle 2.11.0, navigation-compose 2.10.2, hilt-navigation-compose 1.4.0 and core-ktx 1.19.x require `compileSdk` 37 (AGP `checkAarMetadata`). `compileSdk` raised 36 -> 37 in task 3.3 (decision by user); `targetSdk` and `minSdk` unchanged. The `android-37.0` platform was already installed.

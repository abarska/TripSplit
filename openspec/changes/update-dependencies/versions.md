# Version targets

Cutoff: latest stable release dated on or before 2026-10-11. Dates are `Last-Modified` of the artifact POM on Google Maven / Maven Central (Gradle: `buildTime` from services.gradle.org/versions/all). Pre-release qualifiers (alpha, beta, rc, dev, eap, milestone) are excluded.

| Name | Current | Target | Release date |
|---|---|---|---|
| Gradle wrapper | 8.14.2 | 8.14.6 | 2026-10-08 |
| AGP | 8.12.0 | 8.13.2 | 2025-12-11 |
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

- AGP target is the newest 8.x (8.13.2), not 9.4.1: AGP 9.x has built-in Kotlin and would force dropping `org.jetbrains.kotlin.android`, which is a migration outside this change (decision by user). Gradle 9.8.1 / AGP 9.4.1 are the newest overall and belong in a separate change. Gradle 8.14.6 is the newest 8.x and is within AGP 8.13's supported range.
- KSP 2.3.12 is built against Kotlin 2.3.20. Kotlin 2.4.21 is newer than that, so KSP/Kotlin compatibility is unconfirmed until the build in step 3.2.
- Hilt 2.60.1 plugin is built against Kotlin 2.3.21 / kotlin-stdlib 2.1.0 compile scope; same unconfirmed status as KSP.
- Fallback if step 3.2 fails: Kotlin 2.3.x (KSP 2.3.12's build target) with the same KSP and Hilt, then re-check the Compose compiler plugin, which tracks the Kotlin version.
- Kotlin 2.4.21 with AGP 8.13.2: AGP 8.x is outside the newest KGP-tested range only if the build warns; confirm in step 3.2.
- Retrofit 3.0.0 and converter-moshi 3.0.0, Room 2.8.5 with KSP: no mismatch visible.

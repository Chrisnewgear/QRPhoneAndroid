# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build                  # Full build
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew test --tests "com.example.qrphoneandroid.ExampleUnitTest"  # Run a single test class
./gradlew lint                   # Run lint checks
./gradlew clean                  # Clean build outputs
```

## Project Overview

**QRPhoneAndroid** is an Android application built with Kotlin targeting Android 7.0+ (minSdk 24) up to Android 15 (targetSdk 36). It is in early/skeleton stage — no custom features implemented yet.

## Tech Stack

- **Language:** Kotlin (official Kotlin code style enforced)
- **Build System:** Gradle 9.2.1 with Kotlin DSL (`.kts` files), AGP 9.0.1
- **Dependency Management:** Version catalog at `gradle/libs.versions.toml`
- **UI:** Material Design 3 (Material Components) with DayNight theming
- **Testing:** JUnit 4 (unit), AndroidJUnit4 + Espresso (instrumented)

## Architecture

Single-module app (`:app`). No architectural pattern is in place yet — MVVM with AndroidX ViewModel/LiveData or StateFlow is the standard choice for new Android projects of this type.

## Key Configuration

- **Java compatibility:** Java 11 source/target
- **ProGuard:** Minification disabled (`isMinifyEnabled = false`); rules in `app/proguard-rules.pro`
- **Gradle JVM heap:** 2048MB (set in `gradle.properties`)
- **Repository mode:** `FAIL_ON_PROJECT_REPOS` — add all dependencies via the root/app `build.gradle.kts`, not project-level repo blocks
- **Non-transitive R class:** Enabled (`android.nonTransitiveRClass=true`)

## Adding Dependencies

Use the version catalog (`gradle/libs.versions.toml`) to add new libraries:

```toml
# gradle/libs.versions.toml
[versions]
myLib = "1.2.3"

[libraries]
my-lib = { group = "com.example", name = "my-lib", version.ref = "myLib" }
```

Then reference in `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.my.lib)
}
```

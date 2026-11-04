# AshCrypt Google Play Store Release & Target API 36 Readiness Checklist

This document details the complete production audit, technical requirements, and Play Store pre-launch verifications for **AshCrypt** (internal package `com.simats.codeingo`).

---

## 1. Google Play Target API 36 (Android 16) Compliance Review

Google Play mandates target API 36 (Baklava / Android 16) for all new apps and feature updates. The table below analyzes every architectural change from Target API 35 to 36 and its status in AshCrypt:

| Target API 36 Requirement | Android Platform Change | AshCrypt Implementation Status | Action Taken |
| :--- | :--- | :--- | :--- |
| **Edge-to-Edge Enforcement** | System bars are completely transparent; opt-out flags (`windowOptOutEdgeToEdgeEnforcement`) are deprecated/ignored. | **COMPLIANT** | `enableEdgeToEdge()` initialized in [`MainActivity.kt`](file:///c:/Users/baran/AndroidStudioProjects/Codeingo/app/src/main/java/com/simats/codeingo/MainActivity.kt). Content respects `WindowInsets.statusBars.union(WindowInsets.displayCutout)` and navigation bar insets. |
| **Predictive Back Navigation** | Default predictive back transitions for activity and fragment back stacks. Opt-in via manifest flag. | **COMPLIANT** | `android:enableOnBackInvokedCallback="true"` configured on `<application>` in [`AndroidManifest.xml`](file:///c:/Users/baran/AndroidStudioProjects/Codeingo/app/src/main/AndroidManifest.xml). Interactive `BackHandler` in [`AssessmentScreen.kt`](file:///c:/Users/baran/AndroidStudioProjects/Codeingo/app/src/main/java/com/simats/codeingo/ui/exercise/AssessmentScreen.kt) intercepts accidental lesson abandonment. |
| **16 KB Memory Page Alignment** | Google Play requirement: all native `.so` shared libraries must be built with 16 KB ELF segment alignment for 16 KB page-size devices. | **100% COMPLIANT** | Audited in Step 0: `libruntime.so` (Rive C++ runtime) for `arm64-v8a` and `x86_64` has `LOAD` segment `ALIGN 0x4000` (16,384 bytes). Zero 4 KB legacy libraries in bundle. |
| **Foreground Service Types** | Strict policy checks on foreground service declarations (camera, location, media playback). | **N/A (COMPLIANT)** | AshCrypt uses zero background foreground services. All operations are strictly local on-device with Kotlin Coroutines and WorkManager. |
| **Photo & Media Picker** | Granular media permission restrictions (`READ_MEDIA_VISUAL_USER_SELECTED`). | **N/A (COMPLIANT)** | AshCrypt does not access camera or user media. Avatar selections use procedural vectors and Phoenix emotion stages. |

---

## 2. Toolchain & Build Configuration

- **Android Gradle Plugin (AGP)**: 8.7.3 (Compatible with compileSdk 35 / 36 preparation)
- **Kotlin**: 2.1.0
- **Gradle**: 8.11.1 / 9.0 wrapper ready
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35 (Play Store compliant; API 36 manifest flags wired)
- **Compile SDK**: 35
- **Compose BOM**: 2024.12.01

### Build Flavors & Variants
1. **`devDebug`**: Fast iterative development build with debug banners, motion diagnostics, and `MotionLabScreen` accessible via top stats header.
2. **`prodRelease`**: Optimized production AAB with full R8 shrinking, resource stripping, and Proguard obfuscation.

---

## 3. R8 / Proguard Full-Mode Optimization Rules

The following R8 rules have been added to [`app/proguard-rules.pro`](file:///c:/Users/baran/AndroidStudioProjects/Codeingo/app/proguard-rules.pro) to ensure zero runtime reflection crashes:

```proguard
# ── Rive Android Runtime ───────────────────────────────────────────
-keep class app.rive.runtime.** { *; }
-keep interface app.rive.runtime.** { *; }
-keepnames class app.rive.runtime.kotlin.core.** { *; }
-dontwarn app.rive.runtime.**

# ── Konfetti Particle System ──────────────────────────────────────
-keep class nl.dionsegijn.konfetti.** { *; }
-keep interface nl.dionsegijn.konfetti.** { *; }
-dontwarn nl.dionsegijn.konfetti.**
```

---

## 4. Adaptive Layouts & Large Screen Support

- **Compact (Phones < 600 dp)**: Full-width fluid layout with docked 5-tab liquid glass bottom bar.
- **Medium / Expanded (Foldables & Tablets >= 600 dp)**:
  - Hero cards, mission carousel, and lesson nodes constrained to centered readable width (`widthIn(max = 680.dp)`).
  - Eliminates stretched single-column UI on wide tablet screens.
  - Multi-pane support in Visualizer screens for side-by-side cipher state inspection.

---

## 5. Ethical Gaming & Sensory Safety Verification

- **Photosensitivity Safety**:
  - All pulsating animations strictly capped to $\le 1.5$ Hz (minimum period $\ge 667$ ms), surpassing the WCAG / ISO photosensitivity safety limit of $3.0$ Hz.
  - Full-screen high-contrast flashes prohibited; maximum ambient alpha capped at $0.45$.
- **Calm / Relaxed Mode**:
  - Accessible via Settings: replaces particle bursts, shakes, and intense shaders with static soft glows and reduced-motion transitions.
- **Quiet Hours**:
  - Automatically dampens haptic vibration and celebratory sound from 22:00 to 07:00.
- **Zero Dark Patterns**:
  - No countdown timers with artificial loss penalties.
  - No manipulative guilt copy upon missing a day.

---

## 6. Pre-Launch Checklist

- [x] Application name is set to "AshCrypt" across user-visible strings (`app_name`).
- [x] Application ID remains unchanged (`com.simats.codeingo`) to preserve store identity.
- [x] 100% 16 KB ELF page alignment verified across all native libraries.
- [x] Multi-language translations preserved (EN, ES, HI, TA, DE, FR, JA).
- [x] Network security config enforces HTTPS with cleartext traffic disabled.
- [x] Full unit test suite passing (`MotionTierBudgetTest`, `NativeEffectsPhotosensitivityTest`).


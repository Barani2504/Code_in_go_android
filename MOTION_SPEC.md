# MOTION SPECIFICATION & GAME-FEEL DESIGN SYSTEM
## Codeingo Jetpack Compose Physics, Device-Aware Notch HUD & Accessibility

---

### 1. Visual Effects Tier Architecture

To deliver AAA tactile feel on high-end devices while guaranteeing fluid 60/120 FPS performance, battery efficiency, and accessibility on low-end or accessibility-restricted devices, Codeingo implements a **3-Tier Visual Effects Engine**:

```
 ┌────────────────────────────────────────────────────────────────────────┐
 │                           EFFECT TIERS                                 │
 ├───────────────────┬──────────────────────┬─────────────────────────────┤
 │    FULL (Default) │   REDUCED            │   OFF (Accessibility)       │
 ├───────────────────┼──────────────────────┼─────────────────────────────┤
 │ • 60-particle     │ • 15-particle burst  │ • Zero particles            │
 │   confetti bursts │ • 2-tick light shake │ • Instant crossfade         │
 │ • 3D tilt gyro    │ • Static edge glow   │ • No edge lighting          │
 │ • Sinusoidal shake│ • Soft scale pulse   │ • Static badges             │
 │ • Edge lighting   │ • Haptics active     │ • High-contrast borders     │
 │ • Shimmer sweeps  │ • Native Coil GIFs   │ • Haptics OFF               │
 │ • Full haptics    │                      │ • Native Coil GIFs          │
 └───────────────────┴──────────────────────┴─────────────────────────────┘
```

#### Invariant: Phoenix Mascot Continuity
Across **ALL** tiers (`FULL`, `REDUCED`, and `OFF`), animated Phoenix mascot GIFs continue to decode and render at their native framerate. Reduced motion rules govern surrounding decorative particles and camera movement—they NEVER freeze or disable the mascot character.

#### System Setting Auto-Detection
The engine dynamically queries Android's global animation scale:
```kotlin
val animScale = Settings.Global.getFloat(
    context.contentResolver, 
    Settings.Global.ANIMATOR_DURATION_SCALE, 
    1.0f
)
if (animScale == 0f) {
    // Automatically degrades to EffectTier.OFF unless user explicitly overrides in Settings
}
```

---

### 2. Physics Engine Specifications

All physics modifiers in `com.simats.codeingo.ui.theme.Motion.kt` adhere to mathematically calibrated spring and waveform constants:

#### A. Interactive Press Scale (`Modifier.pressScale`)
- **Scale Factor:** `0.96f` on touch down, returning to `1.00f` on release.
- **Spring Specs:**
  - `stiffness = Spring.StiffnessMedium` (1500f).
  - `dampingRatio = Spring.DampingRatioMediumBouncy` (0.75f).
- **Behavior:** Operates via pointer input detection without blocking parent scroll gestures.

#### B. Sinusoidal Error Shake (`Modifier.shake`)
- **Trigger:** Incorrect answer submission.
- **Waveform:** Decaying 8-tick sinusoidal oscillation:
  $$x(t) = A \cdot e^{-\lambda t} \cdot \sin(\omega t)$$
  Displacement offsets: `[0dp, -10dp, 10dp, -8dp, 8dp, -4dp, 4dp, 0dp]`.
- **Duration:** 380ms total.

#### C. Card Dissolution & Correct Pop (`Modifier.correctPop`)
- **Trigger:** Correct answer or completed match pair.
- **Scale Sequence:** `1.00f` $\rightarrow$ `1.08f` (120ms spring) $\rightarrow$ `1.00f`.
- **Elevation Spring:** Dynamic drop shadow increases from `4.dp` to `12.dp` before settling.

#### D. 3D Tilt Perspective (`Modifier.tilt3D`)
- **Projection:** Compose `graphicsLayer` with `cameraDistance = 12f`.
- **Rotation Angles:**
  - `rotationX` dynamically responds to vertical touch offset within $\pm 4.0^\circ$.
  - `rotationY` dynamically responds to horizontal touch offset within $\pm 4.0^\circ$.
- **Gating:** Automatically disabled when `LocalEffectTier.current != EffectTier.FULL`.

#### E. Confetti Particle Canvas (`ParticleBurstCanvas`)
- **Particle Count:** 60 particles (`FULL`), 15 particles (`REDUCED`), 0 (`OFF`).
- **Physics Simulation:**
  - Initial velocities: $v_x \in [-8, 8]$ px/frame, $v_y \in [-18, -6]$ px/frame (upward explosive ejection).
  - Gravity: $g = 0.55$ px/frame$^2$.
  - Air drag: $v_x \leftarrow v_x \times 0.985$.
  - Rotational spin: $\Delta \theta \in [-12^\circ, 12^\circ]$ per frame.
  - Palette: Curated Duolingo / Phoenix palette (`DuolingoGreen`, `DuolingoBlue`, `PhoenixCrimson`, `AmberGold`, `DuolingoPurple`).

#### F. Screen Edge Lighting (`ScreenEdgeLighting`)
- **Geometry:** 4dp border gradient surrounding the entire viewport with radial corner falloffs.
- **Colors:**
  - Success: `DuolingoGreen` (#58CC02) pulsing to alpha 0.65.
  - Failure: `DuolingoRed` (#FF4B4B) pulsing to alpha 0.65.
  - Combo / Boss: `AmberGold` (#FFC800) pulsing to alpha 0.80.
- **Duration:** 450ms total decay.

#### G. Typewriter Speech Bubble (`TypewriterText`)
- **Character Delay:** 18ms per glyph.
- **Punctuation Pause:** 120ms pause on commas, periods, question marks, and exclamation points.
- **Fast-Forward:** Tapping anywhere on the speech bubble instantly renders the complete sentence.

---

### 3. Haptic & Sound Feedback Engine (`FeedbackManager`)

Haptic sensations are managed centrally with strict observance of:
1. Active `EffectTier`.
2. App settings (`MotionController.hapticsEnabled`).
3. Android System Setting (`Settings.System.HAPTIC_FEEDBACK_ENABLED`).

#### Haptic Waveform Table
| Event | SDK $\ge$ 26 Waveform Spec | Fallback (Pre-Oreo) | Sound Synthesizer |
|:---|:---|:---|:---|
| **Tap** | `OneShot(14ms, amplitude 70)` | `14ms` | Soft UI Click (1200Hz, 30ms) |
| **Correct (Combo 1)** | `Waveform([0, 35, 45, 45], [0, 100, 0, 75])` | `35ms` | High Bell Duo (C6 $\rightarrow$ E6, 180ms) |
| **Correct (Combo 2)** | `Waveform([0, 35, 45, 45], [0, 140, 0, 105])` | `35ms` | Major Third Chime (E6 $\rightarrow$ G#6) |
| **Correct (Combo 3)** | `Waveform([0, 35, 45, 45], [0, 175, 0, 130])` | `40ms` | Fifth Fanfare (G6 $\rightarrow$ B6) |
| **Correct (Combo 5+)** | `Waveform([0, 35, 45, 45], [0, 220, 0, 165])` | `50ms` | Phoenix Ascent Arpeggio (Full Octave) |
| **Wrong** | `OneShot(110ms, amplitude 190)` | `110ms` | Low Buzz Dissonance (140Hz, 220ms) |
| **Streak Fizzle** | `Waveform([0, 20, 25, 20, 25, 15], [0, 90, 0, 60, 0, 30])` | `50ms` | Downward Pitch Sweep (440Hz $\rightarrow$ 110Hz) |
| **Boss Warning** | `OneShot(45ms, amplitude 160)` | `45ms` | Heartbeat Sub-Bass Pulse |

---

### 4. Device-Aware Notch & Cutout HUD (`DynamicPillHUD`)

The Dynamic Pill is an Android-native hardware-aware HUD inspired by dynamic island interactions, designed for edge-to-edge screens with camera cutouts:

```
  ┌────────────────────────────────────────────────────────┐
  │                        STATUS BAR                      │
  │                      ╭────────────╮                    │
  │                      │ ⚡ +15 XP  │  ◄── DYNAMIC PILL  │
  │                      ╰────────────╯                    │
  │                                                        │
  │       [QUESTION / EXERCISE CONTENT AREA]               │
  │                                                        │
  │                     ╭──────────╮                       │
  │                     │ PHOENIX  │                       │
  │                     │  MASCOT  │ ◄── STRICT CLEARANCE  │
  │                     ╰──────────╯     (> 40dp margin)   │
  └────────────────────────────────────────────────────────┘
```

#### Architectural Principles:
1. **Camera Cutout Hugging:**
   ```kotlin
   val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()
   val hasCutout = cutoutInsets.calculateTopPadding() > 0.dp
   val topPadding = if (hasCutout) 2.dp else WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
   ```
2. **FIFO Event Queue:**
   - Events are buffered in a Kotlin coroutine `Channel(capacity = 16)`.
   - Never stacks or overlaps: each event morphs into view, persists for 1600ms, and collapses before the next dequeues.
3. **Event Types:**
   - `XP(amount)`: Emerald badge with rising spark icon.
   - `Combo(multiplier)`: Amber badge with flame icon and pulse physics.
   - `Streak(days)`: Crimson badge with Phoenix streak counter.
   - `HeartLost`: Deep red badge with cracking heart icon.
   - `TimerWarning(secondsRemaining)`: Alert banner pulsing at 1Hz.
   - `BossIntro(bossTitle)`: Full-width expanding banner announcing boss entry.
4. **Ambient Glow Ring:**
   - When punch-hole is centered, draws an animated 2.5dp ring around the camera lens:
     - Green: Correct response.
     - Red: Wrong response.
     - Yellow: Active combo streak.

---

### 5. Interactive Animation Lab

Developers and UX reviewers can test every animation, physics parameter, haptic tick, and notch pill event in real-time via the built-in **Animation Lab Screen**:
- **Location:** Settings $\rightarrow$ Visual Effects $\rightarrow$ Launch Animation Lab (`Routes.ANIMATION_LAB`).
- **Interactive Controls:**
  - Real-time tier toggling (`FULL` $\leftrightarrow$ `REDUCED` $\leftrightarrow$ `OFF`).
  - Haptic test triggers (Tap, Correct Combo 1..5, Error, Fizzle, Boss Warning).
  - Notch event dispatchers (+20 XP, Combo x3, Streak 7d, Heart Lost, Boss Intro).
  - Physics sandbox (Press-scale target, Sinusoidal shake trigger, 3D tilt gyro card, Confetti burst spawner).

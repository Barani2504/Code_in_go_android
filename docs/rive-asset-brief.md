# AshCrypt — Rive Mascot Asset Specification & Motion Brief

**Product**: AshCrypt (formerly Codeingo)  
**Target Mascot**: The Phoenix Companion (Evolution Stages 1 to 5)  
**Runtime**: `app.rive:rive-android` (Android native Jetpack Compose integration)  
**Deliverable**: `.riv` vector animation file(s) placed into `app/src/main/assets/rive/`

---

## 1. Asset Overview & Budget Requirements

| Asset Name | Target Path | Max KB Budget | Purpose |
| :--- | :--- | :--- | :--- |
| `phoenix_mascot.riv` | `app/src/main/assets/rive/phoenix_mascot.riv` | **≤ 180 KB** | Main companion reactive throughout dashboard, quiz sessions, and feedback drawers. |
| `phoenix_egg.riv` | `app/src/main/assets/rive/phoenix_egg.riv` | **≤ 90 KB** | Stage 1 Egg hatching ceremony upon boss victory and chapter completion. |

> **Size Delta Comparison**:  
> Legacy static GIF assets: ~4.5 MB to 7 MB per animation set.  
> Target Rive vector files: **~180 KB total**, yielding over **95% binary asset size savings**, infinite DPI resolution, 60/120 FPS buttery vector curves, and real-time state machine interaction.

---

## 2. Artboard & State Machine Architecture

### Artboard 1: `PhoenixMascot`
- **State Machine Name**: `PhoenixStateMachine`
- **Default State**: `Idle` (breathing, soft feather sway, blinking every 3.5s)

#### Inputs & Triggers:
| Input Name | Type | Expected Range / Values | Description |
| :--- | :--- | :--- | :--- |
| `evolutionStage` | Number | `1.0` (Egg), `2.0` (Hatchling), `3.0` (Fledged), `4.0` (Firebird), `5.0` (Divine Phoenix) | Dynamically swaps skin, crest, and tail plumage layers across chapters. |
| `emotionId` | Number | `0` to `28` | Matches AshCrypt's 29 dynamic launcher emotion variants. |
| `isThinking` | Boolean | `true` / `false` | Tilts head, raises wing to chin, glowing question mark aura. |
| `isCurious` | Boolean | `true` / `false` | Lean forward, eyes wide, perked ears/crest. |
| `isDetermined`| Boolean | `true` / `false` | Fierce brow, battle stance, subtle glowing feather tips. |
| `isSleepy` | Boolean | `true` / `false` | Night mode / quiet hours pose; head tucked into wing, slow breathing. |
| `celebrate` | Trigger | Fire on event | Level complete / correct answer; 360 spin, joyful flap, sparks particle burst. |
| `fail` | Trigger | Fire on event | Wrong answer; sympathetic head shake, drooped crest, puff of soft smoke. |
| `evolve` | Trigger | Fire on event | Unit master evolution; blazing radiant burst morphing to next plumage stage. |

---

### Artboard 2: `PhoenixEgg`
- **State Machine Name**: `EggHatchStateMachine`
- **Default State**: `GentlyRocking`

#### Inputs & Triggers:
| Input Name | Type | Expected Range / Values | Description |
| :--- | :--- | :--- | :--- |
| `wobbleSpeed` | Number | `0.0` to `3.0` | Accelerates wobbling intensity as boss health depletes. |
| `crackLevel` | Number | `0` (Smooth) to `4` (Heavily fractured) | Procedural crack line reveals glowing inner core. |
| `hatch` | Trigger | Fire on event | Egg shell shatters into shards revealing the Stage 2 Hatchling. |

---

## 3. Sensory Safety & Accessibility Standards

1. **Photosensitivity Safety**:  
   - No flashes, blinks, or color alternating faster than **3 Hz** (3 times per second).
   - High-contrast bursts must be clamped below 70% luminosity shift.
2. **Calm Mode / Reduce-Motion**:  
   - The state machine must support a static resting pose when `reduceMotion == true`. The app passes this flag to pause timelines and render a gentle resting frame.
3. **Looping Ethics**:  
   - Idle breathing loops must be organic and non-frenetic (cycle length ≥ 2.8 seconds).

---

## 4. Native Android Controller Contract

AshCrypt provides [`MascotController.kt`](file:///c:/Users/baran/AndroidStudioProjects/Codeingo/app/src/main/java/com/simats/codeingo/ui/mascot/MascotController.kt):
- Automatically loads `phoenix_mascot.riv` from assets if present.
- If `.riv` is missing or on low-end devices in battery-saver mode, automatically falls back to native Jetpack Compose vector/GIF renderers with identical emotion state bindings.
- Automatically pauses Rive rendering when the view scrolls off-screen or the activity enters background.
- Caps concurrent active instances to at most 2 across the entire application view hierarchy.

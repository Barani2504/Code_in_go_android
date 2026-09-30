# MASTER PROMPT: Implement the "Data Structures" Course in My Gamified Android Learning App

> Paste this whole document into your AI coding assistant (Claude Code, Cursor, etc.).
> Tell it to work **phase by phase** (see Section 14) and to confirm after each phase before moving on.

---

## 0. ROLE AND CONTEXT

You are a senior Android engineer and game-UX designer. I am building a **Duolingo-style gamified coding-learning app** in **Kotlin + Jetpack Compose (Material 3)**. I want you to implement a complete **"Data Structures" course** inside it. The course must feel like a game (Candy Crush juice, Duolingo bite-sized lessons), not like a textbook.

**Existing app assets you must integrate with (do not rebuild them):**
- A **Phoenix companion** that evolves through **18 stages** (egg → Phoenix Ascendant), rendered by `PhoenixCreature(stage: Float)` in Compose Canvas, with the level-up sequence in `PhoenixEvolutionScreen` (squash and stretch, particle burst, shockwave ring, shake, "LEVEL UP!" text). Stage names: Mystic Egg, Hairline Crack, Shaking Egg, Hatching, Ember Chick, Fluffy Flameling, Spark Fledgling, Ash Wing, Cinder Glider, Ember Flyer, Flame Sprite, Blaze Wing, Inferno Youngling, Sunfire Hawk, Radiant Firebird, Solar Phoenix, Eternal Ember, Phoenix Ascendant.
- Pre-rendered animated WebP for each stage (loaded with Coil) may be used in lists and cards. The live Canvas version is used in hero moments.
- Green primary color `#58CC02`, dark purple gradient background `#1B1B3A → #3B2A5C`.

**Tech stack to use (unless my project already has an equivalent):** Kotlin, Jetpack Compose, Material 3, Navigation-Compose, ViewModel + StateFlow, Hilt, Room, DataStore, Kotlin Coroutines/Flow, kotlinx.serialization, Coil, `androidx.compose.animation` (`Animatable`, `AnimatedContent`, `updateTransition`, `animate*AsState`, `spring`, `keyframes`, `rememberInfiniteTransition`), Compose `Canvas` and `graphicsLayer`. No heavy third-party game engines.

---

## 1. PRODUCT GOALS AND DESIGN PRINCIPLES

1. **Learn by doing.** Every concept must be *manipulated* by the learner (drag, tap, swipe, step, build) before it is *explained*. Max 20 seconds of reading between interactions.
2. **Visualize everything.** Each data structure has a live animated visualizer that reflects real state. Learners must see pointers move, elements shift, nodes rotate.
3. **Bite-sized.** A lesson is 5–8 exercises, about 3–5 minutes.
4. **Juicy feedback.** Every correct or wrong answer triggers animation, sound and haptics (Section 9).
5. **Every unit has a metaphor world** (Section 8) so learners remember concepts through story.
6. **Mistakes are teachable.** A wrong answer shows a *"why"* animation (e.g., a linked list losing its tail because pointers were rewired in the wrong order).
7. **Progression is visible:** path map, XP bar, streak, hearts, Phoenix evolution.
8. **Content-driven.** Lessons and exercises come from JSON in `assets/`, rendered by a generic engine, so adding content never requires new screens.
9. **Offline-first**, accessible, 60 fps on mid-range devices.

---

## 2. ARCHITECTURE

Use **Clean Architecture + MVVM**, feature-modular packages:

```
app/
 ├─ core/            (design system, theme, sound, haptics, extensions)
 ├─ data/            (Room, DataStore, content loader, repositories)
 ├─ domain/          (models, use-cases, game rules)
 ├─ feature/
 │   ├─ path/        (course map, unit/lesson nodes)
 │   ├─ lesson/      (lesson player, exercise renderers, result screen)
 │   ├─ visualizer/  (one visualizer per data structure + shared engine)
 │   ├─ sandbox/     ("Playground" operate-the-structure mode)
 │   ├─ boss/        (boss battle mini-games)
 │   ├─ phoenix/     (evolution integration)
 │   ├─ practice/    (spaced repetition hub, mistakes review)
 │   ├─ profile/     (stats, streak, achievements)
 │   └─ shop/        (gems, streak freeze, cosmetics)
 └─ assets/content/ds/   (unit_01_foundations.json ... unit_11_advanced.json)
```

### 2.1 Core domain models (Kotlin)

```kotlin
data class Course(val id: String, val title: String, val units: List<Unit>)
data class Unit(val id: String, val index: Int, val title: String, val difficulty: Difficulty, // GREEN, YELLOW, BLUE, RED
                val worldTheme: String, val lessons: List<Lesson>, val boss: BossBattle,
                val phoenixEvolutionsAwarded: List<PhoenixTrigger>)
data class Lesson(val id: String, val title: String, val type: LessonType, // NORMAL, CHALLENGE, CHECKPOINT, CHEST, BOSS
                  val exercises: List<Exercise>, val xpReward: Int)

sealed interface Exercise { val id: String; val prompt: String; val hint: String?; val explanation: String
    // see Section 5 for all subtypes
}
```

### 2.2 Visualizer engine (the heart of the course)

Build ONE generic, reusable **Step-Recorder engine**:

```kotlin
interface DsSimulator<S, Op> {           // S = structure state, Op = operation
    fun initial(): S
    fun apply(state: S, op: Op): List<Step<S>>   // returns animation steps
}
data class Step<S>(val state: S, val narration: String, val highlight: Highlight, val codeLine: Int?, val costDelta: Cost)
```

- A `VisualizerPlayer` composable takes `List<Step<S>>` and offers: **Play / Pause / Step forward / Step back / Speed (0.5x–3x) / Scrub slider**.
- It shows a **synchronized code panel** (current line highlighted) with a **language toggle** (Kotlin default, Java, Python, C++), plus a **narration bubble** spoken by the Phoenix.
- It shows a live **complexity meter** (comparisons, shifts, pointer changes, memory) so learners *feel* Big-O.
- Every exercise that says "operate" reuses the simulator, so the lesson checks the **learner's operation sequence against the simulator's ground truth**.

Compose implementation guidance: animate positions with `animateOffsetAsState`/`Animatable`, use `key()` in lists so items animate on reorder, draw arrows/edges on `Canvas` with animated path progress, use `AnimatedContent` for state swaps, and keep the state immutable so step-back works.

---

## 3. GLOBAL GAME SYSTEMS

Implement (Room + DataStore, exposed via repositories):

| System | Rules |
|---|---|
| **XP** | Lesson = 10 base XP + accuracy bonus (up to +10) + speed bonus (up to +5). Boss = 50 XP. |
| **Phoenix Evolution** | XP and boss wins drive the 18 stages (mapping in Section 10). |
| **Hearts** | 5 hearts. Wrong answer = -1. At 0: lesson fails, options: wait to regen (1 heart / 30 min), practice-to-earn, or spend gems. |
| **Streak** | Daily flame; streak freeze item; animated flame on the home top bar; milestone celebrations (3, 7, 30, 100). |
| **Gems / Embers** | Currency from lessons, chests, quests. Spend on heart refill, streak freeze, Phoenix skins. |
| **Combo** | Consecutive correct answers build a combo (x2, x3...). Candy-Crush-style escalating VFX and pitch-up sound. |
| **Daily quests** | 3 per day (e.g., "Finish 2 lessons", "Get 10 combo", "Beat 1 visualizer challenge"). |
| **Mastery** | Each topic has 5 crowns; repeating lessons at higher difficulty upgrades crowns (Legendary = gold). |
| **Spaced repetition** | "Practice Hub" resurfaces weak topics using a Leitner or SM-2 scheduler; wrong answers go into a "Mistake Vault". |
| **Achievements** | e.g., "Pointer Ninja" (reverse a list flawlessly), "Big-O Whisperer", "Tree Hugger", "Zero Collisions". |
| **Leaderboard** (optional flag) | Weekly leagues, local mock data first. |

---

## 4. COURSE PATH MAP UI (Duolingo-style)

- Vertical scrolling **zig-zag path** of circular lesson nodes inside each unit; a **sticky unit banner** that changes color per difficulty (🟢 green, 🟡 yellow, 🔵 blue, 🔴 red).
- Node states: locked (grey), available (pulsing glow via `rememberInfiniteTransition`), in-progress (ring), completed (gold crown), legendary.
- **Special nodes:** Chest (rewards), Checkpoint (mixed review), Boss (unit finale, larger with a skull/flame icon).
- Tapping a node opens a bouncy bottom sheet: title, XP, "START" button.
- The **Phoenix mascot walks or flies along the path** to the current node (animate along a `Path` with `PathMeasure`).
- A floating **Phoenix mini-card** on the top bar shows the current evolution stage and progress to the next.
- On unit completion: full-screen celebration + unit color transition + unlock animation of the next unit (lock shatter, chain break).

---

## 5. EXERCISE TYPE LIBRARY (build once, reuse everywhere)

Implement each as a composable with a shared `ExerciseHost` (handles check button, feedback banner, hearts, combo, hint, explanation, skip). Each supports **drag or tap fallback** for accessibility.

| # | Type | Interaction | Used for |
|---|---|---|---|
| 1 | `MultipleChoice` | Tap one option, animated reveal | Concept checks |
| 2 | `MultiSelect` | Tap all that apply | Properties, complexities |
| 3 | `TrueFalseSwipe` | Swipe card left/right | Quick facts, myth-busters |
| 4 | `MatchPairs` | Tap-tap or drag lines between columns | Term ↔ definition, structure ↔ use case |
| 5 | `OrderSteps` (Parsons) | Drag code lines / steps into correct order | Algorithms |
| 6 | `FillCode` | Tap tokens into blanks in a code snippet | Syntax, pointer logic |
| 7 | `PredictOutput` | Show code and structure state, learner picks resulting state | Tracing |
| 8 | `BugHunt` | Tap the buggy line, then choose the fix | Debugging |
| 9 | `BuildStructure` | Drag nodes/items onto a canvas to construct a structure | Layout, tree building |
| 10 | `OperateVisualizer` | Learner performs operations (tap positions, drag pointers); engine validates each step | Insert, delete, rotate |
| 11 | `ComplexityDial` | Slider/dial to pick O(1)...O(n²)...; graph curve animates | Big-O |
| 12 | `SortIntoBuckets` | Drag items into labeled buckets | Classification |
| 13 | `TraceRoute` | Tap nodes in the order an algorithm visits them | Traversals, BFS/DFS |
| 14 | `SpeedRound` | 30-second rapid-fire questions with combo | Review |
| 15 | `CodeSandbox` | Small editor with test cases (compile-time simulated with a safe interpreter or pre-defined snippet-slot checker) | Challenges |

Each exercise JSON carries: `id, type, prompt, payload, correctAnswer|validator, hint, explanation, difficulty, tags, conceptId`.

**Wrong-answer "Why" animation:** When wrong, replay the simulator to show what actually happens (e.g., the array shift skipping an element), then let the learner retry once for half XP.

---

## 6. SHARED INTERACTION PATTERNS

- **Pointer tokens:** draggable colored pins (`head`, `curr`, `prev`, `next`, `i`, `j`, `low`, `high`, `mid`, `top`, `front`, `rear`) that snap to nodes/indices.
- **Cost meter:** live counters (comparisons, moves, pointer updates) + Big-O badge.
- **"Undo" and "Explain this step"** buttons on every visualizer.
- **Phoenix hints:** tapping the Phoenix gives a progressive hint (Hint 1 = nudge, Hint 2 = stronger, Hint 3 = shows the answer with a XP penalty).
- **Challenge Mode toggle:** same visualizer, but the learner must find the operation sequence themselves under a timer.

---

## 7. CONTENT AUTHORING FORMAT (example)

```json
{
  "lessonId": "arrays_insertion",
  "title": "Shift Party",
  "concepts": ["array_insertion", "shifting", "on_cost"],
  "exercises": [
    { "type": "OperateVisualizer", "prompt": "Insert 42 at index 2. Tap the elements that must shift.",
      "payload": { "structure": "array", "initial": [10,20,30,40,50], "op": {"insert": {"index": 2, "value": 42}} },
      "validator": "simulator", "hint": "Everything from index 2 onward moves right.",
      "explanation": "Inserting in the middle shifts n-k elements, so cost is O(n)." },
    { "type": "ComplexityDial", "prompt": "Insert at the END (capacity available). Complexity?", "payload": {"options": ["O(1)","O(log n)","O(n)","O(n²)"]}, "correctAnswer": "O(1)" }
  ]
}
```

Create a **JSON schema + Kotlin `@Serializable` sealed classes** and a content validator that runs in debug builds and fails loudly on malformed content.

---

## 8. FULL CURRICULUM SPEC (implement ALL topics)

For **each topic** below, build (a) an interactive lesson of 5–8 exercises that includes the listed signature activity, (b) a visualizer or simulator step-through where applicable, (c) a "Quick Recap" flashcard, and (d) code samples in Kotlin/Java/Python/C++. Each unit ends with a **Challenges** node and a **Boss Battle**.

### 🟢 UNIT 1: Foundations — World: *"The Hatchery Warehouse"*
Metaphor: the Phoenix is organizing a chaotic warehouse of eggs.

| Topic | Signature interactive activity |
|---|---|
| What is a Data Structure? | **Messy Room Rescue:** find an item in a jumbled pile vs an organized shelf against a timer; learner drags items onto labeled shelves and sees search time drop. |
| Types of Data Structures | **Sort the Zoo:** drag structure cards (Array, Stack, Tree, Graph, HashMap...) into a taxonomy tree (Primitive / Non-primitive, Linear / Non-linear) that assembles a diagram as they succeed. |
| Linear vs Non-Linear | **Path or Web:** swipe cards showing diagrams into "Linear" or "Non-linear"; then *walk* each structure by tapping to see one-way vs multi-way movement. |
| Static vs Dynamic | **Elastic Bus:** a fixed-seat bus vs a growing bus; slider adds passengers; the static bus "overflows" with a comedic crash, the dynamic bus resizes (memory blocks animate). |
| Time & Space Complexity | **Big-O Racetrack:** cars labeled O(1), O(log n), O(n), O(n²) race as the learner drags the input size `n`; then `ComplexityDial` on real code snippets; "Space meter" fills with stack/heap usage. |

**Challenges:** mixed speed round. **Boss:** *The Chaos Egg* — classify 15 rapid items (structure or complexity) before the timer runs out.

### 🟢 UNIT 2: Arrays — World: *"Locker Row Avenue"*
| Topic | Signature activity |
|---|---|
| Array Basics | **Locker Row:** lockers with index labels; tap index to open; **address calculator** (`base + i × size`) with sliders; index-out-of-bounds "alarm". |
| Traversal | **Conveyor Sweep:** drag the `i` pointer; forward, backward, skip-step; assemble the `for` loop via `OrderSteps`. |
| Insertion | **Shift Party:** choose the insert position, elements slide right one by one; counter shows shifts; compare start / middle / end costs. |
| Deletion | **Close the Gap:** remove an element, remaining elements slide left; "ghost slot" mistake if the learner forgets to shift. |
| Searching | **Hi-Lo Master:** guess-the-number game that reveals binary search (halving visual, `low/mid/high` pins) then **race linear vs binary** on the same array; trap question: unsorted array breaks binary search. |
| 2D Arrays | **Battleship Grid:** tap `[row][col]`; toggle row-major vs column-major traversal; spiral and diagonal path challenges; rotate-matrix puzzle. |
| Array Challenges | Two Sum, max subarray, reverse in place, rotate by k, remove duplicates. Uses `OrderSteps` + `CodeSandbox` with test cases. |

**Boss:** *The Locker Thief* — array operations under time pressure with shifting lockers.

### 🟢 UNIT 3: Strings — World: *"The Scroll Scriptorium"*
| Topic | Signature activity |
|---|---|
| String Basics | Letters as tiles; indexing; **immutability demo** (try to change a tile, learn a new string is created); ASCII mini-table. |
| Traversal | **Highlighter:** sweep pointer to count vowels, consonants, digits. |
| Manipulation | **Word Forge:** drag tool cards (slice, concat, reverse, replace, split, join) onto a word to reach a target string. |
| Palindrome | **Mirror Chamber:** two pointers converge from both ends; learner decides match/mismatch at each step; includes ignore-case/punctuation twist. |
| Anagrams | **Letter Sieve:** drop letters into a 26-bucket frequency chart for both words; compare bars; contrast with the sorting approach. |
| String Challenges | Reverse words, first unique char, longest common prefix, valid anagram. |

**Boss:** *The Scrambled Scroll* — unscramble and validate strings using learned tools.

### 🟡 UNIT 4: Linked Lists — World: *"Chain Canyon"* (train cars)
| Topic | Signature activity |
|---|---|
| Singly Linked List | **Train Builder:** couple cars together; each car knows only the car behind it. |
| Node & Pointers | **Node Anatomy:** drag `value` and `next` into a node; **Pointer Surgery:** drag arrows to rewire; null terminator "cliff". |
| Insertion | **Rewire Order Puzzle:** insert at head, tail, middle; wrong step order makes the tail *fall off the cliff* (lost reference). |
| Deletion | Unhook a car; the orphan node fades with a garbage-collection animation. |
| Traversal | `curr` pin hops node to node; predict-the-output of `while(curr != null)`. |
| Reversal | **Three-Pointer Dance:** `prev / curr / next`, learner controls each step; step-back enabled. |
| Doubly Linked List | Two-way arrows; forward and backward run; insertion needs 4 pointer updates (pointer-count meter). |
| Circular Linked List | **Carousel:** tail connects to head; Josephus-style elimination game; **tortoise vs hare** race to detect the cycle. |

**Boss:** *The Runaway Train* — repair a broken chain using correct pointer operations.

### 🟡 UNIT 5: Stack — World: *"Pancake Tower Diner"*
| Topic | Signature activity |
|---|---|
| Stack Basics | Plates/pancakes stack up; only the top is reachable; LIFO rule; real-world examples (undo, browser back) as mini demos. |
| Push & Pop | **Tap PUSH / POP** on a tower; overflow and underflow animations with funny errors. |
| Stack using Array | `top` index pin moves; capacity meter; fill-code exercise. |
| Stack using Linked List | Push = new node becomes head; compare memory model with the array version side by side. |
| Parentheses Matching | **Bracket Bouncer:** a stream of brackets arrives; learner pushes and pops; mismatched bracket is thrown out with a bounce animation. |
| Expression Evaluation | **Railway Yard:** infix → postfix with a switching-track visual (shunting yard); then evaluate postfix on a calculator stack. |

**Boss:** *Diner Rush* — real-time orders that require correct push/pop sequences.

### 🟡 UNIT 6: Queue — World: *"Ember Café Rush"*
| Topic | Signature activity |
|---|---|
| Queue Basics | Customers line up; FIFO; first-served rule. |
| Enqueue & Dequeue | **Serve the Line:** tap ENQUEUE or DEQUEUE while customers arrive; patience meters rise. |
| Queue using Array | `front` and `rear` pins; show **wasted space** problem after many dequeues. |
| Circular Queue | Ring visual; `(rear+1) % size`; full vs empty distinction puzzle. |
| Deque | Double-ended tunnel; add/remove at both ends; sliding-window mini-game. |
| Priority Queue | **ER Triage:** patients with priority levels; drag to the right order; reveal heap-backed implementation preview. |

**Boss:** *The Café Stampede* — manage mixed queue types under timed rules.

### 🟡 UNIT 7: Hashing — World: *"Hash Harbor"* (magic locker keeper)
| Topic | Signature activity |
|---|---|
| Hashing Basics | **Magic Locker Assigner:** give a name, get a locker number instantly; compare with searching every locker. |
| Hash Function | **Craft Your Hash:** build `(sum of char codes) % m` visually; test distribution with a live histogram; "bad hash" that puts everything in one bucket. |
| Hash Table | Bucket array; insert, search, delete with animated index computation. |
| HashMap | Key → value phone-book UI; map API operations (`put/get/remove/containsKey`). |
| HashSet | **Club Bouncer:** duplicate entries are rejected; set operations (union, intersection). |
| Collision | **Two Guests One Locker:** birthday-paradox slider shows collision probability rising. |
| Collision Resolution | Side-by-side **chaining vs linear probing vs double hashing**; load-factor slider triggers **rehash** animation. |

**Boss:** *The Locker Storm* — resolve a flood of collisions with the right strategy.

### 🔵 UNIT 8: Trees — World: *"Ember Forest"*
| Topic | Signature activity |
|---|---|
| Tree Basics | Tap-to-label terminology (root, parent, child, leaf, height, depth, subtree) on a glowing tree. |
| Binary Tree | **Grow the Tree:** drag nodes to left/right slots; property checks (full, complete, perfect). |
| Tree Traversal, Preorder / Inorder / Postorder | **Forest Tour:** the Phoenix flies through the tree in the chosen order; learner predicts and taps the visiting order (`TraceRoute`); recursion call-stack panel; mnemonic cards. |
| Tree Traversal, Level Order | Wave-by-wave glow using a queue panel. |
| Binary Search Tree | **Plinko Insert:** drop a value from the root, it bounces left or right; search path highlights; delete cases (0, 1, 2 children) as three mini-puzzles. |
| AVL Tree | **Balance Doctor:** balance-factor badges; detect imbalance; drag-to-rotate LL / RR / LR / RL puzzles. (Foundation level here; advanced deletion appears in Unit 11.) |
| Heap | **Bubble-up Bubbles:** dual view (tree and array); insert with sift-up, extract with sift-down; min-heap vs max-heap toggle; heapify challenge. |

**Boss:** *The Twisted Oak* — repair an unbalanced, mis-ordered tree.

### 🔵 UNIT 9: Trie — World: *"Crystal Cave of Words"*
| Topic | Signature activity |
|---|---|
| Trie Basics | Letters as crystals branching down; end-of-word glowing marker. |
| Insert | Learner spells a word by tapping letters; crystals grow; shared prefixes reuse nodes (node counter vs plain list). |
| Search | Trace a word down the cave; distinguish "prefix exists" vs "word exists". |
| Delete | Remove a word safely (unmark vs prune) with edge cases (shared prefix). |
| Prefix Search | Type a prefix; matching branches light up; count words. |
| Autocomplete | **Mini Keyboard:** live suggestion strip driven by the trie; ranking by frequency. |

**Boss:** *The Whispering Wall* — complete the words the cave demands using prefix logic.

### 🔵 UNIT 10: Graphs — World: *"Sky Islands"*
| Topic | Signature activity |
|---|---|
| Graph Basics | Islands (vertices) and bridges (edges); build a graph by dragging; degree counting. |
| Directed Graph | One-way bridges; in-degree/out-degree; reachability quiz. |
| Undirected Graph | Two-way bridges; connected components highlighting. |
| Weighted Graph | Toll bridges with costs; find the cheapest route manually, then compare with the optimal. |
| Adjacency Matrix | Learner fills a grid from a drawn graph and vice versa; space cost meter (O(V²)). |
| Adjacency List | Drag neighbors into per-vertex lists; compare memory with the matrix on sparse and dense graphs. |
| BFS | **Ripple Flood:** waves spread from the start island; queue panel; shortest-path-in-edges discovery. |
| DFS | **Deep Diver:** explorer with a thread dives, backtracks; stack panel; maze mini-game; BFS vs DFS side by side. |

**Boss:** *The Storm Archipelago* — reach the goal island with limited moves using the right traversal.

### 🔴 UNIT 11: Advanced Structures — World: *"The Sun Citadel"* (final)
| Topic | Signature activity |
|---|---|
| Union-Find | **Kingdom Merge:** union and find with animated parent pointers; path compression flattening; union by rank; connected-components puzzle. |
| Segment Tree | **Range Query Tower:** learner selects a range and the tree highlights which nodes are combined; point update ripples up; sum/min/max toggle. |
| Fenwick Tree | **Lowbit Ladder:** binary index visual, `i & -i` jumps; prefix-sum queries and updates. |
| AVL Tree (advanced) | Deletion rebalancing chains, height maintenance, comparison vs BST worst case. |
| Red-Black Tree | **Color Court:** rules as laws; recolor vs rotate decisions via drag; insertion fix-up cases. |
| B-Tree | **Library Shelving:** disk-page metaphor; node split animation; order/degree slider; why databases use B-Trees. |

**Final Boss:** *The Eternal Flame Guardian* — a multi-phase battle mixing all previous worlds. Winning unlocks Phoenix Ascendant.

> **Note on duplication:** "AVL Tree" appears in both Unit 8 and Unit 11. Treat Unit 8 as *concept and rotations*, Unit 11 as *deletion, proofs of height bound, and comparison with Red-Black*. Do not repeat the same exercises.

---

## 9. JUICE, ANIMATION AND SOUND SPEC

| Event | Animation | Sound / Haptic |
|---|---|---|
| Correct answer | Green banner slides up with a bounce, checkmark draws itself, mini sparkle burst | Rising chime (pitch scales with combo), light haptic |
| Wrong answer | Card shakes horizontally with a spring, red banner, heart cracks and drops | Soft buzz, medium haptic |
| Combo x3 / x5 / x10 | Flame trail behind the XP counter, escalating particle intensity, screen-edge glow | Layered chimes |
| Lesson complete | Confetti, XP counting up, stars fill in sequence, Phoenix does a victory flap | Fanfare |
| Node unlock | Lock shakes then shatters, node pops with overshoot spring | Crack + whoosh |
| Phoenix evolve | Reuse the existing squash, burst, ring, shake and text pop | Big fanfare |
| Drag and snap | Slight scale-up while dragging, magnetic snap with spring on drop | Tick haptic |
| Heart loss at 0 | Dim screen, Phoenix sad animation (droop), offer refill sheet | Low tone |

Use `Animatable` for choreographed sequences, `spring(DampingRatioMediumBouncy)` for playfulness, `keyframes` for shakes, `rememberInfiniteTransition` for idle pulses. Provide a **"Reduce motion"** setting that swaps big effects for simple fades.

---

## 10. PHOENIX EVOLUTION MAPPING (18 stages across 11 units)

The Phoenix starts at Stage 1. There are **17 evolutions**. Evolutions trigger on (a) a **midpoint checkpoint** (for units with two evolutions) and (b) **beating the unit's boss**.

| Unit | Evolutions gained | Stage after unit |
|---|---|---|
| 1 Foundations | 2 | 3 (Shaking Egg) |
| 2 Arrays | 2 | 5 (Ember Chick), hatch cutscene at Stage 4 to 5 |
| 3 Strings | 1 | 6 |
| 4 Linked Lists | 2 | 8 |
| 5 Stack | 1 | 9 |
| 6 Queue | 1 | 10 |
| 7 Hashing | 2 | 12 |
| 8 Trees | 2 | 14 |
| 9 Trie | 1 | 15 |
| 10 Graphs | 2 | 17 |
| 11 Advanced | 1 | 18 (Phoenix Ascendant) |

Implementation notes:
- Store `phoenixStage` in Room/DataStore; expose as `StateFlow`. A `PhoenixEvolutionUseCase` decides when to fire an evolution event.
- On trigger, navigate to the **Evolution Celebration screen** (reuse `PhoenixEvolutionScreen` logic) showing old stage → new stage with the full sequence, then return to the path map.
- Show the current Phoenix stage on the path map header; tapping opens a **Phoenix Gallery** (all 18 stages, locked ones as silhouettes).
- The Phoenix reacts to context: cheers on correct answers, worried at 1 heart, sleeps if streak is at risk, glows brighter with high combos.

---

## 11. BOSS BATTLES (unit finales)

Build a `BossBattleScreen` framework: boss with a health bar, learner has 3 hearts, each correct action damages the boss, wrong action damages the learner. Each boss uses a **themed mini-game** from Section 8 with a time limit, escalating in phases (phase 2 speeds up, phase 3 adds a twist). Bosses drop chests. Losing lets the learner retry with a hint or study the weak topics.

---

## 12. PRACTICE HUB, MISTAKE VAULT AND CHALLENGES

- **Mistake Vault:** every wrong exercise is stored with concept tag; the learner can replay them as a "Repair" session for bonus XP.
- **Daily Review:** 5 spaced-repetition exercises chosen by the scheduler.
- **Free Play Sandbox:** for every structure, an open playground with operation buttons, random-data generator, and the step-through code panel.
- **Interview Mode (unlocked per unit):** curated real-interview problems with hints and complexity questions.

---

## 13. NON-FUNCTIONAL REQUIREMENTS

- **Performance:** 60 fps; use `remember`, `derivedStateOf`, stable/immutable state, `key` in lazy lists, avoid allocations in `Canvas` draw scopes; cap particle counts (e.g., 60); test on low-end device profiles.
- **Accessibility:** TalkBack labels for all visualizer elements, tap alternatives for drag, high-contrast mode, color-blind-safe palettes (never rely on color alone; add shapes/icons), scalable fonts.
- **Offline:** all content bundled; progress synced later if backend added.
- **Localization-ready:** all strings in `strings.xml`; content JSON supports language keys.
- **Testing:** unit tests for every simulator (operation sequences vs expected states), exercise validators, XP/hearts/streak logic, phoenix evolution triggers; Compose UI tests for the lesson player; screenshot tests for visualizers; JSON content validation test.
- **Code quality:** Kotlin style guide, KDoc on public APIs, no business logic inside composables, previews for every composable.
- **Analytics hooks** (interface only): lesson_start, exercise_result, hint_used, boss_result, evolution_unlocked.

---

## 14. IMPLEMENTATION PHASES (do them in order and stop for review after each)

1. **Foundation:** project structure, theme, Room/DataStore, content models, JSON loader and validator, navigation skeleton.
2. **Global systems:** XP, hearts, streak, gems, combo, daily quests, Phoenix stage state and evolution use-case.
3. **Path map UI:** zig-zag path, unit banners, node states, Phoenix header, unlock animations.
4. **Lesson player + exercise library:** `ExerciseHost` and all 15 exercise types with feedback, hints, "why" replay.
5. **Visualizer engine:** `DsSimulator`, `Step`, `VisualizerPlayer`, code panel with language toggle, complexity meter.
6. **Units 1–3** (Foundations, Arrays, Strings) content and their visualizers + boss battles + first two Phoenix evolutions.
7. **Units 4–7** (Linked List, Stack, Queue, Hashing) content, visualizers, bosses, evolutions.
8. **Units 8–10** (Trees, Trie, Graphs) content, visualizers (tree layout, trie, graph canvas), bosses, evolutions.
9. **Unit 11** (Advanced) content, visualizers, final boss, Phoenix Ascendant finale.
10. **Practice Hub, Mistake Vault, Sandbox, Interview Mode, achievements, shop.**
11. **Polish:** sound, haptics, reduce-motion, accessibility, performance profiling, tests, content QA.

**For each phase, deliver:** the full source files (not pseudo-code), a short summary of what was built, what is stubbed, how to run and test it, and any decisions you made that I should review.

---

## 15. RULES FOR YOU (the assistant)

- Do **not** skip topics; every item in Section 8 must have a lesson, a signature interactive activity and a recap.
- Do **not** use placeholder "TODO" logic in simulators. Every operation must be correct and unit-tested.
- Prefer reusable components over one-off screens; if you duplicate code twice, extract it.
- Ask me before adding any new dependency that is not in Section 0.
- If something in this spec is ambiguous or conflicts with my existing code, list the options briefly and pick the most sensible default, then continue.
- Keep the tone of in-app copy friendly, short, and a little playful, spoken by the Phoenix.

**Begin with Phase 1 now.**

# Android Modernization Roadmap (Hostile Takeover)

## Repository analysis

### 1) Core engine
- The deterministic RTS gameplay core is in `game/` (simulation, units, map logic, UI/forms, and networking abstractions). Representative files include `Simulation.cpp`, `Multiplayer.cpp`, `xtransport.cpp`, and unit/building classes.  
- Shared low-level runtime/networking utilities live in `base/` (`socket`, message queues, threads, timers).  
- Cross-platform transport and host integration for SDL lives in `game/sdl/`.

### 2) Rendering system
- Android currently runs through SDL entry points (`game/sdl/main.cpp`, `game/sdl/display.cpp`) and Java SDL bridge (`game/sdl/android/src/org/libsdl/app/SDLActivity.java`).
- Android launcher/activity wrapper is `game/sdl/android/src/com/spiffcode/ht/GameActivity.java`.
- Current rendering path is SDL-driven; lifecycle glue is Java-side activity callbacks.

### 3) Asset pipeline
- Runtime data is a packed PDB (`htdata*.pdb`, `htsfx.pdb`) consumed by readers in `game/filepdbreader.cpp`, `game/mempdbreader.cpp`, and load/save systems.
- Build pipeline is legacy toolchain driven from `data/makefile` and helper tools under `bin/`, `packpdb2/`, `inicrunch/`, `wavcrunch/`, etc.
- Android activity currently expects packed assets copied from APK assets into app-internal storage at startup.

### 4) Multiplayer code
- Client multiplayer stack is in `game/Multiplayer.cpp`, `game/lobby*.cpp`, `game/roomform.cpp`, `game/xtransport.cpp`, and transport managers in platform folders.
- Dedicated server code is in `server/` (lobby/game rooms, stats posting, endpoint management, command processing).
- Service endpoint defaults are centralized in `game/serviceurls.cpp`.

---

## Phase roadmap

## Phase 1 — Minimal playable Android Studio build (current focus)
**Goal**: compile and launch the existing SDL Android port from Android Studio/Gradle with API 24+ baseline, without gameplay rewrites.

**Approach**
1. Keep legacy Java + native engine intact.
2. Introduce Gradle Kotlin DSL build (`game/sdl/android`) while preserving `ndk-build` integration.
3. Raise Android API floor/target to modern baseline (minSdk 24, target/compileSdk current stable in project files).
4. Document known gaps (asset packaging and Java runtime/toolchain constraints).

**Validation criteria**
- Android module syncs in Android Studio.
- `debug` variant is discoverable.
- Native build is invoked from Gradle via `externalNativeBuild.ndkBuild`.
- App starts to SDL splash/game shell when required PDB assets are present in APK assets.

## Phase 2 — Kotlin-first app shell + lifecycle hardening
**Goal**: add Kotlin entry layer around legacy engine.

- Introduce Kotlin `GameHostActivity` and lifecycle coordinator.
- Move Java-only helper logic behind Kotlin wrappers (do not rewrite engine).
- Ensure pause/resume/destroy correctness for SDL/native thread ownership.
- Replace legacy storage assumptions with scoped, secure app-internal paths.

## Phase 3 — Content source modernization + fallback mirrors
**Goal**: preserve community content availability.

- Implement configurable mission/content source registry:
  - official legacy URL
  - mirror URL
  - local/offline cache fallback
- Add in-app selector and health checks.
- Keep deterministic mission parsing untouched.

## Phase 4 — Multiplayer mode matrix (self-host + LAN/P2P + optional Bluetooth)
**Goal**: modernize connectivity without central lock-in.

- Expose mode selection UX:
  - custom self-hosted server
  - LAN direct peer/server IP
  - optional Bluetooth session bootstrap
- Document and enforce trust model differences:
  - server-authoritative vs peer-hosted
  - cheat surface and mitigation options
- Add secure defaults (TLS where applicable, input validation, no embedded secrets).

## Phase 5 — Incremental Kotlin migration + extensibility
**Goal**: future-proof architecture with deterministic safeguards.

- Gradual Java→Kotlin conversion on non-core layers first.
- Introduce extension points for higher-res textures, extra maps/units, and audio expansion.
- Add regression harnesses around simulation and sync-sensitive logic.

---

## Phase 1 implementation status

Implemented in this change:
1. Added Gradle Kotlin DSL Android project files in `game/sdl/android`.
2. Wired `externalNativeBuild` to existing `jni/Android.mk` so native engine integration remains unchanged.
3. Updated manifest SDK floor/target to API 24/35.

Notes:
- This keeps legacy behavior while making Android Studio import/sync straightforward.
- Actual first-run playability still depends on packaging required `htdata*.pdb`/`htsfx.pdb` into APK assets.

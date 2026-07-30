# AGENTS.md

Instructions for AI agents working in this repository. Read this before making
any change.

## What this project is

Ante is an offline-first shared-expense ledger for Android. It is a portfolio
project whose explicit purpose is demonstrating Senior/Staff-level Android
engineering - the author must be able to explain any file cold and extend the
core algorithms live, with AI off. That purpose defines a hard boundary on what
AI may write.

## The AI boundary (most important rule)

**Do not write, complete, or "fix" the domain core.** That means:

- The `Money` value class and its allocation/split math (`:core:model`)
- The debt-minimization settle-up algorithm
- Sync and conflict-resolution logic
- The substance of ADRs (`docs/adr/`), `docs/DESIGN.md`, and this file's
  judgment calls - these are author-voice documents

If a task appears to require touching any of the above, stop at the boundary:
build the module structure, wiring, and test scaffolding _around_ it, leave the
core empty or stubbed, and say explicitly that the domain logic was left for the
author. Do not fill in a stub "just to make it compile" - a `TODO()` body is the
correct end state.

**Fair game:** Gradle/build-logic and convention plugins, version catalog, CI,
project scaffolding, module stubs, test infrastructure and scaffolds (not the
property-test assertions for money/settlement), boilerplate, formatting.

## Build and verify

CI runs exactly this on every PR (JDK 21 toolchain, Java 17 bytecode):

```
./gradlew assemble lint test spotlessCheck
```

Run it before declaring work done. Formatting is Spotless + ktfmt
(`kotlinlangStyle`); fix violations with `./gradlew spotlessApply`, never by
hand-tuning. Configuration cache, build cache, and parallel execution are all
enabled - keep any build-logic change configuration-cache compatible.

## Module map

- `:app` - the Android application. Namespace/applicationId is
  `io.appkitchen.ante`.
- `:core:model` - plain JVM library (no Android deps); holds `Money`. Domain
  core: see the boundary above.
- `build-logic/` - included build (`io.appkitchen.ante.buildlogic`) with the
  convention plugins: `ante.android.application`, `ante.android.compose`,
  `ante.jvm.library`.

New modules apply a convention plugin, never raw `plugins { }` configuration. Do
not create empty placeholder modules - a module arrives with its first real
consumer.

## Deliberate decisions that look like mistakes

Do not "correct" these:

- **detekt is absent** on purpose (incompatible with AGP 9's built-in Kotlin at
  1.23.8; 2.0.0-alpha disables configuration cache). Spotless is the enforced
  check. Revisit at detekt 2.0 stable.
- **AGP is `compileOnly` in build-logic**, and the root build's `apply false`
  aliases are what put it on the runtime classpath. Removing them breaks
  `ante.android.application` with `NoClassDefFoundError`.

## Docs and ADRs

Architecture decisions live in `docs/adr/` (MADR full template, managed via
`adrs` CLI and the `adrs.toml` config). ADRs and `docs/DESIGN.md` are
author-written - agents may fix broken links or formatting, nothing more.

## Hygiene

- Never commit `local.properties` or `*.local.md` files.
- Match existing code style and comment density; comments explain constraints,
  not narration.
- Commit or push only when asked.

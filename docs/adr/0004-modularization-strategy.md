---
number: 4
title: Modularization Strategy
status: proposed
date: 2026-07-30
---

# Modularization Strategy

## Context and Problem Statement

That Ante is multi-module is not really the question. What this decision settles
is the naming convention, how deeply modules are isolated from each other, how
build configuration is shared between them, and which dependency edges are
forbidden.

The repo currently contains one module, `:app`. Every module added after this
lands inherits the answer, so the cost of choosing badly compounds rather than
staying local.

How much module isolation does a repo this size actually earn?

## Decision Drivers

- Single developer, small graph: isolation that costs more than it returns is a
  real loss, not a neutral one.
- Module names should be ones a reviewer already recognizes.
- Build configuration must be shared rather than copied; adding a module should
  mean applying an archetype, not pasting an `android { }` block.
- Dependency rules have to be enforceable. A rule that lives only in a document
  is a rule that gets broken during a late-night refactor.
- ADR-0001 chose one presentation architecture for every module, so there is no
  second-paradigm module to isolate; modules divide by responsibility only.
- `:core:model` carries the money and settle-up logic from ADR-0002 and
  ADR-0003, whose tests should run on a plain JVM.

## Considered Options

- Single module
- Now-in-Android style
  - `core:*` / `feature:*` with convention plugins
- `:api` / `:impl` / `:fakes` per feature
- Now-in-Android style, with one feature demonstrating `:api` / `:impl` /
  `:fakes`

## Decision Outcome

Chosen option: "Now-in-Android style", because it abstracts enough without
adding too much complexity for a single-dev project at this scale.

Module dependencies must be declared in-to-out. `core:*` modules may not depend
on `feature:*` modules. `:app` is the Android application aggregator for all
`feature:*` modules and no other module may depend on it.

All `feature:*` modules are Android libraries, `:app` is the only Android
application. Most of the `:core:*` are JVM only. `:core:designsystem` or UI
focused core modules are Android libraries.

### Consequences

- Good, because reviewers will be familiar with the design; the vocabulary is
  the one `nowinandroid` taught the ecosystem.
- Good, because a new module costs a directory, a one-line archetype plugin
  block, and its own dependency list - nothing else. `compileSdk`, Java
  compatibility, and lint configuration all come from the archetype.
- Good, because `core:model` stays a plain JVM module, so the ADR-0002 money
  tests and ADR-0003 planner tests run without an Android runtime.
- Bad, because the layout does not structurally prevent forbidden edges
  (feature-to-feature especially). Until the build-time graph check described
  under Confirmation lands, enforcement is review-only - a gap this ADR treats
  as debt, not as an accepted end state.
- Bad, because if the graph grows or a second contributor arrives, the absence
  of `:api`/`:impl` separation means implementation changes recompile downstream
  modules, and the migration to interface modules gets more expensive the longer
  it waits.

### Confirmation

Dependency rules are enforced by a check rather than by review. The project
graph is inspected at build time and forbidden edges fail the build; the
archetype convention plugins are where that check is registered. Documented
rules with no failing build behind them do not count as confirmation.

CI already runs `./gradlew assemble lint test spotlessCheck` on every pull
request and on `main`, so the graph check joins that command rather than
becoming a separate optional job.

Two review-time signals that the decision is being applied:

- A module build file that configures `compileSdk`, `minSdk`, or Java
  compatibility directly has bypassed its archetype plugin.
- A module that applies the Compose archetype without containing composables is
  paying the Compose compiler for nothing.

Static analysis is currently limited to ktlint via Spotless. Detekt is absent
for the reason recorded in `gradle/libs.versions.toml`: no released version
works against AGP 9's built-in Kotlin with the configuration cache enabled.

## Pros and Cons of the Options

### Single module

All source in `:app`.

- Good, because there is no build wiring and no dependency rules to enforce.
- Good, because it is the fastest layout to move in at this size.
- Neutral, because incremental build time is not yet a problem worth solving.
- Bad, because nothing prevents UI code from reaching directly into database
  code; the boundaries exist only as package names.
- Bad, because the design-system catalog app in DESIGN.md §4.1 needs the design
  system as a separately consumable module.
- Bad, because test scoping degrades to whole-app runs, and the ADR-0002 and
  ADR-0003 property tests want a plain JVM module.

### Now-in-Android style `core:*` / `feature:*` with convention plugins

`core:model`, `core:data`, `core:database`, `core:network`, `core:designsystem`,
`core:ui`, and one module per feature, each applying a shared archetype plugin.

- Good, because the vocabulary is the one most Android reviewers already read
  fluently.
- Good, because the boundaries follow responsibility, matching how the graph is
  already drawn.
- Good, because an archetype plugin reduces a new module's build file to a
  plugin block and its own dependencies.
- Good, because it lets `core:model` be a plain JVM module, so money and
  settle-up tests need no Android runtime.
- Neutral, because the dependency rules have to be written down and enforced;
  the layout alone does not imply them.
- Bad, because nothing in the layout stops feature-to-feature dependencies,
  which is the edge most likely to be added by accident.

### `:api` / `:impl` / `:fakes` per feature

Each feature exposes an interface module, hides an implementation module, and
ships a fakes module for consumers' tests.

- Good, because consumers compile against interfaces, so changing an
  implementation does not recompile downstream modules.
- Good, because fakes are first-class and shared, which keeps tests off mocking
  frameworks.
- Good, because it is the layout that survives many teams editing one graph.
- Neutral, because it requires a dependency-injection story to bind
  implementations to interfaces at the app boundary.
- Bad, because it roughly triples module count for a single-developer repo.
- Bad, because reading one feature means opening three modules.
- Bad, because the recompile-avoidance benefit is not measurable at a build this
  small, so the cost is paid up front against a return that arrives later or
  never.

### Now-in-Android style, with one feature demonstrating `:api` / `:impl` / `:fakes`

The graph is Now-in-Android style throughout, except one feature that is split
three ways.

- Good, because the pattern is present and reviewable without being paid for
  across the whole graph.
- Neutral, because the extra structure is bounded and can be reverted to the
  house pattern cheaply.
- Bad, because two module conventions coexist, and a reader has to work out why
  one feature is shaped differently.
- Bad, because the split feature's fakes are the only fakes, so the testing
  approach is inconsistent across the repo.

## More Information

What already exists and why, so this record matches the build rather than
describing an intention:

Build configuration lives in `build-logic`, an included build, rather than in
`buildSrc`. `buildSrc` sits on every build's classpath and any change to it
invalidates the whole build; an included build is a normal Gradle build whose
plugins are cached independently. The version catalog at
`gradle/libs.versions.toml` is shared into `build-logic` by path, so the
convention plugins and the modules they configure resolve identical versions.

Compose is a separate archetype (`ante.android.compose`) rather than being
folded into the module archetypes, so modules with no UI do not pay the Compose
compiler's per-module cost. This is the modularization choice with the most
direct effect on build time at this size.

Still missing for the graph in DESIGN.md §4.1: an Android library archetype and
a plain JVM library archetype. `core:model` should take the JVM one - it holds
the `Money` type from ADR-0002 and the settle-up planner from ADR-0003, and
neither needs the Android runtime. Keeping that module free of Android is what
lets those property tests run as ordinary JVM tests rather than under
Robolectric.

Kotlin Multiplatform is the trigger to revisit. A KMP data layer would push
`core:model` and `core:data` into `commonMain` source sets, which changes the
archetype plugins rather than the module names.

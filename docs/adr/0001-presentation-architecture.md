---
number: 1
title: Presentation Architecture
status: proposed
date: 2026-07-28
---

# Presentation Architecture

## Context and Problem Statement

A presentation architecture is needed that is easy to review and reason about by
a typical Android developer.

## Decision Drivers

- Easy to review
- Library/Platform is well-documented
- Clear testing story
- Arch known by dev (logr)

## Considered Options

- ViewModel + StateFlow everywhere
- ViewModel + StateFlow; One bounded Molecule module
- Molecule/Composable presenters everywhere

## Decision Outcome

Chosen option: "ViewModel + StateFlow everywhere", because most Android
engineers will be familiar with the tools and patterns making it easier to
review and it is a platform the dev (me, logr) is already familiar with.

### Consequences

- Good, because Google backed architecture convention
- Good, because used in many community reference apps (e.g. `nowinandroid`)
- Good, because plenty of documentation to reference
- Good, because the architecture is one I know well
- Good, because one consistent architecture for all modules.
- Bad, because no demonstration of Molecule
- Bad, because presentation layer is not fully JVM-testable

## Pros and Cons of the Options

### ViewModel + StateFlow everywhere

- Good, because it is a common setup recommended by Google and the Android team
  specifically.
- Good, because the dev knows the arch well (used in a production app with prior
  job).
- Neutral, because supports unidirectional data flow.
- Neutral, because it unifies the whole project on one arch.
- Bad, because it does not demonstrate Molecule.

### ViewModel + StateFlow; One bounded Molecule module

Same as the ViewModel + StateFlow everywhere option but replacing a single
module with Molecule.

- Good, because most of the arch is known to the dev.
- Good, because it demonstrates Molecule.
- Neutral, because it introduces two separate arch ideas into one project.
- Bad, because it requires further decisioning upfront; which module? how to
  interface with primary arch?

### Molecule/Composable presenters everywhere

- Good, because it demonstrates Molecule throughout, not as a one-off.
- Good, because presenters become plain composable functions, making the whole
  presentation layer JVM-testable without instrumentation.
- Neutral, because the project stays on one consistent architecture, just a less
  common one.
- Bad, because most reviewers will not read it fluently; the architecture itself
  becomes the thing being reviewed.
- Bad, because the dev has not run it in production; every problem is a novel
  problem on the project's critical path.

## More Information

The Molecule gap _could_ be resolved at a later date with a rewrite branch as a
source of comparison. That is deferred in favor of shipping v1 on one
architecture; the bounded Molecule "dialect module" originally sketched in the
5-week build plan is descoped in line with this decision.

---
number: 2
title: Money Representation
status: proposed
date: 2026-07-28
---

# Money Representation

## Context and Problem Statement

Money math needs to be exact; floating point cannot represent 0.1 exactly.
Splits can produce remainders.

Where does rounding live and who absorbs the remainder?

## Decision Drivers

- Only single-currency support is needed
- Division is the hard part
- Split-math must be deterministic
- Offline-first; Serializable representation

## Considered Options

- `Money` value class with `long` minor units
- Raw java.math.BigDecimal
- Wrapped java.math.BigDecimal
- Joda-Money

## Decision Outcome

Chosen option: "`Money` value class with `long` minor units", because it comes
out best (see below). Remainder allocation is deterministic going to first
member sorted by ID.

### Consequences

- Good, because industry-standard pattern.
- Good, because easily serializable; easy to store on disk.
- Good, because can compile-time enforce correct behavior.
- Neutral, because it is owned code (mitigated with tests).
- Bad, because more work to support multi-currency.
- Bad, because the same member takes the extra share in an uneven split.

### Confirmation

The `Money` value class will be unit tested to ensure accuracy.

## Pros and Cons of the Options

### `Money` value class with `long` minor units

- Good, because exact integer arithmetic.
- Good, because easily serializable; easy to store on disk.
- Good, because can compile-time enforce correct behavior.
- Neutral, because it requires custom handling.
- Neutral, because unaware of currencies initially.
- Bad, because the math is owned in the project.

### Raw java.math.BigDecimal

- Good, because arithmetic is exact for `+`, `-`, and `×`.
- Good, because rounding modes are provided; no owned math.
- Neutral, because it is already on the JVM; no new dependency.
- Bad, because `equals` is scale-sensitive; `1.0` does not equal `1.00`.
- Bad, because the `double` constructor silently imports float error.
- Bad, because division throws unless given a `RoundingMode`.
- Bad, because storage and serialization both need converters.
- Bad, because splits still need an allocator written by hand.

### Wrapped java.math.BigDecimal

Same as the raw option, but behind a domain type that pins scale internally to
hide the `equals` and rounding traps.

- Good, because it hides the scale-sensitive `equals` trap.
- Good, because it gives the domain a real vocabulary type.
- Neutral, because the owned code moves from the math to the wrapper.
- Bad, because it keeps every storage and serialization cost of the raw option.
- Bad, because splits still need an allocator written by hand.
- Bad, because it is more machinery than minor units for no added correctness.

### Joda-Money

A small, mature JVM library; scale fixed to the currency, backed by BigDecimal.

- Good, because it is battle-tested and well-maintained.
- Good, because it is currency-aware without extra work.
- Neutral, because it trades a dependency for not owning arithmetic.
- Bad, because currency-awareness solves a v1 non-goal.
- Bad, because the Java API has no operators or value semantics.
- Bad, because storage and serialization both need converters.
- Bad, because splits still need an allocator written by hand.

## More Information

Multi-currency is the trigger to revisit. Supporting it means adding a currency
code and a minor-unit scale to the type (JPY has 0 decimals, BHD has 3) plus a
migration - contained, but not free.

Determinism reaches past this decision. Sync and conflict resolution assume two
devices splitting the same expense land on the same answer, so allocation order
must be stable (sorted by member ID) rather than incidental.

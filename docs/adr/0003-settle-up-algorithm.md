---
number: 3
title: Settle Up Algorithm
status: proposed
date: 2026-07-29
---

# Settle Up Algorithm

## Context and Problem Statement

Balances derived from the expense log sum to zero. Members who paid more than
their share are creditors, members who paid less are debtors. Settling up means
producing a set of transfers that clears every balance, and many valid sets
exist for the same ledger.

Which set does Ante produce, and what does it promise about it?

## Decision Drivers

- Transfer count is the only objective worth optimizing; total amount moved is
  fixed at the sum of the positive balances for any plan without wasted
  transfers.
- Groups are small; realistically under fifteen members with nonzero balances.
- Two devices planning from the same ledger must produce the same plan (see
  ADR-0002).
- Netting across the group changes who pays whom, which is a product decision
  and not only an algorithmic one.
- The plan is computed on-device, offline, with no server round trip.

## Considered Options

- Per-expense settlement; no netting
- Pairwise netting between members who shared an expense
- Greedy; largest debtor pays largest creditor
- Exact minimum transfer count, with a greedy fallback above a size threshold

## Decision Outcome

Chosen option: "Greedy; largest debtor pays largest creditor", because it is the
smallest amount of code that keeps every promise Ante actually makes. Minimality
is not one of those promises: the plan is at most n - 1 transfers,
deterministic, and total-preserving, and greedy delivers all three in O(n log n)
with a single code path to test. The exact algorithm's ~0.1 extra transfers
saved per plan (see More Information) does not buy back the cost of a subset-DP
implementation, a documented threshold, and a second path to keep correct.

Group-wide netting is **opt-in**, following the Splitwise precedent. By default,
balances are netted pairwise, so a member only ever pays people they shared an
expense with, and the plan is the pairwise net itself. When a group enables
simplification, the planner runs greedy over group-wide netted balances and the
transfer count drops accordingly.

Ties - equal balances competing for largest debtor or largest creditor - resolve
by member ID, matching the allocation rule in ADR-0002, so two devices planning
from the same ledger produce byte-identical plans.

### Consequences

- Good, because the plan is bounded (at most n - 1 transfers), deterministic,
  and simple enough to explain in one sentence.
- Good, because by default the user is only ever asked to pay someone they
  shared an expense with; the surprising pairings group-wide netting produces
  appear only after an explicit opt-in.
- Good, because there is one planner code path, one set of property tests, and
  no size threshold to document or defend.
- Bad, because the plan is not minimal, and the shortfall is not rare - roughly
  one plan in ten uses an extra transfer. Ante therefore claims "at most n - 1
  transfers," never "the fewest possible."
- Bad, because the smallest counterexample (five members, see More Information)
  is simple enough that a curious reviewer can find it; the non-minimality is
  documented and test-pinned rather than discoverable by surprise.

### Confirmation

The allocator and the planner are separate functions and are tested
independently. Deriving balances from the expense log is covered by ADR-0002;
this decision covers only the balances-to-transfers step, which never divides
and so has no rounding behavior of its own.

Property tests over randomized balance vectors that sum to zero:

- For every member, transfers received minus transfers sent equals that member's
  balance.
- Every transfer amount is positive.
- No member both sends and receives.
- Transfer count is at most n - 1 for n nonzero balances.
- Total amount moved equals the sum of the positive balances.
- The same balances produce an identical plan, including ordering.

There is no minimality oracle, because there is no minimality claim. Instead,
the five-member counterexample from More Information is pinned as a
characterization test: it asserts greedy produces four transfers where three
exist, so the known shortfall is documented in the test suite rather than
rediscovered later and mistaken for a regression.

## Pros and Cons of the Options

### Per-expense settlement; no netting

Every participant repays the payer of each expense directly.

- Good, because every transfer maps to an expense the two members shared.
- Good, because it needs no algorithm beyond walking the ledger.
- Neutral, because it is the behavior users get if this feature does not exist.
- Bad, because transfer count grows with the number of expenses rather than the
  number of members.
- Bad, because it asks for many small payments that people will not make.

### Pairwise netting between members who shared an expense

Balances are netted between pairs only, never across the group.

- Good, because a member only ever pays people they shared costs with.
- Good, because the result needs no explanation to the user.
- Neutral, because it is bounded by member pairs rather than by expenses.
- Bad, because it leaves cycles in place; A pays B, B pays C, C pays A.
- Bad, because transfer count stays well above the minimum.

### Greedy; largest debtor pays largest creditor

Net all balances group-wide, then repeatedly transfer the smaller of the largest
credit and the largest debt.

- Good, because it is short, obvious, and O(n log n).
- Good, because it always terminates in at most n - 1 transfers.
- Good, because every transfer zeroes at least one member, which makes it easy
  to reason about.
- Neutral, because it needs an explicit tie-break rule to be deterministic.
- Bad, because it is not minimal, and the shortfall is not rare; on randomized
  balance vectors it exceeds the minimum in roughly one case in ten.
- Bad, because minimality would be a claim the code does not keep.

### Exact minimum transfer count, with a greedy fallback

The minimum is n - c, where c is the largest number of disjoint subsets of
members whose balances each sum to zero. Any plan's transfer graph splits into
components that each must sum to zero and each cost at least one transfer fewer
than their member count, which is where the formula comes from. c is found by
subset dynamic programming.

- Good, because the minimality claim becomes true and testable.
- Good, because it is fast at real group sizes; under a millisecond up to twelve
  nonzero balances, about twenty milliseconds at seventeen.
- Neutral, because the general problem is NP-hard, which is the reason for the
  threshold rather than a compromise hidden by it.
- Bad, because it is materially more code than the greedy option.
- Bad, because it needs a documented threshold and a second path to test.

## More Information

Greedy is optimal for groups of four or fewer; that was checked exhaustively for
balances up to nine minor units. The smallest counterexample has five members:

    A -4   B -3   C +2   D +2   E +3

    greedy    A→E 3, B→D 2, A→C 1, B→C 1          4 transfers
    minimum   {B,E} and {A,C,D} each sum to zero  5 - 2 = 3 transfers
              A→C 2, A→D 2, B→E 3

Over roughly 22,000 randomized vectors of four to eight nonzero balances, greedy
exceeded the minimum in 9.6% of cases, by an average of 1.03 transfers.

Group-wide netting carries a cost that is not technical. It can tell a member to
pay someone they never shared an expense with, which reads as a bug to anyone
who remembers the original expenses. Splitwise ships the equivalent as an opt-in
"simplify debts" setting rather than as the default.

A plan is derived from a snapshot of balances. If an expense lands between
showing a plan and confirming it, the plan is stale. However, the recorded
payment still stands and any difference shows up as a residual balance when the
balances re-derive.

Multi-currency is the trigger to revisit. Netting needs a common unit, so
balances in different currencies cannot be netted without a rate; the planner
would become per-currency at best.

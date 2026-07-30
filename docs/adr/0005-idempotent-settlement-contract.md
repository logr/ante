---
number: 5
title: Idempotent Settlement Contract
status: proposed
date: 2026-07-29
---

# Idempotent Settlement Contract

## Context and Problem Statement

Recording a settlement is a payments-shaped action: it must never double-record,
even across retries, app kills, and offline queues. This is the pattern real
payment APIs expect clients to implement, and the one mobile clients most often
get wrong.

The specific failure window is between the server accepting the settlement and
the client receiving confirmation. If the response is lost, the client cannot
know whether to retry or skip - without an idempotency contract, retries risk
double-charging.

## Decision Drivers

- Offline-first: the client must be able to create and persist settlements
  before any network contact.
- Exactly-once semantics: a settlement recorded twice is financially incorrect
  and breaks the ledger.
- Settlements are immutable domain records with no lifecycle state.
- Simplicity: v1 uses a fake backend; the contract must work the same way
  whether the server is real or stubbed.

## Considered Options

- Deterministic hash idempotency key (SHA256 of settlement fields)
- Client-generated UUID persisted before send
- Server-generated key returned on first request

## Decision Outcome

Chosen option: "Deterministic hash idempotency key", because the SHA256 hash of
the settlement's own fields (`groupId + payerId + payeeId + amount` + `before`)
is derived, not stored - eliminating a separate persistence step and the risk of
key drift across retries. It comes out best (see below).

### Consequences

- Good, because the key is derived from immutable settlement fields - no
  separate storage needed, no risk of key mismatch on retry.
- Good, because the same settlement created on two devices produces the same
  key, enabling natural deduplication without coordination.
- Good, because it enforces that identical settlements are treated as the same
  operation - if the fields match, the settlement is the same.
- Bad, because a genuinely different settlement cannot share the same field
  combination (this is actually desired; it means the same payer/payee/
  amount/before pair can only be recorded once).
- Bad, because reversal requires a new settlement void - this is correct
  behavior but means reversals are explicit records, not state transitions.

### Confirmation

The idempotency key derivation will be unit tested to ensure determinism: the
same input fields always produce the same hash, and any field change produces a
different hash. Integration tests will verify that submitting the same
settlement twice results in exactly one record.

## Pros and Cons of the Options

### Deterministic hash idempotency key (SHA256)

The idempotency key is `SHA256(groupId + payerId + payeeId + amount + before)`,
computed from the settlement's own immutable fields. No separate key storage.

- Good, because derived - no separate persistence step before send.
- Good, because deterministic - same settlement on any device produces the same
  key, enabling cross-device deduplication.
- Good, because simple - one less piece of state to manage.
- Bad, because hash collisions are theoretically possible (negligible with
  SHA256).

### Client-generated UUID persisted before send

The client generates a UUID, saves it locally before the first network attempt,
and reuses it on retries. Server deduplicates on the UUID.

- Good, because each settlement attempt is independently addressable.
- Good, because the same field combination can be recorded multiple times with
  different keys (useful if identical payments are legitimate).
- Bad, because the key must exist before the first send - requires a persistence
  step specifically for the key, creating a new failure mode (key saved but
  settlement not yet persisted).
- Bad, because the key doesn't help with cross-device deduplication - two
  devices creating the same settlement would generate different UUIDs.
- Bad, because more state to manage: the key, its lifecycle, and its
  relationship to the settlement record.

### Server-generated key

The server returns an idempotency key on first successful request; the client
stores it for subsequent retries.

- Good, because the server controls uniqueness.
- Bad, because the key doesn't exist until the server responds - exactly the
  failure window this pattern protects against. If the response is lost, the
  client has no key to retry with.
- Bad, because it breaks offline-first operation - settlements created offline
  have no key until they reach the server for the first time.

## More Information

A Settlement can be overpaid. If Jan owes Mary $20.00 and pays her $25.00 then
that is a recorded fact and settle up amounts are recomputed leaving $5.00 owed
from Mary to Jan. Only `amount > 0` is required to post a payment.

Settlement 'state' does not exist. There is no paid or unpaid - there are
expenses and settlements and the resulting balance between two members is
derived from those facts.

Reversal is handled by creating a settlement void which is an appended,
immutable, fact. Any views and derivations are recomputed on sync (or entry if
locally voided). A Settlement with a void releases its key so the Settlement can
be reposted if nothing changes. A release operation means the key for a voided
transaction is not considered when doing the idempotency check.

Since a SHA hash key can be released the idempotency check is two-tiered. The
Settlement id is checked against all records, including voided ones, to ensure a
voided Settlement is not recorded again.

The sync transport layer (retry policy, exponential backoff with jitter, queue
management) is specified in ADR _Sync & conflict resolution_ (planned). This ADR
defines only the deduplication contract.

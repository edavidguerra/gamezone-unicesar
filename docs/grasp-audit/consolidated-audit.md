# GRASP Audit - Consolidated

This file merges the individual audits in `docs/grasp-audit/` (leader, developer 1 and
developer 2) into one list of violations. Each violation keeps the ID assigned by its author.
Duplicates are merged under the lowest ID and the other ID is marked as merged.

## Violation ID ranges

| Member | Role | GRASP range | SOLID range |
|---|---|---|---|
| Ever Guerra | Technical Leader | V-01 to V-09 | V-30 to V-39 |
| Gustavo Rodriguez | Developer 1 | V-10 to V-19 | V-40 to V-49 |
| Juan Araujo | Developer 2 | V-20 to V-29 | V-50 to V-59 |

Every refactor commit that fixes a violation must include its ID in the message, for example
`refactor(V-01): move final total calculation to Sale`, and the same ID must appear in
`docs/grasp-refactor-log.md`.

## Patterns to review

1. Information Expert
2. Creator
3. Controller
4. Low Coupling
5. High Cohesion
6. Polymorphism
7. Pure Fabrication
8. Indirection
9. Protected Variations

## Audit table template

Each member copies this table into their own audit file and fills one row per violation.

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-XX | | | | | | High / Medium / Low |

Severity guide:

- **High**: the violation duplicates business logic or makes a change in one class break another.
- **Medium**: the responsibility is in the wrong class but the code works and is not duplicated.
- **Low**: naming, small coupling or a missing abstraction with no current impact.

A finding can be rejected after review. Keep it in the table with `Rejected` in the
Severity column and explain why in the last column.

## Consolidated violations

To be completed by the leader after the three individual audits are merged.

| ID | Pattern/Principle | Class.method (line) | Owner | Severity | Status |
|---|---|---|---|---|---|

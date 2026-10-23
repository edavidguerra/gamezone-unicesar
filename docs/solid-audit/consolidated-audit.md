# SOLID Audit - Consolidated

This file merges the individual audits in `docs/solid-audit/` (leader, developer 1 and
developer 2) into one list of violations. Each violation keeps the ID assigned by its author.
Duplicates are merged under the lowest ID and the other ID is marked as merged.

The audit is made on `develop` after the GRASP refactor (Requirement 7), so the findings
deferred from GRASP (V-05, V-06 and V-26) are reviewed again here.

## Violation ID ranges

| Member | Role | SOLID range |
|---|---|---|
| Ever Guerra | Technical Leader | V-30 to V-39 |
| Gustavo Rodriguez | Developer 1 | V-40 to V-49 |
| Juan Araujo | Developer 2 | V-50 to V-59 |

Every refactor commit that fixes a violation must include its ID in the message, for example
`refactor: extract SaleRepository interface (V-31)`, and the same ID must appear in
`docs/solid-refactor-log.md`.

## Principles to review

1. Single Responsibility Principle (SRP)
2. Open/Closed Principle (OCP)
3. Liskov Substitution Principle (LSP)
4. Interface Segregation Principle (ISP)
5. Dependency Inversion Principle (DIP)

## Audit table template

Each member copies this table into their own audit file and fills one row per violation.

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-XX | | | | | | High / Medium / Low |

Severity guide:

- **High**: a change in one module forces changes in unrelated classes, or a subclass breaks
  the behavior its parent promises.
- **Medium**: the class depends on more than it needs, or on a concrete class where an
  abstraction would isolate a likely change.
- **Low**: a small design smell with no current impact.

A finding can be rejected after review. Keep it in the table with `Rejected` in the
Severity column and explain why in the last column. Patterns checked with no violation use
`-` as ID.

## Consolidated violations

To be completed by the leader after the three individual audits are merged.

| ID | Principle | Class.method (line) | Owner | Severity | Status |
|---|---|---|---|---|---|

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

Merged on `feature/solid-audit` from `leader-audit.md`, `developer1-audit.md` and
`developer2-audit.md`. All three audits were made on `develop` at commit `fd40f64`, so the
line numbers refer to that commit. Principles checked with no violation are kept in each
individual audit and are not repeated here.

| ID | Principle | Class.method (line) | Owner | Severity | Status |
|---|---|---|---|---|---|
| V-30 | SRP | ConsoleMenu (534 lines, seven module menus, `execute`, `readInt`) | Leader | High | Open |
| V-31 | DIP | SaleService field (24) and constructor (32); Main.main (37) | Leader | Medium | Open |
| V-32 | ISP / DIP | WarrantyService and ReturnService receive the whole `SaleService` | Leader | Medium | Open |
| V-33 | SRP | Sale.generateReceipt (105-119) | Leader | - | Rejected (Sale owns every value on the receipt and does not print) |
| V-40 | OCP | ProductRepository.saveAll (33-43), loadAll (74-82) | Developer 1 | - | Rejected (the file mapping belongs to the repository, as GRASP V-15) |
| V-42 | LSP | Warranty constructor (19-25) | Developer 1 | Low | Open |
| V-43 | DIP | ProductService field (16) and constructor (25); Main.main (35, 42) | Developer 1 | Medium | Open |
| V-44 | ISP | ProductService used by WarrantyService only through `findById` | Developer 1 | Medium | Open |
| V-50 | DIP | PersonService constructor (27) | Developer 2 | Medium | Open |
| V-51 | DIP | AccessoryService field (19) and constructor (28) | Developer 2 | Medium | Open |
| V-52 | DIP | PromotionService field (17) and constructor (20) | Developer 2 | Medium | Open |
| V-53 | DIP | ReturnService field (22) and constructor (38) | Developer 2 | Medium | Open |
| V-54 | DIP | WarrantyService field (19) and constructor (24) | Developer 2 | Medium | Open |
| V-55 | ISP / DIP | WarrantyService fields (20-21) and constructor (24-25); ReturnService field (23) and constructor (38) | Developer 2 | Medium | Open, needs V-32 and V-44 |
| V-56 | SRP | ReturnService monthly balance methods (163-205) | Developer 2 | Medium | Open, needs V-32 |
| V-57 | OCP | PromotionRepository.saveAll (55, 58), loadAll (73) | Developer 2 | - | Rejected (same reason as V-40) |

Totals: 16 findings. 13 open and 3 rejected.

The GRASP findings deferred to this phase are covered here: V-05 (cycle between `SaleService`
and `WarrantyService`) by V-32 and V-55, V-06 (concrete `SaleRepository`) by V-31, and V-26
(`WarrantyService` coupled to whole services) by V-55.

## Related findings

| Problem | IDs | How the work is split |
|---|---|---|
| Every service depends on a concrete repository class | V-31, V-43, V-50, V-51, V-52, V-53, V-54 | Each owner extracts the interface of the repositories of their module and keeps the file code in a `File...Repository` class created in `Main`. The leader does `SaleRepository` first so the others follow the same pattern |
| `WarrantyService` and `ReturnService` receive whole services they barely use | V-32, V-44, V-55 | The leader adds `SaleLookup` (V-32) and Developer 1 adds `ProductLookup` (V-44). Developer 2 changes the two clients to receive the lookups (V-55). `ReturnService` keeps the concrete `ProductService` because it also calls `listProducts` and `restoreStock`. V-32 and V-55 describe the same lines from the provider side and from the client side |
| Repositories test the concrete type to write the file | V-40, V-57 | Both rejected: the repository is the only class that knows the file format, so the mapping must stay there |
| `ConsoleMenu` holds every screen | V-30, V-56 | Developer 2 moves the monthly balance to `MonthlyBalanceService` (V-56) before the leader splits the menu (V-30), so the new `ReturnMenu` already calls the new service |

## Refactor order

The interfaces must exist before the classes that use them, so the refactor on
`feature/solid-refactor` goes in four turns. Each turn starts after the previous one is merged
into `develop`:

1. **Leader:** V-31 (`SaleRepository` interface and `FileSaleRepository`) and V-32
   (`SaleLookup` implemented by `SaleService`).
2. **Developer 1:** V-43 (`ProductRepository` interface), V-44 (`ProductLookup`) and V-42
   (`Warranty.getEndDate()` computed from the duration).
3. **Developer 2:** V-50 to V-54 (repository interfaces), V-55 (`WarrantyService` and
   `ReturnService` receive the lookups, which removes the setter in `Main`) and V-56
   (`MonthlyBalanceService`).
4. **Leader:** V-30 (split of `ConsoleMenu`), then the final non-regression check against the
   baseline in `docs/grasp-refactor-log.md`.

Every refactor commit carries its ID, for example `refactor: extract SaleRepository interface (V-31)`,
and adds one row to `docs/solid-refactor-log.md`.

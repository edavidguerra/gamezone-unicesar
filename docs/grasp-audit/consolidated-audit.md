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

Merged on `feature/grasp-audit` from `leader-audit.md`, `developer1-audit.md` and
`developer2-audit.md`. All three audits were made on `develop` at commit `2f00b33`, so the
line numbers refer to that commit. Patterns checked with no violation are kept in each
individual audit and are not repeated here.

| ID | Pattern/Principle | Class.method (line) | Owner | Severity | Status |
|---|---|---|---|---|---|
| V-01 | Information Expert | SaleService.registerSale (84-109) | Leader | High | Fixed (`239c21c`) |
| V-02 | Polymorphism | SaleService.registerSale (96) | Leader | Medium | Fixed (`0b4c0de`) |
| V-03 | Polymorphism | ConsoleMenu.registerSale (209) | Leader | Medium | Fixed (`989f294`) |
| V-04 | Controller / High Cohesion | ConsoleMenu.listAccessoriesByTypeFlow (294-308) | Leader | Medium | Fixed (`50d6a86`) |
| V-05 | Low Coupling | Main.main (49-50), SaleService.setWarrantyService (51) | Leader | Medium | Deferred to Requirement 8 (V-32) |
| V-06 | Protected Variations / Indirection | SaleService fields (25), Main.main (37) | Leader | Low | Deferred to Requirement 8 (V-31) |
| V-10 | Polymorphism | CategoryDiscount.belongsToTargetCategory (37-48) | Developer 1 | High | Fixed (`71c0bca, 0170d28`) |
| V-11 | Polymorphism | Product (new `isWarrantyEligible`) | Developer 1 | High | Fixed (`2faab25`) |
| V-12 | Information Expert | ProductService.restoreStock (116-122) | Developer 1 | Medium | Fixed (`9c004d2`) |
| V-13 | Polymorphism | Accessory (new `getAccessoryType`) | Developer 1 | Medium | Fixed (`c121391`) |
| V-14 | Polymorphism | Warranty (new `getTypeCode`) | Developer 1 | Medium | Fixed (`752bca4`) |
| V-15 | Protected Variations | ProductRepository.saveAll (33-43), loadAll (74-82) | Developer 1 | - | Rejected (mapping to the file belongs to the repository) |
| V-20 | Low Coupling / Indirection | ReturnRepository constructor (37), imports (7-9) | Developer 2 | High | Fixed (`fe94892`) |
| V-21 | Polymorphism | ReturnService.registerReturn (101) | Developer 2 | Medium | Fixed (`555b658`) |
| V-22 | Polymorphism | ReturnService.restoreItemStock (256) | Developer 2 | Medium | Fixed (`7b78363`) |
| V-23 | Information Expert / Controller | AccessoryService (new `listAccessoriesByType`) | Developer 2 | Medium | Fixed (`c44fa4d`) |
| V-24 | Polymorphism | WarrantyRepository.saveAll (63) | Developer 2 | Medium | Fixed (`a24de8f`) |
| V-25 | Information Expert | AccessoryService.restoreStock (166) | Developer 2 | Low | Fixed (`df719f5`) |
| V-26 | Low Coupling | WarrantyService fields and constructor (20-25) | Developer 2 | Medium | Deferred to Requirement 8 (V-55) |

Totals: 19 findings. 15 fixed in the GRASP refactor, 3 deferred to Requirement 8 and 1
rejected. The commits are listed in `docs/grasp-refactor-log.md`, and the final
non-regression check matched the baseline.

## Related findings

Some members found the same problem from different classes. They were not merged into one ID
because each part changes a different class and is committed by a different member.

| Problem | IDs | How the work is split |
|---|---|---|
| "Only consoles have a warranty" is checked with `instanceof Console` in three classes | V-11, V-02, V-03, V-21 | Developer 1 adds `Product.isWarrantyEligible()` (V-11). Each caller is then fixed by its owner: SaleService (V-02) and ConsoleMenu (V-03) by the leader, ReturnService (V-21) by Developer 2 |
| The menu filters accessories by type with `instanceof` | V-13, V-23, V-04 | Developer 1 adds `Accessory.getAccessoryType()` (V-13), Developer 2 adds `AccessoryService.listAccessoriesByType` (V-23) and the leader makes the menu call it (V-04). V-23 and V-04 describe the same lines of `ConsoleMenu`, from the service side and from the controller side |
| Stock is increased outside the class that owns it | V-12, V-25 | Developer 1 adds `Product.increaseStock` and uses it in ProductService (V-12); Developer 2 uses it in AccessoryService (V-25) |
| Warranty type is detected with `instanceof` | V-14, V-24 | Developer 1 adds `Warranty.getTypeCode()` (V-14); Developer 2 uses it in WarrantyRepository (V-24) |
| Product category is detected with `instanceof` | V-10, V-22 | Developer 1 adds `Product.getCategory()` and uses it in CategoryDiscount (V-10); Developer 2 uses it in ReturnService (V-22) |
| Services depend on the whole `SaleService` | V-05, V-26 | Both need an interface instead of a concrete class, which is a SOLID change. They are fixed in Requirement 8 with `SaleLookup` and `ProductLookup` |

## Refactor order

The model methods must exist before the classes that call them, so the refactor on
`feature/grasp-refactor` goes in three turns, and each turn starts after the previous one is
merged into `develop`:

1. **Developer 1:** V-10, V-11, V-12, V-13, V-14 (new methods in `Product`, `Accessory` and
   `Warranty`, and their first callers).
2. **Developer 2:** V-20, V-21, V-22, V-23, V-24, V-25.
3. **Leader:** V-01, V-02, V-03, V-04, then the non-regression check against the baseline in
   `docs/grasp-refactor-log.md`.

Every refactor commit carries its ID, for example `refactor(V-10): add getCategory to Product`,
and adds one row to the Refactors table of `docs/grasp-refactor-log.md`.

# SOLID Audit - Developer 1

Auditor: Gustavo Rodriguez (Developer 1). ID range: V-40 to V-49.
Audited on `develop` at commit `fd40f64` (after the GRASP refactor).

Scope: product hierarchy (`Product`, `VideoGame`, `Console`), accessory hierarchy (`Accessory`,
`Controller`, `Cable`, `Memory`), promotion hierarchy (`Promotion`, `PercentageDiscount`,
`CategoryDiscount`, `BulkPurchaseDiscount`), warranty hierarchy (`Warranty`, `BasicWarranty`,
`ExtendedWarranty`), `ProductService`, `ProductRepository`, the `exceptions` package and the
`validation` package. Line numbers refer to that commit.

Rows with `-` as ID are principles that were checked and have no violation in this scope.

## Open/Closed

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-40 | OCP | ProductRepository.saveAll (33-43), loadAll (74-82) | `if (product instanceof VideoGame ...) else if (product instanceof Console ...)` when writing and `type.equals("VIDEOGAME")` / `type.equals("CONSOLE")` when reading | A new product type forces editing the repository in two methods, so the class is not closed to that change | Rejected: same reason as GRASP V-15. The repository is the only class that knows the file format, so the mapping from type to line must live there; moving it into `Product` would make the model depend on persistence. The change stays inside one class and does not reach the services or the UI | Rejected |
| - | OCP | Promotion.calculateDiscount (48) and its three subclasses; PromotionService.findBestPromotionFor (74-78) | `findBestPromotionFor` calls `promotion.calculateDiscount(sale)` on every active promotion without asking for its type | No violation: a new promotion is a new subclass with its own `calculateDiscount`. `PromotionService` and `SaleService.registerSale` (84-87) do not change. Only `PromotionRepository` must learn the new line format, which is the same accepted case as V-40 | - | - |
| - | OCP | CategoryDiscount.belongsToTargetCategory (37-39) | `return product.getCategory().equals(targetCategory);` | No violation after GRASP V-10: a new product type only implements `getCategory()` and the discount works without editing this class | - | - |
| - | OCP | Product.getCategory, isWarrantyEligible; Accessory.getAccessoryType; Warranty.getTypeCode | `SaleService` and `ConsoleMenu` (209) ask `product.isWarrantyEligible()` instead of testing the class | No violation after GRASP V-11 to V-14: the behaviors that change with the type are polymorphic methods, so a new subclass is added without editing the classes that call them | - | - |
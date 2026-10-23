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

## Liskov Substitution

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-42 | LSP | Warranty constructor (19-25) | `this.endDate = startDate.plusMonths(getDurationInMonths());` (24) | The base constructor calls an abstract method before the subclass constructor has run. Today both subclasses return a constant (3 and 12), so it works, but a subclass whose duration depends on its own fields would return 0 there and get a wrong `endDate`, so the base class does not keep its promise for every subclass. Also, `setEndDate` (40) has no callers and allows an end date that does not match the duration | Delete the `endDate` field and `setEndDate`; `getEndDate()` returns `startDate.plusMonths(getDurationInMonths())` and `isActive` (42-44) uses `getEndDate()`. `WarrantyService.listWarrantiesExpiringSoon` (89-90) keeps working because it already uses the getter | Low |
| - | LSP | Product: VideoGame, Console, Accessory | Every subclass implements `getDescription` and `getCategory` with a non-null value; `Console` overrides `isWarrantyEligible` to return `true`. None overrides `hasStock`, `reduceStock` or `increaseStock`, and none throws an exception | No violation: any product works where `Product` is expected, as in `Sale.calculateTotal`, `CategoryDiscount` and `ProductService`. The non-regression sale with a video game and a console gave the expected totals | - | - |
| - | LSP | Accessory: Controller, Cable, Memory | Each one implements `getAccessoryType` with its own token and `getDescription`; none changes `isCompatibleWith` or the stock methods | No violation: `AccessoryService` and `ReturnService` work with any accessory without knowing the subclass | - | - |
| - | LSP | Promotion: PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount | `calculateDiscount` returns a value between 0 and the sale subtotal (for percentages from 0 to 100) and never throws; `BulkPurchaseDiscount` returns 0 when the minimum quantity is not reached | No violation: `findBestPromotionFor` compares the result of any subclass with the same rule | - | - |
| - | LSP | Warranty: BasicWarranty, ExtendedWarranty | `getDurationInMonths` returns a positive number (3 and 12), `getAdditionalCost` never returns a negative value (`ExtendedWarranty` returns 0 when there is no product) and `getTypeCode` returns `BASIC` or `EXTENDED` | No violation in the subclasses themselves; the only risk is in the base constructor (V-42) | - | - |
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

## Single Responsibility, Interface Segregation and Dependency Inversion

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-43 | DIP | ProductService field (16) and constructor (25); Main.main (35, 42) | `private ProductRepository productRepository;` and `new ProductRepository()`: the service depends on the concrete class that writes `data/products.csv` | A high-level module (the catalog rules) depends on a low-level detail (a text file). Changing the storage or testing the service with fake data means editing `ProductService` | Turn `ProductRepository` into an interface with `saveAll` and `loadAll`, move the current code to `FileProductRepository implements ProductRepository`, and create it in `Main` (only line 35 changes) | Medium |
| V-44 | ISP | WarrantyService field (21) and constructor (24-25); Main.main (49) | `WarrantyService` only calls `productService.findById` (35), but it receives the whole `ProductService` with seven public methods (`registerVideoGame`, `registerConsole`, `listProducts`, `findById`, `hasStock`, `reduceStock`, `restoreStock`) | The client depends on six methods it never uses, including the ones that change the stock, so a change in any of them reaches a class that only needs to look products up | Add `ProductLookup` in `service` with `Product findById(String id)`; `ProductService implements ProductLookup`. Developer 2 changes the type in `WarrantyService` (V-55). `ReturnService` uses `listProducts` (46) and `restoreStock` (259), not `findById`, so it cannot receive only a `ProductLookup` | Medium |
| - | SRP | ProductService (whole class) | Registers, lists and finds video games and consoles, and updates their stock | No violation: one reason to change, the rules of the product catalog. Validation goes to `ProductValidator` and file access to the repository | - | - |
| - | SRP | ProductRepository (whole class) | Only converts products to lines of `data/products.csv` and back | No violation: one reason to change, the file format | - | - |
| - | SRP | ProductValidator, PersonValidator, SaleValidator | Static methods with private constructors (13, 15, 19); each validator only checks the data of one entity and throws the matching exception | No violation: each validator changes only when the rules of its entity change | - | - |
| - | ISP / DIP | ProductValidator, PersonValidator, SaleValidator | The services call the static methods directly | No violation: the validators have no state and no alternative implementation, so an interface would add indirection without isolating any likely change | - | - |
| - | SRP / LSP | `exceptions` package | `GameZoneException` stores the message and the code; each subclass only fixes its code (`RESOURCE_NOT_FOUND`, `BUSINESS_RULE`, `INVALID_DATA`, `PERSISTENCE`). `ConsoleMenu.execute` catches any of them as `GameZoneException` | No violation: each class represents one error category, and no subclass changes how `getMessage` or `getErrorCode` behave | - | - |
# GRASP Audit - Developer 1

Auditor: Gustavo Rodriguez (Developer 1). ID range: V-10 to V-19.
Audited on `develop` at commit `2f00b33`.

Scope: product hierarchy (`Product`, `VideoGame`, `Console`), accessory hierarchy (`Accessory`,
`Controller`, `Cable`, `Memory`), promotion hierarchy (`Promotion` and its three discounts),
warranty hierarchy (`Warranty`, `BasicWarranty`, `ExtendedWarranty`), `Return`,
`ProductService`, `ProductRepository`, the `exceptions` package and the `validation` package.
Line numbers refer to that commit.

Rows with `-` as ID are patterns that were checked and have no violation in this scope.

## Information Expert and Creator

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-12 | Information Expert | ProductService.restoreStock (116-122) | `product.setStock(product.getStock() + quantity)` (119) | `Product` owns `stock` and already knows how to check it (`hasStock`, 50) and lower it (`reduceStock`, 60), but the opposite operation is done outside, in the service. The stock arithmetic is split between two classes, and `AccessoryService.restoreStock` (166) repeats the same line (V-25, Developer 2) | Add `public void increaseStock(int quantity)` to `Product`; `restoreStock` only calls `product.increaseStock(quantity)` | Medium |
| - | Information Expert | Return.calculateRefundAmount, getDiscountRate, calculateItemRefund | The refund is computed inside `Return` from `originalSale` and `returnedProducts` | No violation found: `Return` holds the sale reference and the returned items, so it is the expert on the refund. `ReturnService` only adds the warranty part through `addWarrantyRefund` | - | - |
| - | Information Expert | Promotion.calculateDiscount (48) and its three subclasses | Each discount computes its amount from the `Sale` it receives | No violation found: the percentage, the target category and the minimum quantity live in each promotion, so each one is the expert on its own discount | - | - |
| - | Creator | ProductService.registerVideoGame (39), registerConsole (54); ProductRepository.loadAll (75, 79) | `new VideoGame(...)`, `new Console(...)` | No violation found: the service creates the product after validating the data received from the menu, so it has the initializing data and keeps the list that contains the product. The repository creates products while reading `products.csv`, the only place where the saved data exists | - | - |
| - | Creator | PromotionService.registerPercentageDiscount (27), registerCategoryDiscount (39), registerBulkPurchaseDiscount (48); PromotionRepository (74-78) | `new PercentageDiscount(...)`, `new CategoryDiscount(...)`, `new BulkPurchaseDiscount(...)` | No violation found: the service has the initializing data and keeps the list of promotions; the repository rebuilds each promotion from its saved line | - | - |
| - | Creator | WarrantyService.assignBasicWarranty (48), assignExtendedWarranty (55), resolveRecords (40-41) | `new BasicWarranty(...)`, `new ExtendedWarranty(...)` | No violation found: the warranty needs a product and a sale, and `WarrantyService` is the class that receives both and keeps the list of warranties | - | - |
| - | Creator | ReturnService.registerReturn (92); ReturnRepository.loadAll (100) | `new Return(...)` | No violation found: the service creates the return only after it has resolved the sale and every returned product, so it has all the initializing data. The repository rebuilds returns from the saved file | - | - |
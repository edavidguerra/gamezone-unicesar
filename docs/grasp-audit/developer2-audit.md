# GRASP Audit - Developer 2

Auditor: Juan Araujo (Developer 2). ID range: V-20 to V-29.
Audited on `develop` at commit `2f00b33`.

Scope: `Person`, `Client`, `Seller`, `PersonRepository`, `PersonService`, `AccessoryRepository`,
`AccessoryService`, `PromotionRepository`, `PromotionService`, `ReturnRepository`,
`ReturnService`, `WarrantyRepository`, `WarrantyService`.
Line numbers refer to that commit.

Rows with `-` as ID are patterns that were checked and have no violation in this scope.

## Low Coupling and Indirection

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-20 | Low Coupling / Indirection | ReturnRepository (imports l. 7-9, constructor l. 37) | `import com.gamezone.service.AccessoryService`, `ProductService` and `SaleService` (l. 7-9); the constructor `ReturnRepository(SaleService, ProductService, ...)` receives the services (l. 37) | The persistence layer depends on the service layer, which breaks the UI → Service → Persistence → Model rule and creates a circular dependency (ReturnService → ReturnRepository → services) | Follow the SaleRepository approach: `loadAll(List<Sale> sales, List<Product> catalog)` resolves sales and products from the lists it receives; ReturnService passes `saleService.listSales()` and the catalog (products + accessories); the constructor becomes empty and Main uses `new ReturnRepository()` | High |
| V-26 | Low Coupling | WarrantyService fields and constructor (l. 20-21, 24-25) | Fields `SaleService saleService` and `ProductService productService` (l. 20-21) and constructor parameters of the same concrete types (l. 24-25); only `productService.findById` (l. 35) and `saleService.findById` (l. 36) are called | WarrantyService is coupled to whole concrete service classes when it only needs a lookup by id, and it keeps the dependency on SaleService | Deferred: it is fixed in the SOLID phase by depending on `SaleLookup` and `ProductLookup` (V-55) | Medium |
## Polymorphism

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-21 | Polymorphism | ReturnService (l. 101) | `if (product instanceof Console)` to decide whether the warranty must be cancelled | The service asks for the concrete type; a new product type with warranty forces editing this method | Use `product.isWarrantyEligible()` (added to Product by Developer 1, V-11) | Medium |
| V-22 | Polymorphism | ReturnService.restoreItemStock (l. 256) | `if (item instanceof Accessory)` to decide which service receives the restored stock | The service decides by concrete type where the stock goes | Use `"ACCESSORY".equals(item.getCategory())` (Developer 1, V-10) | Medium |
| V-24 | Polymorphism | WarrantyRepository (l. 63) | `String type = (warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED";` | The repository asks for the concrete type to build the file code, and a new warranty type would be saved as `EXTENDED` by default | Use `warranty.getTypeCode()` (Developer 1, V-14) | Medium |

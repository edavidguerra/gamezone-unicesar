# SOLID Audit - Developer 2

Auditor: Juan Araujo (Developer 2). ID range: V-50 to V-59.
Audited on `develop` at commit `fd40f64` (after the GRASP refactor).

Scope: `Person`, `Client`, `Seller`, `PersonRepository`, `PersonService`, `AccessoryRepository`,
`AccessoryService`, `PromotionRepository`, `PromotionService`, `ReturnRepository`,
`ReturnService`, `WarrantyRepository`, `WarrantyService`. Line numbers refer to that commit.

Rows with `-` as ID are principles that were checked and have no violation in this scope.

## Dependency Inversion

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-50 | DIP | PersonService constructor (l. 27) | `public PersonService(PersonRepository personRepository)`: the service receives the concrete class that reads and writes the people file | A high-level module (people rules) depends on a low-level detail (a text file). Changing the storage or testing the service with fake data means editing `PersonService` | Turn `PersonRepository` into an interface and move the current code to `FilePersonRepository implements PersonRepository`; `Main` creates `FilePersonRepository` | Medium |
| V-51 | DIP | AccessoryService field (l. 19) and constructor (l. 28) | `private final AccessoryRepository repository;` and `AccessoryService(AccessoryRepository repository)` | The accessory rules depend on the concrete class that writes the accessories file | Same fix: interface `AccessoryRepository` and `FileAccessoryRepository` | Medium |
| V-52 | DIP | PromotionService field (l. 17) and constructor (l. 20) | `private final PromotionRepository repository;` and `PromotionService(PromotionRepository repository)` | The promotion rules depend on the concrete class that writes the promotions file | Same fix: interface `PromotionRepository` and `FilePromotionRepository` | Medium |
| V-53 | DIP | ReturnService field (l. 22) and constructor (l. 38) | `private final ReturnRepository repository;` and `ReturnService(ReturnRepository repository, ...)` | The return rules depend on the concrete class that writes the returns file | Same fix: interface `ReturnRepository` and `FileReturnRepository` | Medium |
| V-54 | DIP | WarrantyService field (l. 19) and constructor (l. 24) | `private final WarrantyRepository repository;` and `WarrantyService(WarrantyRepository repository, ...)` | The warranty rules depend on the concrete class that writes the warranties file | Same fix: interface `WarrantyRepository` and `FileWarrantyRepository`; `WarrantyRecord` stays public inside the interface or in its own file | Medium |
## Interface Segregation

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-55 | ISP / DIP | WarrantyService fields (l. 20-21) and constructor (l. 24-25); ReturnService field (l. 23) and constructor (l. 38) | `WarrantyService` only calls `productService.findById` (l. 35) and `saleService.findById` (l. 36); `ReturnService` only calls `saleService.findById` (l. 65) and `saleService.listSales` (l. 48, 179). Both receive the whole concrete `SaleService` and `ProductService` | Both clients depend on methods they never use (`registerSale`, `listSalesByClient`, `setWarrantyService`, `registerVideoGame`...). It also keeps the cycle SaleService and WarrantyService (GRASP V-05, same finding as V-32) | `WarrantyService` receives `SaleLookup` and `ProductLookup`; `ReturnService` receives `SaleLookup`. `ReturnService` keeps the concrete `ProductService` because it also calls `listProducts` (l. 46) and `restoreStock` | Medium |
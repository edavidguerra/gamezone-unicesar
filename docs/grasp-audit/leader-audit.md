# GRASP Audit - Technical Leader

Auditor: Ever Guerra (Technical Leader). ID range: V-01 to V-09.
Audited on `develop` at commit `2f00b33`.

Scope: `ui/ConsoleMenu`, `model/Sale`, `service/SaleService`, `Main` and
`persistence/SaleRepository`. Line numbers refer to that commit.

## Violations

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-01 | Information Expert | SaleService.registerSale (84-109) | The service computes `finalTotal = subtotal - discount + warrantyCost` and pushes it into `Sale` with `setFinalTotal`. `Sale` already holds the products, `discountAmount` and `extendedWarrantyCost` | `Sale` is the expert on its own totals but only knows how to compute the subtotal. The formula lives outside the class that owns the data, so `ConsoleMenu.printSales` (231) ends up printing `calculateTotal()` (the subtotal) instead of the amount paid (baseline cases 7 and 8) | Add `Sale.calculateFinalTotal()` returning `calculateTotal() - discountAmount + extendedWarrantyCost`; the service only calls `sale.setFinalTotal(sale.calculateFinalTotal())` | High |
| V-02 | Polymorphism | SaleService.registerSale (96) | `if (item instanceof Console)` decides which items receive the basic and extended warranty | The service asks for the concrete type instead of asking the product. Adding another product with warranty means editing the sale logic | Use `item.isWarrantyEligible()` (method added by Developer 1 in V-11) | Medium |
| V-03 | Polymorphism | ConsoleMenu.registerSale (209) | `if (product instanceof Console)` decides whether to ask for the extended warranty | The UI asks for the concrete type to decide a business rule. A new product type with warranty would force a change in the menu | Ask the product itself: `product != null && product.isWarrantyEligible()` (method added by Developer 1 in V-11) | Medium |
| V-04 | Controller / High Cohesion | ConsoleMenu.listAccessoriesByTypeFlow (294-308) | The menu loads every accessory and filters it with a `switch` on the type name plus `instanceof Controller/Cable/Memory` | Filtering is application logic, not input/output. The menu stops being a thin controller that delegates to the services, and its cohesion drops | Call `accessoryService.listAccessoriesByType(type)` (created by Developer 2 in V-23) and only print the result | Medium |
| V-05 | Low Coupling | Main.main (49-50), SaleService.setWarrantyService (51) | `WarrantyService` receives `SaleService` in its constructor and `SaleService` receives `WarrantyService` through a setter | The two services depend on each other. `SaleService` cannot work until `Main` calls the setter, and a change in either class can break the other | Deferred to Requirement 8: `WarrantyService` and `ReturnService` will depend on a `SaleLookup` interface (V-32) instead of the whole `SaleService`, which breaks the cycle | Medium (deferred) |
| V-06 | Protected Variations / Indirection | SaleService fields (25), Main.main (37) | `SaleService` stores and calls the concrete class `SaleRepository`, which reads and writes `data/sales.txt` | Any change in how sales are stored (another file format, a database) reaches the service. There is no stable interface between the business rules and the storage | Deferred to Requirement 8: `SaleRepository` interface with a file-based implementation (V-31). It is a dependency inversion change, so it is fixed with SOLID | Low (deferred) |

## Patterns checked in ConsoleMenu with no violation

- **Controller**: every other menu option reads the input, calls one service method and prints
  the result. Since Requirement 6 the `execute` method also centralizes error handling, so the
  menu does not contain business rules for exceptions.

## Patterns checked in Sale and SaleService with no violation

- **Creator**: `SaleService.registerSale` (83) creates the `Sale` after it has resolved the
  client, the seller and every product. It is the class that has the initializing data, so
  the pattern is applied correctly. `SaleRepository.loadAll` creates `Sale` objects when it
  reads `sales.txt`, which is also correct because it owns the saved data.

## Patterns checked in Main and SaleRepository with no violation

- **Indirection**: `Main` is the only class that creates repositories and services and wires
  them together, so `ConsoleMenu` never creates its own dependencies.
- **Pure Fabrication**: `SaleRepository` is an invented class that keeps file access out of
  `Sale`. It converts `IOException` into `PersistenceException`, so the services do not depend
  on `java.io` errors.

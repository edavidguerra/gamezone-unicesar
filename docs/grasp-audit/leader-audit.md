# GRASP Audit - Technical Leader

Auditor: Ever Guerra (Technical Leader). ID range: V-01 to V-09.
Audited on `develop` at commit `2f00b33`.

Scope: `ui/ConsoleMenu`, `model/Sale`, `service/SaleService`, `Main` and
`persistence/SaleRepository`. Line numbers refer to that commit.

## Violations

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-03 | Polymorphism | ConsoleMenu.registerSale (209) | `if (product instanceof Console)` decides whether to ask for the extended warranty | The UI asks for the concrete type to decide a business rule. A new product type with warranty would force a change in the menu | Ask the product itself: `product != null && product.isWarrantyEligible()` (method added by Developer 1 in V-11) | Medium |
| V-04 | Controller / High Cohesion | ConsoleMenu.listAccessoriesByTypeFlow (294-308) | The menu loads every accessory and filters it with a `switch` on the type name plus `instanceof Controller/Cable/Memory` | Filtering is application logic, not input/output. The menu stops being a thin controller that delegates to the services, and its cohesion drops | Call `accessoryService.listAccessoriesByType(type)` (created by Developer 2 in V-23) and only print the result | Medium |

## Patterns checked in ConsoleMenu with no violation

- **Controller**: every other menu option reads the input, calls one service method and prints
  the result. Since Requirement 6 the `execute` method also centralizes error handling, so the
  menu does not contain business rules for exceptions.

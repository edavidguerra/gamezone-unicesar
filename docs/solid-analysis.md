# SOLID Analysis

This document explains how each SOLID principle applies to GameZone, which violations were
found in the audit (`docs/solid-audit/consolidated-audit.md`) and how they were fixed
(`docs/solid-refactor-log.md`). Each section is written by the member who owns the principle.

| Principle | Owner |
|---|---|
| Single Responsibility (SRP) | Technical Leader (Ever Guerra) |
| Open/Closed (OCP) | Developer 1 (Gustavo Rodriguez) |
| Liskov Substitution (LSP) | Developer 1 (Gustavo Rodriguez) |
| Interface Segregation (ISP) | Technical Leader (Ever Guerra) |
| Dependency Inversion (DIP) | Developer 2 (Juan Araujo) |

Each section answers three questions:

1. What the principle says.
2. Where GameZone already follows it (class and method).
3. Which violations were found (V-XX) and how they were fixed.

## Single Responsibility Principle (SRP)

Owner: Technical Leader.

**What it says.** A class should have only one reason to change, that is, it should answer to
one actor or one kind of change.

**Where GameZone follows it.**

- The four layers already separate the main reasons to change: `model` (business data and
  rules), `service` (use cases), `persistence` (file format) and `ui` (console screens).
- Each repository changes only when its file format changes, and each validator only when the
  validation rules of its entity change.
- `SaleService.registerSale` coordinates several steps, but each one is delegated to the class
  that owns it (validators, `PromotionService`, `WarrantyService`, `ProductService`, the
  repository), so the service has one responsibility: the "register a sale" use case.

**Violations.**

- **V-30** `ConsoleMenu` has 534 lines and changes for any of the seven modules, for the way
  input is read and for the way errors are shown. The fix splits it into `ConsoleInput`
  (Scanner, number parsing and the `execute` error handler) and one menu class per module
  (`ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`,
  `WarrantyMenu`, `ReturnMenu`). `ConsoleMenu` keeps only the main menu.
- **V-33** `Sale.generateReceipt` builds text in the model. It was rejected: `Sale` owns every
  value on the receipt, the method returns a `String` and does not depend on the console, so
  moving it would only add a class that reads six getters.

## Open/Closed Principle (OCP)

Owner: Developer 1.

**What it says.** A class should be open for extension and closed for modification: new
behavior is added with new code, not by editing code that already works.

**Where GameZone follows it.**

- `Promotion.calculateDiscount` is abstract. `PromotionService.findBestPromotionFor` and
  `SaleService.registerSale` work with any promotion, so a new kind of discount is a new
  subclass and those services do not change.
- After the GRASP refactor the behaviors that change with the product type are polymorphic:
  `getCategory()` (used by `CategoryDiscount`), `isWarrantyEligible()` (used by `SaleService`,
  `ReturnService` and `ConsoleMenu`), `getAccessoryType()` and `Warranty.getTypeCode()`. A new
  subclass implements them and no caller is edited.

**Violations.**

- **V-40** `ProductRepository.saveAll` and `loadAll` choose the line format with `instanceof`
  and `type.equals(...)`. It was rejected: the repository is the only class that knows the
  file format, so a new product type must be taught to it in one place, and moving that
  mapping into `Product` would make the model depend on persistence (same reason as GRASP
  V-15).

## Liskov Substitution Principle (LSP)

Owner: Developer 1.

**What it says.** Objects of a subclass must be usable wherever the base class is expected,
without breaking what the base class promises.

**Where GameZone follows it.**

- Every `Product` subclass (`VideoGame`, `Console` and the three accessories) returns a valid
  description and category, and none throws new exceptions or changes the stock methods, so
  `Sale`, `CategoryDiscount` and the services treat them all as `Product`.
- Every `Promotion` returns a discount between 0 and the subtotal, so
  `findBestPromotionFor` can compare any of them with the same rule.
- `BasicWarranty` and `ExtendedWarranty` return a positive duration and a non-negative cost.
- The exception subclasses only fix their message and code, so `ConsoleMenu.execute` can catch
  any of them as `GameZoneException`.

**Violations.**

- **V-42** The `Warranty` constructor computed `endDate` by calling the abstract method
  `getDurationInMonths()`, which runs before the subclass constructor. It works today because
  both durations are constants, but a subclass that computed its duration from its own fields
  would get a wrong end date. The fix removes the `endDate` field and its unused setter:
  `getEndDate()` computes `startDate.plusMonths(getDurationInMonths())` when it is called, and
  `isActive` uses it.

## Interface Segregation Principle (ISP)

Owner: Technical Leader.

**What it says.** A client should not be forced to depend on methods it does not use. Large
interfaces are split into small ones that fit each client.

**Where GameZone follows it.** Before Requirement 8 the project has no interfaces of its own,
so clients depend on whole concrete classes. Most of them use all or almost all of the class
they receive (for example `SaleService` and `SaleRepository`).

**Violations.**

- **V-32** `WarrantyService` only needs `findById` and `ReturnService` only needs `findById`
  and `listSales`, but both receive the whole `SaleService`, including `registerSale`,
  `listSalesByClient`, `listSalesBySeller` and `setWarrantyService`. The fix adds the
  `SaleLookup` interface with only those two methods. `SaleService` implements it and the two
  services receive a `SaleLookup`. This also breaks the dependency cycle between `SaleService`
  and `WarrantyService` (GRASP V-05).
- The same idea is applied by Developer 1 (`ProductLookup`) and Developer 2 (the lookups used
  by `WarrantyService` and `ReturnService`), so each service exposes a small read-only view to
  the classes that only need to find data.

## Dependency Inversion Principle (DIP)

Owner: Developer 2.

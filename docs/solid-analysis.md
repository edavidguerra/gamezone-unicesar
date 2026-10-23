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

## Liskov Substitution Principle (LSP)

Owner: Developer 1.

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

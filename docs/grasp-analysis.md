# GRASP Analysis

This document explains how each GRASP pattern applies to GameZone, which violations were
found in the audit (`docs/grasp-audit/consolidated-audit.md`) and how they were fixed
(`docs/grasp-refactor-log.md`). Each section is written by the member who owns the pattern.

| Pattern | Owner |
|---|---|
| Information Expert | Developer 1 (Gustavo Rodriguez) |
| Creator | Developer 1 (Gustavo Rodriguez) |
| Controller | Technical Leader (Ever Guerra) |
| Low Coupling | Technical Leader (Ever Guerra) |
| High Cohesion | Developer 2 (Juan Araujo) |
| Polymorphism | Developer 1 (Gustavo Rodriguez) |
| Pure Fabrication | Developer 2 (Juan Araujo) |
| Indirection | Technical Leader (Ever Guerra) |
| Protected Variations | Developer 2 (Juan Araujo) |

Each section answers three questions:

1. What the pattern says.
2. Where GameZone already applies it (class and method).
3. Which violations were found (V-XX) and how they were fixed.

## Information Expert

Owner: Developer 1.

**What it says.** Assign a responsibility to the class that has the information needed to
fulfill it.

**Where GameZone applies it.**

- `Product.hasStock` and `Product.reduceStock` work on the `stock` the product owns.
- `Return.calculateRefundAmount` computes the refund from the original sale and the returned
  items it holds, using `getDiscountRate` and `calculateItemRefund`.
- Each `Promotion` subclass computes its own discount in `calculateDiscount(Sale)`, because the
  percentage, the target category or the minimum quantity are its own attributes.

**Violations.**

- **V-12** `ProductService.restoreStock` did the stock arithmetic itself with
  `product.setStock(product.getStock() + quantity)`, although `Product` owns `stock` and already
  knew how to lower it. Fix: `Product.increaseStock(int quantity)`, and the service only calls
  `product.increaseStock(quantity)`. Developer 2 applies the same method in
  `AccessoryService.restoreStock` (V-25).

## Creator

Owner: Developer 1.

**What it says.** Class B should create objects of class A when B contains or aggregates A, or
when B has the data needed to initialize A.

**Where GameZone applies it.**

- `ProductService.registerVideoGame` and `registerConsole` create the products after
  validating the data, and they keep the list that contains them.
- `PromotionService` creates the three kinds of discount, and `WarrantyService` creates
  `BasicWarranty` and `ExtendedWarranty` because it receives the product and the sale they need.
- `ReturnService.registerReturn` creates the `Return` only after it has resolved the sale and
  every returned product.
- Each repository creates objects only while reading its file, because it is the only class
  that has the saved data.

**Violations.** None found. Every object in this scope is created by the class that has its
initializing data and keeps it in a list.

## Controller

Owner: Technical Leader.

**What it says.** The first object after the user interface that receives a system event
should be a controller that delegates the work and does not do it itself.

**Where GameZone applies it.** `ConsoleMenu` is a facade controller for the whole console. Each
option reads the input, calls one service method and prints the result, for example
`registerVideoGame` calls `productService.registerVideoGame(...)`. Since Requirement 6 every
option runs inside `ConsoleMenu.execute(Runnable)`, which catches the exception categories in
one place, so error handling is not repeated in every flow.

**Violations.**

- **V-04** `ConsoleMenu.listAccessoriesByTypeFlow` filtered the accessories itself with a
  `switch` and `instanceof`. That is application logic, so the menu was doing work that belongs
  to `AccessoryService`. Fix: the menu calls `accessoryService.listAccessoriesByType(type)`
  (added by Developer 2 in V-23) and only prints the result.
- **V-03** `ConsoleMenu.registerSale` used `instanceof Console` to decide whether to offer the
  extended warranty. The question now goes to the product (`isWarrantyEligible()`), so the
  controller no longer knows the warranty rule.

`ConsoleMenu` is still a large class (one controller for seven modules). Splitting it is a
Single Responsibility change and is handled in Requirement 8 (V-30).

## Low Coupling

Owner: Technical Leader.

**What it says.** Assign responsibilities so that each class depends on as few other classes as
possible, so a change in one class affects few others.

**Where GameZone applies it.** The layers only depend downwards: `ui` uses `service`, `service`
uses `persistence` and `model`, and `model` does not know about the other layers. `Sale` keeps
references to `Client`, `Seller` and `Product`, but it never calls a repository or a service.

**Violations.**

- **V-05** `SaleService` and `WarrantyService` depend on each other. `Main` builds
  `SaleService` first and then injects `WarrantyService` with `setWarrantyService`, so
  `SaleService` is in an incomplete state until that call. The cycle cannot be removed with a
  GRASP move without changing what each service receives, so it is deferred to Requirement 8:
  `WarrantyService` and `ReturnService` will depend on a small `SaleLookup` interface (V-32).
- **V-01** also lowers coupling: once `Sale` computes its own final total, `SaleService` stops
  reading and writing three of its fields to do the arithmetic.

## High Cohesion

Owner: Developer 2.

**What it says.** Keep the responsibilities of a class focused and closely related, so each
class has one clear purpose and is easy to understand, reuse and maintain.

**Where GameZone applies it.**

- Services hold business rules and repositories only read and write files. `PersonService`
  validates through the `validation` package and delegates persistence to `PersonRepository`,
  and it does nothing else.
- `Client` and `Seller` only hold the data of a person, and `Person` holds what both share.
- `AccessoryService` and `PromotionService` manage the list and the rules of their own module
  and do not touch files or the console.

**Violations.** None found in the people, accessory and promotion modules. One related
problem was found in a different pattern: `ReturnService` registers returns and also computes
the monthly sales balance, so it has two reasons to change. It is handled as a Single
Responsibility change in Requirement 8 (V-56), by moving the balance to
`MonthlyBalanceService`.

## Polymorphism

Owner: Developer 1.

**What it says.** When behavior changes with the type of an object, give that behavior to the
type through a polymorphic method instead of asking for the class with `instanceof` or a
`switch`.

**Where GameZone applies it.**

- `Product.getDescription` is abstract and every product and accessory builds its own text.
- `Promotion.calculateDiscount` is abstract and each discount applies its own rule.
- `Warranty.getDurationInMonths` and `getAdditionalCost` let the constructor compute the end
  date without knowing which warranty it is.

**Violations.**

- **V-10** `CategoryDiscount.belongsToTargetCategory` used `instanceof VideoGame`, `Console`
  and `Accessory`. Fix: abstract `getCategory()` in `Product`, and the method became
  `return targetCategory.equals(product.getCategory());`.
- **V-11** Three classes used `instanceof Console` to know whether a product has a warranty.
  Fix: `isWarrantyEligible()` in `Product` (returns `false`) overridden in `Console` (returns
  `true`). The callers are fixed in V-02, V-03 and V-21.
- **V-13** An accessory could not say its type, so the menu tested `instanceof Controller`,
  `Cable` and `Memory`. Fix: abstract `getAccessoryType()` in `Accessory`, which returns the
  same tokens `AccessoryRepository` writes; Developer 2 uses it in V-23.
- **V-14** `Warranty` only had `getWarrantyType()`, which returns text for the user, so
  `WarrantyRepository` tested `instanceof BasicWarranty` to write the file. Fix: abstract
  `getTypeCode()` returning `"BASIC"` or `"EXTENDED"`; Developer 2 uses it in V-24.

`ProductRepository` still checks the product type when it reads and writes the file (V-15). It
was rejected as a violation because that mapping belongs to the repository, a Pure
Fabrication, and moving it to the model would make the model depend on the file format.

## Pure Fabrication

Owner: Developer 2.

**What it says.** When giving a responsibility to a domain class would lower its cohesion or
raise its coupling, create an artificial class that does not represent a domain concept and
give the responsibility to it.

**Where GameZone applies it.**

- `PersonRepository`, `AccessoryRepository`, `PromotionRepository`, `ReturnRepository` and
  `WarrantyRepository` are Pure Fabrications: reading and writing text files is not a concept
  of the store, and putting it in `Client`, `Accessory` or `Return` would make the model depend
  on the file format.
- The classes of the `validation` package, such as `SaleValidator`, group the data checks so
  the services do not repeat them.

**Violations.** The fabrications themselves are correct. One of them had a coupling problem:

- **V-20** `ReturnRepository` imported `SaleService`, `ProductService` and `AccessoryService`,
  so the persistence layer depended on the service layer. Fix: `loadAll(List<Sale> sales,
  List<Product> catalog)` searches in the lists it receives, `ReturnService` passes
  `saleService.listSales()` and the catalog, and the constructor has no parameters.

## Indirection

Owner: Technical Leader.

**What it says.** Put an intermediate object between two components so they do not depend on
each other directly.

**Where GameZone applies it.**

- The services sit between `ConsoleMenu` and the repositories, so the menu never touches a
  file.
- The repositories sit between the services and `data/*.txt`. They also translate
  `IOException` into `PersistenceException` (Requirement 6), so `java.io` errors do not leak
  into the business rules.
- `Main` is the only place that creates and connects the objects, so no class builds its own
  dependencies.

**Violations.**

- **V-06** `SaleService` talks to the concrete `SaleRepository` class. The indirection exists,
  but it is not protected by an interface, so a new storage would still change the service.
  Adding the `SaleRepository` interface is a Dependency Inversion change and is done in
  Requirement 8 (V-31).

## Protected Variations

Owner: Developer 2.

**What it says.** Identify the points where the system is likely to change and wrap them
behind a stable interface, so a change there does not affect the rest of the classes.

**Where GameZone applies it.**

- The file format is the main point of variation. Only the repositories know it, so the
  services and `ConsoleMenu` do not change if the format does.
- The repositories translate `IOException` into `PersistenceException`, so the callers depend
  on one exception type and not on `java.io`.
- Abstract methods such as `Product.getDescription` and `Promotion.calculateDiscount` protect
  the callers from the type of each object.

**Violations.**

- **V-20** (see Pure Fabrication) also belongs here: a change in a service could break the
  repository that imported it. It is fixed by the same change.
- **V-26** `WarrantyService` receives the concrete `SaleService` and `ProductService` but only
  calls `findById` on each. Deferred to Requirement 8 (V-55): it will depend on `SaleLookup`
  and `ProductLookup`.
- **V-57** `PromotionRepository` checks the promotion type with `instanceof` and a `switch`
  when it reads and writes the file. It was rejected as a violation for the same reason as
  V-15: the repository is the only class that knows the file format, so that mapping belongs
  there, and moving it to the model would make the model depend on the file.

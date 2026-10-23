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

## Creator

Owner: Developer 1.

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

## Polymorphism

Owner: Developer 1.

## Pure Fabrication

Owner: Developer 2.

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

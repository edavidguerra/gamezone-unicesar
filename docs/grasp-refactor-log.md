# GRASP Refactor Log

Every GRASP refactor commit is recorded here with its violation ID, the files it changed and
the result of the non-regression check. The baseline below was captured before any refactor,
so each later check is compared against it.

## Non-regression baseline

Captured on `develop` at commit `2f00b33` (2026-10-23), running `Main` with a copy of `data/`.
After each refactor the same inputs must give the same console output.

| # | Operation | Input | Expected output | Result |
|---|---|---|---|---|
| 1 | Register video game | VG90, Zelda, Switch, Aventura, E, 200000, stock 5 | `Zelda [Video game] - Platform: Switch, Genre: Aventura, Age rating: E, Price: 200000.0, Stock: 5` | OK |
| 2 | Register console | CO90, PS5, Sony, Slim, generation 9, 2500000, stock 1 | `PS5 [Console] - Brand: Sony, Model: Slim, Generation: 9, Price: 2500000.0, Stock: 1` | OK |
| 3 | List products | - | Shows VG90 and CO90 with the lines above | OK |
| 4 | Register client | C90, Ana, ana@mail.com | `Client - Email: ana@mail.com` | OK |
| 5 | List clients | - | Shows C90 Ana | OK |
| 6 | List sellers | - | Laura Gomez (V1), Carlos Perez (V2), Ana Torres (V3) | OK |
| 7 | Register sale | V90, client C90, seller V1, VG90 x1 + CO90 x1, extended warranty | `Subtotal: 2700000.0`, `Garantia extendida: +250000.0`, `Total a pagar: 2950000.0` | OK |
| 8 | List sales | - | `2026-10-23 \| Ana \| Laura Gomez \| Total: 2700000.0` | OK |
| 9 | Sales by client | C90 | Same line as operation 8 | OK |
| 10 | Sales by seller | V1 | Same line as operation 8 | OK |

Note: the sale listings (8 to 10) print the subtotal, while the receipt (7) prints the final
total with the warranty. This is recorded as audit finding V-01 and must not be "fixed" by
accident in an unrelated refactor.

### Error cases

| # | Case | Input | Expected output | Result |
|---|---|---|---|---|
| E1 | Duplicate client | C90 again | `[Operacion no permitida] Ya existe una persona con el identificador C90` | OK |
| E2 | Unknown product | XX1 | `[No encontrado] El recurso de tipo producto con identificador XX1 no fue encontrado.` | OK |
| E3 | Negative price | -5 | `[Dato invalido] El campo precio no es valido: debe ser mayor que cero.` | OK |
| E4 | Text in a number field | abc | `[Dato invalido] Debe ingresar un numero valido.` | OK |
| E5 | Not enough stock | CO90 x1 after selling the only unit | `[Operacion no permitida] Stock insuficiente para el producto: CO90` | OK |

## Refactors

One row per refactor commit, added in the same commit as the change.

| ID | Commit | Files | Change | Non-regression check |
|---|---|---|---|---|
| V-01 | `239c21c` | Sale, SaleService | Added `Sale.calculateFinalTotal()` (subtotal minus `discountAmount` plus `extendedWarrantyCost`). `registerSale` no longer keeps local `subtotal`, `discount` and `finalTotal` variables; it calls `sale.setFinalTotal(sale.calculateFinalTotal())` | Operations 1 to 10: OK. Receipt still shows `Total a pagar: 2950000.0` |
| V-02 | `0b4c0de` | SaleService | `registerSale` replaced `item instanceof Console` with `item.isWarrantyEligible()` and removed the unused `Console` import | Operations 1 to 10: OK. PS5 still gets the basic and extended warranty |
| V-03 | `989f294` | ConsoleMenu | `registerSale` asks for the extended warranty when `product != null && product.isWarrantyEligible()` instead of `product instanceof Console` | Operations 1 to 10 and E2, E5: OK. The `(s/n)` prompt still appears only for consoles |
| V-04 | `50d6a86` | ConsoleMenu | `listAccessoriesByTypeFlow` calls `accessoryService.listAccessoriesByType(type)` (V-23) and only prints the result; the `switch` with `instanceof Controller/Cable/Memory` was removed | Operations 1 to 10 and list accessories by type `CABLE`: OK. An unknown type now shows `[Dato invalido] El campo tipo no es valido: debe ser CONTROLLER, CABLE o MEMORY.` instead of an empty list |
| V-05 | - | SaleService, WarrantyService, Main | Deferred: the cycle is removed in the SOLID phase with the `SaleLookup` interface (V-32) | Not applicable |
| V-06 | - | SaleService, SaleRepository | Deferred: `SaleRepository` becomes an interface in the SOLID phase (V-31) | Not applicable |
| V-10 | `71c0bca` | Product, VideoGame, Accessory | Added abstract `getCategory()` to `Product`; `VideoGame` returns `VIDEOGAME` and `Accessory` `ACCESSORY` (`Console` is completed in the V-11 commit) | Operations 1, 2, 3 and 7: OK |
| V-10 | `0170d28` | CategoryDiscount | `belongsToTargetCategory` replaced its three `instanceof` checks with `product.getCategory().equals(targetCategory)` | Operations 1, 2, 3 and 7: OK |
| V-11 | `2faab25` | Product, Console | Added `isWarrantyEligible()` (false in `Product`, true in `Console`) and `Console.getCategory()` returning `CONSOLE`. Callers are changed by V-02, V-03 and V-21 | Operations 1, 2, 3 and 7: OK |
| V-12 | `9c004d2` | Product, ProductService | Added `Product.increaseStock(int)`; `ProductService.restoreStock` calls it instead of `setStock(getStock() + quantity)` | Operations 1, 2, 3 and 7: OK |
| V-13 | `c121391` | Accessory, Controller, Cable, Memory | Added abstract `getAccessoryType()` returning `CONTROLLER`, `CABLE` and `MEMORY` | Operations 1, 2, 3 and 7: OK |
| V-14 | `752bca4` | Warranty, BasicWarranty, ExtendedWarranty | Added abstract `getTypeCode()` returning `BASIC` and `EXTENDED`; `getWarrantyType()` stays for the certificate | Operations 1, 2, 3 and 7: OK |
| V-20 | `fe94892` | ReturnRepository, ReturnService, Main | `ReturnRepository` no longer imports the services: its constructor is empty and `loadAll(List<Sale>, List<Product>)` resolves sales and items from the lists it receives; `ReturnService` passes `listSales()` and the products plus accessories | Operations 1 to 10, list returns and register return with an unknown sale: OK |
| V-21 | `555b658` | ReturnService | `registerReturn` replaced `product instanceof Console` with `product.isWarrantyEligible()` and removed the unused `Console` import | Operations 1 to 10, list returns and list warranties: OK |
| V-22 | `7b78363` | ReturnService | `restoreItemStock` replaced `item instanceof Accessory` with `"ACCESSORY".equals(item.getCategory())` and removed the unused `Accessory` import | Operations 1 to 10 and register return: OK |
| V-23 | `c44fa4d` | AccessoryService | Added `listAccessoriesByType(String)`, which compares `getAccessoryType()` and throws `InvalidDataException` for an unknown type | Operations 1 to 10: OK |
| V-24 | `a24de8f` | WarrantyRepository | `saveAll` replaced `instanceof BasicWarranty` with `warranty.getTypeCode()` and removed the unused import | Operations 1 to 10 and list warranties: OK |
| V-25 | `df719f5` | AccessoryService | `restoreStock` calls `accessory.increaseStock(quantity)` instead of `setStock(getStock() + quantity)` | Operations 1 to 10 and register return: OK |
| V-26 | - | WarrantyService | Deferred: `WarrantyService` will depend on `SaleLookup` and `ProductLookup` in the SOLID phase (V-55) | Not applicable |

## Corrections

Changes made after a refactor broke a baseline case, with the cause and the fix.

## Final non-regression check

Run on `feature/grasp-refactor` after the leader commits (developer 1, developer 2 and leader
corrections applied), with a copy of `data/` and the same inputs as the baseline.

| Cases | Result |
|---|---|
| Operations 1 to 10 | Same output as the baseline. Sale listings still print the subtotal (`Total: 2700000.0`), as recorded in the baseline note; changing it is a behavior change, not a GRASP refactor |
| E1 to E5 | Same messages as the baseline |
| `mvn clean compile` | BUILD SUCCESS, no warnings |

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

## Corrections

Changes made after a refactor broke a baseline case, with the cause and the fix.

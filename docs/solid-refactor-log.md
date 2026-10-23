# SOLID Refactor Log

Every SOLID refactor commit is recorded here with its violation ID, the files it changed and
the result of the non-regression check. The checks use the same inputs and expected outputs as
the baseline in `docs/grasp-refactor-log.md` (10 menu operations and error cases E1 to E5).

## Refactors

One row per refactor commit.

| ID | Commit | Files | Change | Non-regression check |
|---|---|---|---|---|
| V-31 | `1981f73` | SaleRepository, FileSaleRepository, Main | `SaleRepository` is now an interface with `saveAll` and `loadAll`. The file code moved to `FileSaleRepository implements SaleRepository`, and `Main` creates `new FileSaleRepository()`. `SaleService` did not change: it already used the `SaleRepository` type | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-32 | `35987e8` | SaleLookup, SaleService | New interface `SaleLookup` with `findById` and `listSales`; `SaleService implements SaleLookup`. `WarrantyService` and `ReturnService` switch to it in V-55 (Developer 2), which removes the setter cycle of GRASP V-05 | Operations 1 to 10 and E1 to E5: same output as the baseline |

## Corrections

Changes made after a refactor broke a baseline case, with the cause and the fix.

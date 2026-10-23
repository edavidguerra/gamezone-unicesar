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

| V-43 | `c18de1b` | ProductRepository, FileProductRepository, Main | `ProductRepository` is now an interface with `saveAll` and `loadAll`. The file code moved to `FileProductRepository implements ProductRepository` (renamed with `git mv`), and `Main` creates `new FileProductRepository()`. `ProductService` did not change: it already used the `ProductRepository` type | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-44 | `165b95d` | ProductLookup, ProductService | New interface `ProductLookup` with `findById`; `ProductService implements ProductLookup`. `WarrantyService` switches to it in V-55 (Developer 2). `ReturnService` uses `listProducts` and `restoreStock`, so it keeps the whole `ProductService` | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-42 | `919b267` | Warranty | Removed the `endDate` field and the unused `setEndDate`; `getEndDate()` computes `startDate.plusMonths(getDurationInMonths())` and `isActive` and the certificate use it, so the constructor no longer calls an abstract method | Operations 1 to 10 and E1 to E5: same output as the baseline. "Listar garantias proximas a vencer" checked: same list |
| V-40 | - | - | Rejected in the audit: the type checks in `FileProductRepository` stay because the repository is the only class that knows the file format (same reason as GRASP V-15) | Not applicable |

## Corrections

Changes made after a refactor broke a baseline case, with the cause and the fix.

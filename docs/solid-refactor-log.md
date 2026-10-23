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
| V-50 | `f505fe0` | PersonRepository, FilePersonRepository, Main | `PersonRepository` is now an interface with `saveClients`, `loadClients`, `saveSellers` and `loadSellers`. The file code moved to `FilePersonRepository implements PersonRepository` (renamed with `git mv`), and `Main` creates `new FilePersonRepository()` | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-51 | `04b25f5` | AccessoryRepository, FileAccessoryRepository, Main | `AccessoryRepository` is now an interface with `saveAll` and `loadAll`. The file code moved to `FileAccessoryRepository`, and `Main` creates `new FileAccessoryRepository()` | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-54 | `27442fe` | WarrantyRepository, FileWarrantyRepository, Main | `WarrantyRepository` is now an interface with `saveAll` and `loadRawRecords`; `WarrantyRecord` moved inside it and stays public. The file code moved to `FileWarrantyRepository`, and `Main` creates `new FileWarrantyRepository()` | Operations 1 to 10, E1 to E5 and list warranties: same output as the baseline |
| V-52 | `4c285cd` | PromotionRepository, FilePromotionRepository, Main | `PromotionRepository` is now an interface with `saveAll` and `loadAll`. The file code moved to `FilePromotionRepository`, and `Main` creates `new FilePromotionRepository()` | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-53 | `2023b47` | ReturnRepository, FileReturnRepository, Main | `ReturnRepository` is now an interface with `saveAll` and `loadAll(List<Sale>, List<Product>)`. The file code moved to `FileReturnRepository`, and `Main` creates `new FileReturnRepository()` | Operations 1 to 10, E1 to E5 and list returns: same output as the baseline |
| V-55 | `259a487` | WarrantyService | The fields and the constructor receive `SaleLookup` and `ProductLookup` instead of the concrete `SaleService` and `ProductService`. `Main` passes the same objects | Operations 1 to 10, E1 to E5 and list warranties: same output as the baseline |
| V-55 | `a3ad9eb` | ReturnService | The field and the constructor receive `SaleLookup` instead of `SaleService`. It keeps the concrete `ProductService` because it also uses `listProducts` and `restoreStock` | Operations 1 to 10, E1 to E5 and list returns: same output as the baseline |
| V-56 | `fc3599d` | MonthlyBalanceService | New class with `calculateMonthlySales`, `calculateMonthlyReturns` and `generateMonthlyBalance`, receiving `SaleLookup` and `ReturnService`. The old methods stay in `ReturnService` until the Technical Leader connects the menu and removes them | Operations 1 to 10 and E1 to E5: same output as the baseline |
| V-57 | - | - | Rejected in the audit: `PromotionRepository` checks the promotion type when it reads and writes the file because the repository is the only class that knows the file format (same reason as GRASP V-15 and V-40) | Not applicable |

## Corrections

Changes made after a refactor broke a baseline case, with the cause and the fix.
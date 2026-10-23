# SOLID Audit - Technical Leader

Auditor: Ever Guerra (Technical Leader). ID range: V-30 to V-39.
Audited on `develop` at commit `fd40f64` (after the GRASP refactor).

Scope: `ui/ConsoleMenu`, `model/Sale`, `service/SaleService`, `persistence/SaleRepository`
and `Main`. Line numbers refer to that commit.

Rows with `-` as ID are principles that were checked and have no violation in this scope.

## Single Responsibility

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-30 | SRP | ConsoleMenu (534 lines): productsMenu (96), peopleMenu (140), salesMenu (175), accessoriesMenu (235), promotionsMenu (309), warrantiesMenu (372), returnsMenu (423), execute (508), readInt (528) | One class holds the input/output flows of the seven modules, owns the `Scanner` (52, 65), converts the input and catches every exception category | ConsoleMenu has at least eight reasons to change: any change in the screens of one module, in how input is read or in how errors are shown edits the same file. It is also the class with the most merge conflicts between members | Split it inside `ui`: `ConsoleInput` (Scanner, `readInt`, `readDouble`, `execute`) and one class per module: `ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `WarrantyMenu`, `ReturnMenu`. `ConsoleMenu` only shows the main menu and delegates to them | High |
| V-33 | SRP | Sale.generateReceipt (105-119) | The model builds the receipt text in Spanish with a `StringBuilder` | A model class formats text for the user, so it could change for presentation reasons | Rejected: `Sale` owns every value on the receipt (Information Expert), the method returns a `String` and does not print or depend on the console. Moving it would make another class read six getters to rebuild the same text | Rejected |
| - | SRP | SaleService.registerSale (54-120) | Validates, builds the sale, applies the best promotion, assigns warranties, reduces stock and saves | No violation: it is the use case "register a sale". Each step is delegated (validators, `PromotionService`, `WarrantyService`, `ProductService`, the repository) and the service only coordinates them | - | - |
| - | SRP | SaleRepository (whole class) | Only reads and writes `data/sales.txt` and converts `IOException` into `PersistenceException` | No violation: one reason to change, the file format | - | - |

## Dependency Inversion and Interface Segregation

| ID | Pattern/Principle | Class.method (line) | Evidence | Why it is a violation | Proposed fix | Severity |
|---|---|---|---|---|---|---|
| V-31 | DIP | SaleService field (24) and constructor (32); Main.main (37) | `private SaleRepository saleRepository;` and `new SaleRepository()`: the service depends on the concrete class that writes `data/sales.txt` | A high-level module (sale rules) depends on a low-level detail (a text file). Changing the storage or testing the service with fake data means editing `SaleService`. This is the GRASP finding V-06, deferred to this phase | Turn `SaleRepository` into an interface with `saveAll` and `loadAll`, move the current code to `FileSaleRepository implements SaleRepository`, and create it in `Main` | Medium |
| V-32 | ISP / DIP | WarrantyService field (20) and constructor (24); ReturnService field (23) and constructor (38); SaleService.setWarrantyService (50); Main.main (49-50) | `WarrantyService` only calls `saleService.findById` (36) and `ReturnService` only calls `findById` (65) and `listSales` (48, 179), but both receive the whole concrete `SaleService` | Both clients depend on `registerSale`, `listSalesByClient`, `listSalesBySeller` and `setWarrantyService`, which they never use. It also keeps the cycle SaleService ↔ WarrantyService (GRASP V-05), which forces `Main` to inject `WarrantyService` with a setter after both objects exist | Add `SaleLookup` in `service` with `Sale findById(String id)` and `List<Sale> listSales()`. `SaleService implements SaleLookup`; `WarrantyService` and `ReturnService` receive a `SaleLookup`. Developer 2 changes those two services (V-55) | Medium |
| - | ISP | SaleRepository (32, 65) | Two public methods, both used by `SaleService` | No violation: the only client uses the whole class | - | - |

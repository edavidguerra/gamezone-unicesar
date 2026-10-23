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

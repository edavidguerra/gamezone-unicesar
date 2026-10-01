# Integrated Class Diagram

This document shows the final structure of the system after integrating the four modules of Workshop 2 (accessories, promotions, warranties and returns) with the base of Workshop 1 (products, people and sales). It is split in two diagrams to keep them readable: the model layer, and the service and persistence layers with the menu.

## 1. Model layer

```mermaid
classDiagram
    class Person {
        <<abstract>>
        -id : String
        -name : String
        -phone : String
        +getRoleDescription()* String
    }
    class Client {
        -email : String
    }
    class Seller {
        -employeeCode : String
        -shift : String
    }
    Person <|-- Client
    Person <|-- Seller

    class Product {
        <<abstract>>
        -id : String
        -title : String
        -price : double
        -stock : int
        +getDescription()* String
    }
    class VideoGame {
        -platform : String
        -genre : String
        -ageRating : String
    }
    class Console {
        -brand : String
        -model : String
        -generation : String
    }
    class Accessory {
        <<abstract>>
        -compatibleConsoleIds : List~String~
        +isCompatibleWith(consoleId String) boolean
    }
    class Controller {
        -connectionType : String
    }
    class Cable {
        -lengthInMeters : double
        -connectorType : String
    }
    class Memory {
        -capacityInGigabytes : int
        -memoryType : String
    }
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    class Sale {
        -id : String
        -date : String
        -appliedPromotionName : String
        -discountAmount : double
        -extendedWarrantyCost : double
        -finalTotal : double
        +calculateTotal() double
        +generateReceipt() String
        +canBeReturned() boolean
    }
    Sale "*" --> "1" Client
    Sale "*" --> "1" Seller
    Sale "*" --> "1..*" Product : sold items

    class Promotion {
        <<abstract>>
        -id : String
        -name : String
        -startDate : LocalDate
        -endDate : LocalDate
        +isActive(date LocalDate) boolean
        +calculateDiscount(sale Sale)* double
    }
    class PercentageDiscount {
        -percentage : double
    }
    class CategoryDiscount {
        -percentage : double
        -targetCategory : String
    }
    class BulkPurchaseDiscount {
        -minimumQuantity : int
        -percentage : double
    }
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Promotion ..> Sale : calculates discount of
    CategoryDiscount ..> Accessory : ACCESSORY category

    class Warranty {
        <<abstract>>
        -id : String
        -startDate : LocalDate
        -endDate : LocalDate
        +isActive(date LocalDate) boolean
        +getDurationInMonths()* int
        +getWarrantyType()* String
        +getAdditionalCost()* double
    }
    class BasicWarranty
    class ExtendedWarranty
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Warranty "*" --> "1" Product : covers
    Warranty "*" --> "1" Sale : originates from

    class Return {
        -id : String
        -date : LocalDate
        -originalSale : Sale
        -returnedProducts : List~Product~
        -reason : String
        -refundAmount : double
        -warrantyRefund : double
        +calculateRefundAmount() double
        +addWarrantyRefund(amount double)
        +generateReturnReceipt() String
    }
    Return "*" --> "1" Sale : originalSale
    Return "*" --> "1..*" Product : returnedProducts
```

Notes on the model:

- `Sale` stores the result of the sale (promotion name, discount, extended warranty cost and final total), so a return can refund according to what the customer really paid (adjustment A5).
- `Return` keeps the part of the refund that comes from cancelled warranties apart from the refund of the items (`warrantyRefund`), as defined in adjustment A7.
- A `Sale` and a `Return` can contain any `Product`, which includes accessories because `Accessory` extends `Product`.

## 2. Service, persistence and menu layers

```mermaid
classDiagram
    class ConsoleMenu {
        +start()
    }
    class Main {
        +main(args String[])$
    }
    Main ..> ConsoleMenu : creates

    class ProductService {
        +registerVideoGame(...) VideoGame
        +registerConsole(...) Console
        +findById(id String) Product
        +hasStock(id String, quantity int) boolean
        +reduceStock(id String, quantity int)
        +restoreStock(id String, quantity int)
    }
    class AccessoryService {
        +registerController(...) Controller
        +registerCable(...) Cable
        +registerMemory(...) Memory
        +findById(id String) Accessory
        +hasStock(id String, quantity int) boolean
        +updateStock(id String, quantity int)
        +restoreStock(id String, quantity int)
    }
    class PersonService {
        +registerClient(...) Client
        +findClientById(id String) Client
        +findSellerById(id String) Seller
    }
    class PromotionService {
        +registerPercentageDiscount(...) PercentageDiscount
        +registerCategoryDiscount(...) CategoryDiscount
        +registerBulkPurchaseDiscount(...) BulkPurchaseDiscount
        +findBestPromotionFor(sale Sale) Promotion
    }
    class SaleService {
        +setWarrantyService(warrantyService WarrantyService)
        +registerSale(...) Sale
        +listSalesByClient(clientId String) List~Sale~
        +findById(id String) Sale
    }
    class WarrantyService {
        +assignBasicWarranty(...) BasicWarranty
        +assignExtendedWarranty(...) ExtendedWarranty
        +cancelWarranties(productId String, saleId String) double
        +listActiveWarranties() List~Warranty~
    }
    class ReturnService {
        +registerReturn(saleId String, productIds List~String~, reason String) Return
        +viewReturnsByCustomer(customerId String) List~Return~
        +viewReturnsBySale(saleId String) List~Return~
        +calculateMonthlySales(month int, year int) double
        +calculateMonthlyReturns(month int, year int) double
        +generateMonthlyBalance(month int, year int) double
    }

    class ProductRepository
    class AccessoryRepository
    class PersonRepository
    class PromotionRepository
    class SaleRepository
    class WarrantyRepository
    class ReturnRepository

    ConsoleMenu --> ProductService
    ConsoleMenu --> AccessoryService
    ConsoleMenu --> PersonService
    ConsoleMenu --> PromotionService
    ConsoleMenu --> SaleService
    ConsoleMenu --> WarrantyService
    ConsoleMenu --> ReturnService

    SaleService --> SaleRepository
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> PersonService
    SaleService --> PromotionService
    SaleService ..> WarrantyService : injected by setter

    WarrantyService --> WarrantyRepository
    WarrantyService --> SaleService
    WarrantyService --> ProductService

    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService

    ReturnRepository ..> SaleService : resolves sales
    ReturnRepository ..> ProductService : resolves products
    ReturnRepository ..> AccessoryService : resolves accessories

    ProductService --> ProductRepository
    AccessoryService --> AccessoryRepository
    PersonService --> PersonRepository
    PromotionService --> PromotionRepository
```

Notes on the services:

- **Inventory.** `ProductService` owns the stock of video games and consoles, and `AccessoryService` owns the stock of accessories. `SaleService` and `ReturnService` always delegate to the service that owns the item (adjustments A3 and A4).
- **Construction order.** `SaleService` is built first without a `WarrantyService`; then `WarrantyService` is built, and it is injected back with `setWarrantyService` (adjustment A2). `ReturnRepository` and `ReturnService` are built last, because they need the sale, inventory and warranty services.
- **Returns and warranties.** `ReturnService` is the only class that connects both modules: when a console is returned, it calls `WarrantyService.cancelWarranties` and adds the refundable cost to the return (adjustment A7).
- **Monthly balance.** `ReturnService` reads the sales through `SaleService`, so the dependency goes from returns to sales and never the other way (adjustment A6).
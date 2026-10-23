# GRASP Class Diagram

Classes changed by the GRASP refactor, before (`develop` at `2f00b33`) and after
(`feature/grasp-refactor`). Only the members involved in the corrections are shown. The ID on
each note is the violation from `docs/grasp-audit/consolidated-audit.md`.

## Before

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -int stock
        +hasStock(int quantity) boolean
        +reduceStock(int quantity)
        +getDescription()* String
    }
    class VideoGame
    class Console
    class Accessory {
        <<abstract>>
    }
    class Controller
    class Cable
    class Memory
    class Warranty {
        <<abstract>>
        +getWarrantyType()* String
    }
    class BasicWarranty
    class ExtendedWarranty
    class Sale {
        -double discountAmount
        -double extendedWarrantyCost
        -double finalTotal
        +calculateTotal() double
        +setFinalTotal(double finalTotal)
    }
    class SaleService {
        +registerSale(...) Sale
    }
    class ConsoleMenu {
        -registerSale()
        -listAccessoriesByTypeFlow()
    }
    class CategoryDiscount {
        -belongsToTargetCategory(Product product) boolean
    }
    class ReturnService
    class ReturnRepository {
        +ReturnRepository(SaleService, ProductService, AccessoryService)
    }
    class WarrantyRepository
    class ProductService
    class AccessoryService

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    SaleService ..> Sale : computes finalTotal (V-01)
    SaleService ..> Console : instanceof (V-02)
    ConsoleMenu ..> Console : instanceof (V-03)
    ConsoleMenu ..> Controller : instanceof (V-04)
    ConsoleMenu ..> Cable : instanceof (V-04)
    ConsoleMenu ..> Memory : instanceof (V-04)
    CategoryDiscount ..> VideoGame : instanceof (V-10)
    CategoryDiscount ..> Console : instanceof (V-10)
    ProductService ..> Product : setStock(getStock() + q) (V-12)
    AccessoryService ..> Accessory : setStock(getStock() + q) (V-25)
    ReturnService ..> Console : instanceof (V-21)
    ReturnService ..> Accessory : instanceof (V-22)
    WarrantyRepository ..> BasicWarranty : instanceof (V-24)
    ReturnRepository ..> SaleService : persistence depends on service (V-20)
```

## After

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -int stock
        +hasStock(int quantity) boolean
        +reduceStock(int quantity)
        +increaseStock(int quantity)
        +getCategory()* String
        +isWarrantyEligible() boolean
        +getDescription()* String
    }
    class VideoGame {
        +getCategory() String
    }
    class Console {
        +getCategory() String
        +isWarrantyEligible() boolean
    }
    class Accessory {
        <<abstract>>
        +getCategory() String
        +getAccessoryType()* String
    }
    class Controller {
        +getAccessoryType() String
    }
    class Cable {
        +getAccessoryType() String
    }
    class Memory {
        +getAccessoryType() String
    }
    class Warranty {
        <<abstract>>
        +getWarrantyType()* String
        +getTypeCode()* String
    }
    class BasicWarranty {
        +getTypeCode() String
    }
    class ExtendedWarranty {
        +getTypeCode() String
    }
    class Sale {
        -double discountAmount
        -double extendedWarrantyCost
        -double finalTotal
        +calculateTotal() double
        +calculateFinalTotal() double
    }
    class SaleService {
        +registerSale(...) Sale
    }
    class ConsoleMenu {
        -registerSale()
        -listAccessoriesByTypeFlow()
    }
    class AccessoryService {
        +listAccessoriesByType(String type) List~Accessory~
        +restoreStock(String id, int quantity)
    }
    class CategoryDiscount {
        -belongsToTargetCategory(Product product) boolean
    }
    class ReturnService
    class ReturnRepository {
        +ReturnRepository()
        +loadAll(List~Sale~ sales, List~Product~ catalog) List~Return~
    }
    class WarrantyRepository
    class ProductService

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    SaleService ..> Sale : calculateFinalTotal() (V-01)
    SaleService ..> Product : isWarrantyEligible() (V-02)
    ConsoleMenu ..> Product : isWarrantyEligible() (V-03)
    ConsoleMenu ..> AccessoryService : listAccessoriesByType() (V-04)
    AccessoryService ..> Accessory : getAccessoryType() (V-23), increaseStock() (V-25)
    CategoryDiscount ..> Product : getCategory() (V-10)
    ProductService ..> Product : increaseStock() (V-12)
    ReturnService ..> Product : isWarrantyEligible() (V-21), getCategory() (V-22)
    WarrantyRepository ..> Warranty : getTypeCode() (V-24)
    ReturnService ..> ReturnRepository : passes sales and catalog (V-20)
```

## Still open after GRASP

- **V-05** `SaleService` and `WarrantyService` still depend on each other through
  `setWarrantyService`. Fixed in Requirement 8 with `SaleLookup` (V-32).
- **V-06** `SaleService` still uses the concrete `SaleRepository`. Fixed in Requirement 8 (V-31).
- **V-26** `WarrantyService` still receives whole `SaleService` and `ProductService`. Fixed in
  Requirement 8 (V-55).

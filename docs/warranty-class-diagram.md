# Class Diagram: Warranty Module

This document contains the class diagram for the Warranty module in the GameZone Unicesar system, illustrating the abstract `Warranty` class, its concrete subclasses, and their relationships with `Product` and `Sale`.

---

## Mermaid Diagram

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        +getPrice() double
        +getTitle() String
    }

    class Sale {
        -String id
        -LocalDate date
        -List~Product~ products
    }

    class Warranty {
        <<abstract>>
        -String id
        -Product product
        -Sale sale
        -LocalDate startDate
        -LocalDate endDate
        +Warranty(String id, Product product, Sale sale, LocalDate startDate)
        +getId() String
        +getProduct() Product
        +getSale() Sale
        +getStartDate() LocalDate
        +getEndDate() LocalDate
        +isActive(LocalDate date) boolean
        +getDurationInMonths()* int
        +getWarrantyType()* String
        +getAdditionalCost()* double
        +generateWarrantyCertificate() String
    }

    class BasicWarranty {
        +BasicWarranty(String id, Product product, Sale sale, LocalDate startDate)
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    class ExtendedWarranty {
        +ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate)
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Warranty --> Product : covers
    Warranty --> Sale : originatedFrom
    
---

## Persistence and Service Layer — Circular Dependency Fix (Ajuste A2)

The initial version of `WarrantyRepository` resolved `Sale` and `Product`
references directly, using `SaleService` and `ProductService` internally.
This created a circular dependency: `SaleService` needed a
`WarrantyService` to assign warranties during a sale, but
`WarrantyService` built a `WarrantyRepository` that in turn needed
`SaleService` already constructed to resolve each warranty's sale.

The fix inverts this dependency. `WarrantyRepository` no longer resolves
anything — it only reads and writes raw ids through a `WarrantyRecord`
holder. `WarrantyService` receives `SaleService` and `ProductService`
directly and is now responsible for resolving those raw records into
real `Warranty` objects.

```mermaid
classDiagram
    class WarrantyRecord {
        +String type
        +String id
        +String productId
        +String saleId
        +String startDate
    }

    class WarrantyRepository {
        +saveAll(List~Warranty~ warranties) void
        +loadRawRecords() List~WarrantyRecord~
    }

    class WarrantyService {
        -WarrantyRepository repository
        -SaleService saleService
        -ProductService productService
        +WarrantyService(WarrantyRepository, SaleService, ProductService)
        -resolveRecords(List~WarrantyRecord~) List~Warranty~
        +assignBasicWarranty(Product, Sale, LocalDate) BasicWarranty
        +assignExtendedWarranty(Product, Sale, LocalDate) ExtendedWarranty
    }

    WarrantyRepository --> WarrantyRecord : produces
    WarrantyService --> WarrantyRepository : reads raw records from
    WarrantyService --> SaleService : resolves Sale references
    WarrantyService --> ProductService : resolves Product references
```

This linear order removes the cycle: `SaleService` is built first
without a `WarrantyService` (its constructor does not require one).
`WarrantyRepository` and `WarrantyService` are built next, using the
already-constructed `SaleService` and `ProductService`. Finally,
`WarrantyService` is injected back into `SaleService` through a setter
(`saleService.setWarrantyService(warrantyService)`), avoiding the need
for either object to exist before the other during construction.
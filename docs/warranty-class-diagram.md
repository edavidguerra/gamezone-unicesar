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
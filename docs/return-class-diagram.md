# Class Diagram: Return Module

This document contains the class diagram for the Return module in the GameZone Unicesar system, illustrating the `Return` entity and its relationships with `Sale` and `Product`.

---

## Mermaid Diagram

```mermaid
classDiagram
    class Sale {
        -String id
        -String date
        +canBeReturned() boolean
    }

    class Product {
        <<abstract>>
        -String id
        -double price
        +getPrice() double
    }

    class Return {
        -String id
        -LocalDate date
        -final Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        +calculateRefundAmount() double
        +generateReturnReceipt() String
    }

    Return --> "1" Sale : originalSale
    Return --> "*" Product : returnedProducts
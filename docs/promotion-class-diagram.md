# Diagrama de Clases - Módulo de Promociones

El presente documento describe la estructura de clases del módulo de promociones para el sistema GameZone Unicesar, representado mediante sintaxis Mermaid.

1. Diagrama Mermaid

classDiagram
    class Promotion {
        <<abstract>>
        #String id
        #String name
        #LocalDate startDate
        #LocalDate endDate
        +Promotion(String id, String name, LocalDate startDate, LocalDate endDate)
        +getId() String
        +getName() String
        +getStartDate() LocalDate
        +getEndDate() LocalDate
        +isActiveOn(LocalDate date) boolean
        +calculateDiscount(Sale sale)* double
    }

    class PercentageDiscount {
        -double percentage
        +PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage)
        +getPercentage() double
        +calculateDiscount(Sale sale) double
    }

    class CategoryDiscount {
        -String targetCategory
        -double percentage
        +CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String targetCategory, double percentage)
        +getTargetCategory() String
        +getPercentage() double
        +calculateDiscount(Sale sale) double
    }

    class BulkPurchaseDiscount {
        -int minQuantity
        -double percentage
        +BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minQuantity, double percentage)
        +getMinQuantity() int
        +getPercentage() double
        +calculateDiscount(Sale sale) double
    }

    class Sale {
        -String id
        -String date
        -Client client
        -Seller seller
        -List~Product~ products
        -String appliedPromotionName
        -double discountAmount
        +Sale(String id, String date, Client client, Seller seller, List~Product~ products)
        +calculateTotal() double
        +getAppliedPromotionName() String
        +setAppliedPromotionName(String appliedPromotionName) void
        +getDiscountAmount() double
        +setDiscountAmount(double discountAmount) void
        +generateReceipt() String
    }

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Sale ..> Promotion : usa / evalúa


2. Descripción de Relaciones y Componentes

Jerarquía de Promociones (Promotion):

Promotion actúa como la clase abstracta base.

PercentageDiscount, CategoryDiscount y BulkPurchaseDiscount heredan de Promotion e implementan el cálculo específico en calculateDiscount(Sale sale).

Asociación y Evaluación en Sale:

La clase Sale utiliza las instancias de Promotion para evaluar los descuentos acumulados o aplicables.

Guarda los resultados calculados en los campos appliedPromotionName y discountAmount para proyectarlos en la generación del recibo (generateReceipt()).
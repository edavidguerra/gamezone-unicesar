# Class Diagram: Return Module

This document contains the class diagram of the Return module in the GameZone Unicesar system. It shows the Return class and its relationship with Sale and Product, the persistence and service classes of the module, and how the module is integrated with the existing system (SaleService, ProductService, WarrantyService and the ConsoleMenu extension).

## Mermaid Diagram

```mermaid
classDiagram
    class ConsoleMenu {
        -ReturnService returnService
        -returnsMenu() void
        -registerReturnFlow() void
        -listAllReturnsFlow() void
        -listReturnsByCustomerFlow() void
        -listReturnsBySaleFlow() void
        -showMonthlyBalanceFlow() void
    }

    class Sale {
        -String id
        -String date
        -Client client
        -List~Product~ products
        -double discountAmount
        -double finalTotal
        +canBeReturned() boolean
        +calculateTotal() double
        +getFinalTotal() double
    }

    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        -int stock
        +getPrice() double
        +getTitle() String
    }

    class Return {
        -String id
        -LocalDate date
        -Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        -double warrantyRefund
        +Return(String id, LocalDate date, Sale originalSale, List~Product~ returnedProducts, String reason)
        +getId() String
        +getDate() LocalDate
        +getOriginalSale() Sale
        +getReturnedProducts() List~Product~
        +getReason() String
        +getRefundAmount() double
        +getWarrantyRefund() double
        +addWarrantyRefund(double amount) void
        +calculateRefundAmount() double
        +getDiscountRate() double
        +calculateItemDiscount(Product product) double
        +calculateItemRefund(Product product) double
        +generateReturnReceipt() String
    }

    class ReturnRepository {
        -String FILE_PATH
        -SaleService saleService
        -ProductService productService
        +ReturnRepository(SaleService saleService, ProductService productService)
        +saveAll(List~Return~ returns) void
        +loadAll() List~Return~
    }

    class ReturnService {
        -ReturnRepository repository
        -SaleService saleService
        -ProductService productService
        -WarrantyService warrantyService
        -List~Return~ returns
        +ReturnService(ReturnRepository repository, SaleService saleService, ProductService productService, WarrantyService warrantyService)
        +registerReturn(String saleId, List~String~ productIds, String reason) Return
        +viewAllReturns() List~Return~
        +viewReturnsByCustomer(String customerId) List~Return~
        +viewReturnsBySale(String saleId) List~Return~
        +generateMonthlyBalance(int month, int year) double
        -calculateMonthlySales(int month, int year) double
        -calculateMonthlyReturns(int month, int year) double
    }

    class SaleService {
        +findById(String id) Sale
        +listSales() List~Sale~
    }

    class ProductService {
        +findById(String id) Product
        +restoreStock(String id, int quantity) void
    }

    class WarrantyService {
        +cancelWarranties(List~String~ consoleIds) double
    }

    ConsoleMenu --> ReturnService : uses
    ReturnService --> ReturnRepository : persists through
    ReturnService --> SaleService : finds and lists sales
    ReturnService --> ProductService : restores stock
    ReturnService --> WarrantyService : cancels warranties
    ReturnRepository --> SaleService : resolves original sale
    ReturnRepository --> ProductService : resolves products
    Return "*" --> "1" Sale : originalSale
    Return "*" --> "*" Product : returnedProducts
    Sale "1" --> "*" Product : products
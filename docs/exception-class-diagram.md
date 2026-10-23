# Exception Class Diagram

## Exceptions and validators

```mermaid
classDiagram
    class RuntimeException
    class GameZoneException {
        <<abstract>>
        -String errorCode
        +GameZoneException(String message)
        +GameZoneException(String message, String errorCode)
        +getErrorCode() String
    }
    class ResourceNotFoundException {
        +ResourceNotFoundException(String resourceType, String identifier)
    }
    class BusinessRuleException {
        +BusinessRuleException(String message)
    }
    class InvalidDataException {
        +InvalidDataException(String fieldName, String description)
    }
    class PersistenceException {
        +PersistenceException(String message, Throwable cause)
    }
    class ProductValidator {
        +validateProductData(String id, String title, double price, int stock)$
        +validateStockAvailability(Product product, int requestedQuantity)$
        +validateProductExists(Product product, String productId)$
    }
    class PersonValidator {
        +validatePersonData(String id, String name, String email, String phone)$
        +validatePersonExists(Person person, String personId)$
        +validateUniquePerson(List existing, String newId)$
    }
    class SaleValidator {
        +validateSaleData(Client customer, Seller seller, List products)$
        +validateSaleExists(Sale sale, String saleId)$
    }
    RuntimeException <|-- GameZoneException
    GameZoneException <|-- ResourceNotFoundException
    GameZoneException <|-- BusinessRuleException
    GameZoneException <|-- InvalidDataException
    GameZoneException <|-- PersistenceException
    ProductValidator ..> ResourceNotFoundException : throws
    ProductValidator ..> BusinessRuleException : throws
    ProductValidator ..> InvalidDataException : throws
    PersonValidator ..> ResourceNotFoundException : throws
    PersonValidator ..> BusinessRuleException : throws
    PersonValidator ..> InvalidDataException : throws
    SaleValidator ..> ResourceNotFoundException : throws
    SaleValidator ..> BusinessRuleException : throws
    SaleValidator ..> InvalidDataException : throws
```
```mermaid
classDiagram
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    class Accessory {
        <<abstract>>
        -List~String~ compatibleConsoleIds
        +addCompatibleConsole(consoleId)
        +isCompatibleWith(consoleId) bool
        +getDescription()* String
    }

    class Controller {
        -String connectionType
        +getConnectionType() String
        +setConnectionType(connectionType)
        +getDescription() String
    }

    class Cable {
        -double lengthInMeters
        -String connectorType
        +getLengthInMeters() double
        +setLengthInMeters(lengthInMeters)
        +getConnectorType() String
        +setConnectorType(connectorType)
        +getDescription() String
    }

    class Memory {
        -int capacityInGigabytes
        -String memoryType
        +getCapacityInGigabytes() int
        +setCapacityInGigabytes(capacityInGigabytes)
        +getMemoryType() String
        +setMemoryType(memoryType)
        +getDescription() String
    }
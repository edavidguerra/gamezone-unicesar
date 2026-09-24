# Accessory Module Analysis

## 1. Why does Accessory extend Product instead of being an independent class?
`Accessory` extends `Product` to achieve code reuse and polymorphism. Since accessories are sellable items, they share core attributes (`id`, `title`, `price`, and `stock`) with `VideoGame` and `Console`. Extending `Product` allows accessories to be processed seamlessly by inventory and sales services alongside other products.

## 2. Why is Accessory declared abstract?
`Accessory` is declared abstract because a generic, unspecialized accessory does not exist within the business context of GameZone Unicesar. Every physical accessory sold is specifically a controller, a cable, or a memory device. Declaring the class abstract prevents accidental instantiation of plain accessories.

## 3. What relationship exists between Accessory and console ids?
The relationship between `Accessory` and `Console` is an association by ID (`List<String> compatibleConsoleIds`), not a composition or aggregation of object references. Consoles exist independently of accessories, and referencing them by ID prevents unnecessary coupling and avoids complex object graphs during persistence.

## 4. Why is getDescription() overridden again in each subclass instead of reusing Accessory's version?
Although `Accessory` provides a general implementation format, `getDescription()` is overridden in each subclass (`Controller`, `Cable`, `Memory`) to achieve polymorphic representation. Each subclass possesses unique attributes (such as `connectionType`, `lengthInMeters`/`connectorType`, or `capacityInGigabytes`/`memoryType`) that must be appended to the descriptive string.

## 5. Which layer will be responsible for turning this list of ids into real Console objects, and why doesn't Accessory do it itself?
The service layer (specifically `ProductService`) is responsible for resolving console IDs into concrete `Console` objects. The model layer (`Accessory`) must remain clean of business logic, file access, and dependencies on other services or repositories, upholding a strict separation of concerns across application layers.
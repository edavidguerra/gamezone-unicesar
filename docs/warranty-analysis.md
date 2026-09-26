# Requirements Analysis and Design: Warranty Module

This document details the design decisions and requirements analysis for the Warranty module in the GameZone Unicesar system.

---

## Technical Questions & Answers

### 1. Why does Warranty associate with both Product and Sale instead of embedding copies of their data?
**Answer:** 
Associating `Warranty` directly with `Product` and `Sale` references maintains a single source of truth across the system. Embedding redundant copies of product details or sale transactions would lead to data duplication and potential inconsistencies if product information is updated. By storing direct object references (or their identifiers in persistence), the warranty dynamically reflects the exact entities involved without risking state drift.

---

### 2. Why is the end date computed in the constructor instead of stored directly from user input?
**Answer:** 
The warranty end date is a derived value that strictly depends on two factors: the start date and the duration defined by the specific warranty type (e.g., 3 months for basic, 12 months for extended). Computing `endDate` automatically inside the constructor guarantees strict data integrity and eliminates the risk of human error or contradictory dates provided via user input.

---

### 3. Why is calling an overridable method from the constructor safe here specifically?
**Answer:** 
In Java, invoking overridable instance methods from a superclass constructor is generally considered an anti-pattern because subclass fields may not be initialized yet. However, in this specific hierarchy, subclass overrides (`getDurationInMonths()`) return primitive literal constants (`3` or `12`) and do not depend on any instance fields or uninitialized subclass state, making the operation safe.

---

### 4. Why does `ExtendedWarranty.getAdditionalCost()` read `getProduct().getPrice()` instead of receiving the price as a constructor parameter?
**Answer:** 
Reading the price dynamically through `getProduct().getPrice()` guarantees that the surcharge calculation is always based on the live product entity state. Passing the price as a separate constructor parameter would duplicate product data into the warranty instance, creating a risk of stale or mismatched values if the pricing structure evolves.

---

### 5. Which future problem could arise if `WarrantyRepository` stores full `Sale` objects instead of just IDs?
**Answer:** 
Storing full `Sale` objects inside `WarrantyRepository` introduces severe cyclic dependency risks. Since `Sale` references its associated warranties and `Warranty` references its parent `Sale`, deserializing or persisting whole objects in both repositories creates a circular reference graph. This leads to construction-time cyclic dependencies between `SaleService` and `WarrantyService` (which is precisely addressed in Adjustment A2).
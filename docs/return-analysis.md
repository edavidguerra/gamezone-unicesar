# Requirements Analysis and Design: Return Module

This document records the analysis of the Return module in the GameZone Unicesar system. The first part answers the five official guiding questions; the second part keeps the additional design notes written by the team.

## Official guiding questions

### 1. What type of relationship exists between 'Return' and 'Sale'?
**Answer:**
It is an **association** (many-to-one): many `Return` objects can point to the same `Sale`, and each `Return` holds exactly one reference to its original sale (`originalSale`).

It is **not inheritance**, because a return is not a kind of sale. It has its own identity, date, reason and refund amount.
It is **not composition**, because a `Sale` does not own its returns and exists independently of them: the sale is registered first, and it keeps existing whether or not any return is ever created.
It is **not aggregation** either, because a return is not a "part" of a sale and `Sale` does not keep a collection of its returns. The dependency goes in one direction only (`Return` -> `Sale`).

The reference is declared `final` and has no setter, so a return can never be moved to a different sale after it is created.

### 2. How is a partial return represented in the attributes of 'Return'?
**Answer:**
The class stores the products in an attribute `List<Product> returnedProducts`. This list contains **only the products that the customer actually returns**, which may be a subset of `originalSale.getProducts()`. The list of the original sale is never modified.

The elements are references to the same `Product` objects that appear in the sale (video games, consoles and, after adjustment A4, accessories). If a product was bought twice and both units are returned, it appears twice in the list. The refund amount is calculated from this subset only, and when the return is persisted the list is saved as a comma-separated list of product ids.

<!-- QUESTIONS 3 TO 5 -->

## Additional design notes

### Note 1. Why does canBeReturned() live in 'Sale' instead of in ReturnService?
**Answer:**
The `canBeReturned()` method evaluates business rules using data that `Sale` already owns (specifically its sale date). According to information expert and high cohesion principles, the logic to check if a sale is within the eligible timeframe belongs directly on the `Sale` class itself rather than being delegated to an external service.

### Note 2. Why are there no setters for originalSale, returnedProducts or reason in 'Return'?
**Answer:**
A return record represents an immutable historical event once created. Allowing setters for `originalSale`, `returnedProducts`, or `reason` would introduce the risk of modifying audit history after the transaction took place, compromising data consistency.

### Note 3. Why is originalSale declared 'final' in Java?
**Answer:**
Declaring `originalSale` as `final` enforces immutability at the compiler level. It guarantees that the reference to the parent sale can only be assigned once during constructor execution and can never be reassigned to another sale entity later.

### Note 4. What is the risk of trusting a persisted refundAmount value read straight from disk instead of recomputing it with calculateRefundAmount() after loading?
**Answer:**
If the system relies solely on stale persisted amounts, any subsequent changes to refund calculation rules (such as applying proportional discounts in adjustment A5) would not reflect on existing records, leading to financial inconsistencies between actual sale terms and stored return totals.

### Note 5. Which two other services does ReturnService need to coordinate with to restore inventory correctly?
**Answer:**
`ReturnService` must coordinate with `ProductService` (to update inventory stock for video games and consoles) and `AccessoryService` (to restore stock for returned accessory products after adjustment A4).

### Note 6. Why does generateMonthlyBalance delegate to two private methods (calculateMonthlySales and calculateMonthlyReturns) instead of computing the net balance in a single combined loop?
**Answer:**
Separating the sales calculation from the returns calculation keeps each method focused on a single responsibility and easier to test in isolation. More importantly, it allows each figure to be exposed and displayed independently later—exactly what adjustment A6 requires when the console menu needs to show total sales, total returns, and the net balance as three separate figures instead of only the combined result.
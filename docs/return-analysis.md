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

### 3. In which layer is the 30-day rule validated, and how are the dates compared?
**Answer:**
The rule is a business rule, so it is enforced in the **service layer**: `ReturnService.registerReturn` calls `Sale.canBeReturned()` before creating anything and throws an `IllegalArgumentException` (message in Spanish) if the sale is outside the window. The check itself lives in `Sale` (model layer) because `Sale` owns the sale date, so it is the information expert; the service only decides what to do with the answer. It is not placed in the UI because the menu must only collect data and show messages (the same rule would have to be copied into every screen), and it is not placed in persistence because reading or writing files must never decide business rules.

Dates are compared with the `java.time` API: the sale date is parsed with `LocalDate.parse(...)` and the difference in days is calculated with `ChronoUnit.DAYS.between(saleDate, LocalDate.now())`. The sale can be returned when that number is between 0 and 30.

### 4. Which existing method restores the stock, and why is it reused?
**Answer:**
`ReturnService` calls `ProductService.restoreStock(String id, int quantity)`, which increases the stock of the product and saves the inventory through `ProductRepository`. It follows the same pattern as `ProductService.reduceStock`, the method used when a sale is registered. (After adjustment A4, accessories are restored through `AccessoryService.restoreStock` in the same way.)

Reusing it is important because `ProductService` is the single owner of inventory rules and of their persistence. If `ReturnService` changed `product.setStock(...)` directly, the stock could be updated in memory but not saved to `products.csv`, and any future change to how stock is managed would have to be repeated in two places, which leads to inconsistent inventory.

### 5. Where is the monthly balance report located, and what does it depend on?
**Answer:**
The report is `ReturnService.generateMonthlyBalance(int month, int year)`. It belongs to the service layer because it is business logic that combines data from two modules (sales and returns), and the UI (`ConsoleMenu`) only calls it and prints the result, which respects the flow ui -> service -> persistence -> model.

It is placed in `ReturnService` and not in `SaleService` because `ReturnService` already depends on `SaleService` to find sales. Putting the report in `SaleService` would make sales depend on returns, creating a circular dependency between the two services.

To generate the report, `ReturnService` needs: `SaleService` (to list the sales of the month and read the final total of each one), its own list of returns loaded through `ReturnRepository` (to add up the refunded amounts), and `ProductService` for the stock restoration that is part of the same return flow.

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
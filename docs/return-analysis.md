# Requirements Analysis and Design: Return Module

This document details the design decisions and requirements analysis for the Return module in the GameZone Unicesar system.

---

## Technical Questions & Answers

### 1. Why does `canBeReturned()` live in `Sale` instead of in `ReturnService`?
**Answer:** 
The `canBeReturned()` method evaluates business rules using data that `Sale` already owns (specifically its sale date). According to information expert and high cohesion principles, the logic to check if a sale is within the eligible timeframe belongs directly on the `Sale` class itself rather than being delegated to an external service.

---

### 2. Why are there no setters for `originalSale`, `returnedProducts` or `reason` in `Return`?
**Answer:** 
A return record represents an immutable historical event once created. Allowing setters for `originalSale`, `returnedProducts`, or `reason` would introduce the risk of modifying audit history after the transaction took place, compromising data consistency.

---

### 3. Why is `originalSale` declared `final` in Java?
**Answer:** 
Declaring `originalSale` as `final` enforces immutability at the compiler level. It guarantees that the reference to the parent sale can only be assigned once during constructor execution and can never be reassigned to another sale entity later.

---

### 4. What is the risk of trusting a persisted `refundAmount` value read straight from disk instead of recomputing it with `calculateRefundAmount()` after loading?
**Answer:** 
If the system relies solely on stale persisted amounts, any subsequent changes to refund calculation rules (such as applying proportional discounts in adjustment A5) would not reflect on existing records, leading to financial inconsistencies between actual sale terms and stored return totals.

---

### 5. Which two other services does `ReturnService` need to coordinate with to restore inventory correctly?
**Answer:** 
`ReturnService` must coordinate with `ProductService` (to update inventory stock for video games and consoles) and `AccessoryService` (to restore stock for returned accessory products after adjustment A4).

---

### 6. Why does `generateMonthlyBalance` delegate to two private methods (`calculateMonthlySales` and `calculateMonthlyReturns`) instead of computing the net balance in a single combined loop?
**Answer:**
Separating the sales calculation from the returns calculation keeps each
method focused on a single responsibility and easier to test in
isolation. More importantly, it allows each figure to be exposed and
displayed independently later — exactly what adjustment A6 requires
when the console menu needs to show total sales, total returns, and
the net balance as three separate figures instead of only the combined
result.
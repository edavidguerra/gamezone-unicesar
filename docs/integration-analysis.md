# Integration Analysis: Adjustments A1 to A7

This document records how the four modules of Workshop 2 (accessories, promotions, warranties and returns) were integrated into the GameZone Unicesar system. Each module was first developed on its own branch against the Workshop 1 base (products, people and sales). Once they met, some requirements overlapped or contradicted each other, and seven adjustments (A1 to A7) were needed. One more fix, not numbered in the assignment, was found while testing the integrated system and is recorded at the end.

## Integration map

The modules touch each other at a few precise points. Every dependency follows the layer order `ui -> service -> persistence -> model`.

| Module | Depends on | Used by | Integration point |
|---|---|---|---|
| Accessories | `Product` (it extends it) | Sales, Promotions, Returns | `AccessoryService` owns the inventory of accessories, in the same way `ProductService` owns the one of video games and consoles. |
| Promotions | `Sale`, `Product`, `Accessory` | Sales, Returns | `SaleService` asks `PromotionService.findBestPromotionFor(sale)` and stores the result in the `Sale`. |
| Warranties | `Sale`, `Console` | Sales, Returns | `SaleService` assigns warranties when it registers a sale; `ReturnService` cancels them when a console is returned. |
| Returns | `Sale`, all inventory services, `WarrantyService` | Menu | `ReturnService` reads the data saved in the `Sale` (discount, final total) and coordinates stock and warranty cancellation. |

## A1 - Category discount for accessories

**Problem:** The category promotion only accepted `VIDEOGAME` and `CONSOLE` as target category, so the store could not launch promotions on accessories.
**Cause:** The accessories module was integrated after the promotions module had been closed with only two valid categories.
**Solution:** `CategoryDiscount` now admits `ACCESSORY` and recognizes every `Accessory` instance as part of that category when it calculates the discount. `PromotionService.registerCategoryDiscount` accepts the three categories and rejects any other. The menu passes the typed category to the service, so `ACCESSORY` can be used when registering a category promotion, and `data/promotions.csv` includes the sample promotion `P4` (20% on accessories).
**Verification:** Register a sale with only accessories while `P4` is active: the receipt shows the promotion `Descuento en accesorios`. Trying to register a category promotion with an unknown category is rejected by the service.

## A2 - Circular dependency between sales and warranties

**Problem:** The application could not be wired at startup: `SaleService` needed a `WarrantyService` to assign warranties, and `WarrantyService` needed a `SaleService` to resolve the sales of its saved warranties.
**Cause:** `WarrantyRepository` resolved `Sale` and `Product` references by itself, which forced both services to exist before the other one.
**Solution:** `WarrantyRepository` only reads and writes raw ids (`WarrantyRecord`), and `WarrantyService` resolves them. In `Main`, `SaleService` is built first without a `WarrantyService`; then `WarrantyService` is built with the existing `SaleService`, and finally it is injected back with `saleService.setWarrantyService(...)`.
**Verification:** The application starts, and warranties saved in `data/warranties.csv` are loaded again with the right sale and product after a restart.

## A3 - Unified sale registration flow

**Problem:** Requirements 1, 2 and 4 each changed `SaleService.registerSale` on their own, so the result depended on the order of the operations (for example, whether the discount was applied before or after the warranty cost).
**Cause:** The promotion, warranty and inventory logic were added separately, without a single defined order.
**Solution:** `registerSale` was reorganized to run always in the same order: (1) validate that the sale has at least one item; (2) resolve each item as product or accessory and validate its stock; (3) create the sale and calculate the subtotal; (4) look for the best promotion and register the discount, calculated only on the subtotal of the items; (5) generate the basic warranty of each console and the requested extended warranties, adding their cost; (6) calculate the final total as `subtotal - discount + extended warranty cost`; (7) update the inventory through `ProductService` or `AccessoryService` according to the item type; (8) persist the sale and the warranties. `Sale.generateReceipt` shows the subtotal, the promotion, the warranty surcharge and the total to pay.
**Verification:** A sale of a console with extended warranty and an accessory shows a discount that does not include the warranty cost, and a final total equal to subtotal minus discount plus warranty cost.

## A4 - Stock of accessories in returns

**Problem:** Returning an accessory did not give back its stock, and a return that contained an accessory could not be loaded again after restarting the program.
**Cause:** `ReturnService` only restored stock through `ProductService`, which does not know the accessories, and `ReturnRepository` only looked for ids among products.
**Solution:** `ReturnService` now restores each item through the service that owns its inventory (`AccessoryService.restoreStock` for accessories and `ProductService.restoreStock` for the rest). `ReturnRepository` resolves an id as product first and as accessory if it is not found. A product cannot be returned more times than it appears in the sale, counting the units already returned in previous returns.
**Verification:** After returning accessory `A1` of a sale, its stock goes back to the value before the sale. A second return of the same accessory from the same sale is rejected with a message in Spanish.

## A5 - Return of sales with a discount

**Problem:** The refund was calculated with the list prices, so when the sale had a promotion the customer received more than they had paid.
**Cause:** `Return.calculateRefundAmount` ignored the discount saved in the original sale.
**Solution:** Each returned item is refunded as `price * (1 - discount / subtotal)`, using `Sale.calculateTotal()` as subtotal and `Sale.getDiscountAmount()` as discount. The discount is proportional, so returning only part of the sale returns only that part of the discount. The return receipt shows, for each item, the list price, the proportional discount and the refunded amount.
**Verification:** In a sale with subtotal 2,380,000 and a 10% promotion (discount 238,000), returning the console (2,000,000) refunds 1,800,000 and returning the accessory (180,000) refunds 162,000.

## A6 - Monthly balance report

**Problem:** The menu only showed the net balance of the month, so it was not possible to see how much was sold and how much was returned.
**Cause:** The totals of sales and returns were private calculations inside `ReturnService`.
**Solution:** `calculateMonthlySales` and `calculateMonthlyReturns` are now public methods that validate the period (month between 1 and 12 and a positive year). Sales are added up with `Sale.getFinalTotal()`, which already includes discounts and warranty cost, and returns with the refunded amount. `generateMonthlyBalance` returns the difference between both. The menu shows the total of sales, the total of returns and the net balance.
**Verification:** After registering a sale and a return in the current month, the menu shows the total of sales, the total of returns and a net balance equal to their difference. A month outside 1-12 is rejected.

## A7 - Cancelation of warranties on returned consoles

**Problem:** After returning a console, its warranties stayed active, and the cost paid for the extended warranty was never refunded.
**Cause:** `ReturnService.registerReturn` did not communicate with `WarrantyService`.
**Solution:** `WarrantyService.cancelWarranties(productId, saleId)` removes every warranty of that console in that sale and returns the refundable cost: zero for the basic warranty and the additional cost for the extended one. `ReturnService` calls it for each returned console and adds the result to the return with `Return.addWarrantyRefund`. The warranty refund is saved as the seventh column of `data/returns.csv` and shown in the receipt; lines written before this change are read as zero. Warranty ids are now generated from the highest existing number, because the count of warranties produced duplicated ids after cancellations.
**Verification:** After returning a console sold with extended warranty, the warranty no longer appears in the warranties menu, the receipt lists the warranty refund, and the same value is read again after restarting.

## Additional fix - Persistence of sales

**Problem:** After restarting the program, the sales lost the promotion name, the discount, the extended warranty cost and the final total, and the accessories of each sale disappeared. A sale with only accessories could even stop the program from starting, because a sale must contain at least one item. Since the returns and the monthly balance read these values, their results were wrong after a restart.
**Cause:** `SaleRepository` saved only the id, date, client, seller and product ids, and `SaleService` resolved the ids only against the products.
**Solution:** `SaleRepository` saves four more columns (promotion name, discount, warranty cost and final total) and still reads the old five-column format, using the subtotal as final total. `SaleService` resolves the saved ids against the products and the accessories together.
**Verification:** A sale with a console, an accessory, a discount and an extended warranty, and another sale with only an accessory, show the same discount, warranty cost and total before and after restarting the program.

## Persistence formats after the integration

| File | Columns |
|---|---|
| `data/sales.csv` | `id`, `date`, `clientId`, `sellerId`, `itemIds`, `promotionName`, `discount`, `warrantyCost`, `finalTotal` |
| `data/returns.csv` | `id`, `date`, `saleId`, `itemIds`, `reason`, `refundAmount`, `warrantyRefund` |
| `data/warranties.csv` | `type`, `id`, `productId`, `saleId`, `startDate` |
| `data/promotions.csv` | `type`, `id`, `name`, `startDate`, `endDate`, then the fields of each type |

The refund amount saved in `returns.csv` is informative only: when the file is loaded, `ReturnRepository` recalculates it with the current rule and adds the saved warranty refund. `data/returns.csv` and `data/warranties.csv` are generated while the program runs, so they are listed in `.gitignore`.
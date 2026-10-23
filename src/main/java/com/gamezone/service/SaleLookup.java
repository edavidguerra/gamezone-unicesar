package com.gamezone.service;

import com.gamezone.model.Sale;

import java.util.List;

/**
 * Read-only view of the registered sales. Classes that only need to find
 * or list sales (for example the warranty and return services) depend on
 * this interface instead of the whole SaleService.
 */
public interface SaleLookup {

    /**
     * Finds a sale by its identifier.
     *
     * @param id identifier of the sale
     * @return the sale, or {@code null} if no sale has that identifier
     */
    Sale findById(String id);

    /**
     * Lists every registered sale.
     *
     * @return the registered sales
     */
    List<Sale> listSales();
}

package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * Read-only view of the registered video games and consoles. Classes that
 * only need to find a product (for example the warranty service) depend
 * on this interface instead of the whole ProductService.
 */
public interface ProductLookup {

    /**
     * Finds a product by its identifier.
     *
     * @param id identifier of the product
     * @return the product, or {@code null} if no product has that identifier
     */
    Product findById(String id);
}
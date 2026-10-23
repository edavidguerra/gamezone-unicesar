package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

import java.util.List;

/**
 * Storage of returns. Implementations decide where and how they are kept.
 */
public interface ReturnRepository {

    /**
     * Saves the full list of returns, replacing what was stored.
     *
     * @param returns returns to persist
     */
    void saveAll(List<Return> returns);

    /**
     * Loads all stored returns, resolving the original sale and the returned items
     * from the given lists.
     *
     * @param sales sales used to resolve the original sale of each return
     * @param catalog products and accessories used to resolve the returned items
     * @return the stored returns, or an empty list if there are none
     */
    List<Return> loadAll(List<Sale> sales, List<Product> catalog);
}
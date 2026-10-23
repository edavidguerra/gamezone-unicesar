package com.gamezone.persistence;

import com.gamezone.model.Product;

import java.util.List;

/**
 * Storage of video games and consoles. ProductService depends on this
 * interface instead of a concrete file class, so the way products are
 * stored can change without touching the catalog rules.
 */
public interface ProductRepository {

    /**
     * Saves the full list of products, replacing what was stored before.
     *
     * @param products list of products to persist
     */
    void saveAll(List<Product> products);

    /**
     * Loads every stored product.
     *
     * @return list of products, empty if nothing has been stored yet
     */
    List<Product> loadAll();
}
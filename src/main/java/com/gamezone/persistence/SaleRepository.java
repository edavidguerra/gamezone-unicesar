package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Storage of sales. SaleService depends on this interface instead of a
 * concrete file class, so the way sales are stored can change without
 * touching the business rules.
 */
public interface SaleRepository {

    /**
     * Saves the full list of sales, replacing what was stored before.
     *
     * @param sales list of sales to persist
     */
    void saveAll(List<Sale> sales);

    /**
     * Loads every stored sale, resolving its client, seller and products
     * from the lists already loaded by the other services.
     *
     * @param clients  clients already loaded by PersonService
     * @param sellers  sellers already loaded by PersonService
     * @param products products and accessories already loaded
     * @return list of reconstructed sales
     */
    List<Sale> loadAll(List<Client> clients, List<Seller> sellers, List<Product> products);
}

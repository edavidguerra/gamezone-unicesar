package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Storage of clients and sellers. Implementations decide where and how they are kept.
 */
public interface PersonRepository {

    /**
     * Saves the full list of clients, replacing what was stored.
     *
     * @param clients list of clients to persist
     */
    void saveClients(List<Client> clients);

    /**
     * Loads all stored clients.
     *
     * @return the stored clients, or an empty list if there are none
     */
    List<Client> loadClients();

    /**
     * Saves the full list of sellers, replacing what was stored.
     *
     * @param sellers list of sellers to persist
     */
    void saveSellers(List<Seller> sellers);

    /**
     * Loads all stored sellers.
     *
     * @return the stored sellers, or an empty list if there are none
     */
    List<Seller> loadSellers();
}
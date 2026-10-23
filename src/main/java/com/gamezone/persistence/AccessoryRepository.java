package com.gamezone.persistence;

import com.gamezone.model.Accessory;

import java.util.List;

/**
 * Storage of accessories. Implementations decide where and how they are kept.
 */
public interface AccessoryRepository {

    /**
     * Saves the full list of accessories, replacing what was stored.
     *
     * @param accessories list of accessories to persist
     */
    void saveAll(List<Accessory> accessories);

    /**
     * Loads all stored accessories.
     *
     * @return the stored accessories, or an empty list if there are none
     */
    List<Accessory> loadAll();
}
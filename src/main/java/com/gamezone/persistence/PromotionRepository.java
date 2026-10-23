package com.gamezone.persistence;

import com.gamezone.model.Promotion;

import java.util.List;

/**
 * Storage of promotions. Implementations decide where and how they are kept.
 */
public interface PromotionRepository {

    /**
     * Saves the full list of promotions, replacing what was stored.
     *
     * @param promotions list of promotions to persist
     */
    void saveAll(List<Promotion> promotions);

    /**
     * Loads all stored promotions.
     *
     * @return the stored promotions, or an empty list if there are none
     */
    List<Promotion> loadAll();
}
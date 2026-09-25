package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates business rules for promotions: registration and finding
 * the best applicable promotion for a given sale.
 */
public class PromotionService {

    private final PromotionRepository repository;
    private List<Promotion> promotions;

    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
        this.promotions = repository.loadAll();
    }

    public PercentageDiscount registerPercentageDiscount(String id, String name,
            LocalDate startDate, LocalDate endDate, double percentage) {
        PercentageDiscount promo = new PercentageDiscount(id, name, startDate, endDate, percentage);
        promotions.add(promo);
        repository.saveAll(promotions);
        return promo;
    }

    public CategoryDiscount registerCategoryDiscount(String id, String name,
            LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) {
        if (!targetCategory.equals("VIDEOGAME") && !targetCategory.equals("CONSOLE")) {
            throw new IllegalArgumentException("Categoria de descuento no valida: " + targetCategory);
        }
        CategoryDiscount promo = new CategoryDiscount(id, name, startDate, endDate,
                percentage, targetCategory);
        promotions.add(promo);
        repository.saveAll(promotions);
        return promo;
    }

    public BulkPurchaseDiscount registerBulkPurchaseDiscount(String id, String name,
            LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) {
        BulkPurchaseDiscount promo = new BulkPurchaseDiscount(id, name, startDate, endDate,
                minimumQuantity, percentage);
        promotions.add(promo);
        repository.saveAll(promotions);
        return promo;
    }

    public List<Promotion> listAllPromotions() {
        return promotions;
    }

    public List<Promotion> listActivePromotions() {
        List<Promotion> active = new ArrayList<>();
        for (Promotion promotion : promotions) {
            if (promotion.isActive(LocalDate.now())) {
                active.add(promotion);
            }
        }
        return active;
    }

    /**
     * Evaluates every active promotion against the sale and returns the
     * one that grants the highest discount. Returns null if no active
     * promotion grants any discount at all.
     */
    public Promotion findBestPromotionFor(Sale sale) {
        Promotion best = null;
        double bestDiscount = 0.0;
        for (Promotion promotion : listActivePromotions()) {
            double discount = promotion.calculateDiscount(sale);
            if (discount > bestDiscount) {
                bestDiscount = discount;
                best = promotion;
            }
        }
        return best;
    }

    public Promotion findById(String id) {
        for (Promotion promotion : promotions) {
            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }
        return null;
    }
}
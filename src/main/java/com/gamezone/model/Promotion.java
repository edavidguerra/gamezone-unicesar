package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a generic promotion that can be applied to a sale. A
 * promotion is valid only within a date range and defines its own way of
 * calculating the discount amount for a given sale.
 */

public abstract class Promotion {
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /**
     * Checks whether this promotion is active on the given date (inclusive
     * on both ends of the range).
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the discount amount this promotion grants for the given
     * sale. Each subclass implements its own discount rule.
     */
    public abstract double calculateDiscount(Sale sale);
}
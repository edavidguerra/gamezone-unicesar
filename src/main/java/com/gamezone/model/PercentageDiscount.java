package com.gamezone.model;

/**
 * Applies a fixed percentage discount over the whole sale total.
 */

public class PercentageDiscount extends Promotion {
    private double percentage;

    public PercentageDiscount(String id, String name, java.time.LocalDate startDate,
                              java.time.LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    @Override
    public double calculateDiscount(Sale sale) {
        return sale.calculateTotal() * (percentage / 100.0);
    }
}
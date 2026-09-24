package com.gamezone.model;

/**
 * Applies a percentage discount only when the sale contains at least a
 * minimum quantity of products.
 */

public class BulkPurchaseDiscount extends Promotion {
    private int minimumQuantity;
    private double percentage;

    public BulkPurchaseDiscount(String id, String name, java.time.LocalDate startDate,
                                java.time.LocalDate endDate, int minimumQuantity,
                                double percentage) {
        super(id, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    public int getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(int minimumQuantity) { this.minimumQuantity = minimumQuantity; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    @Override
    public double calculateDiscount(Sale sale) {
        if (sale.getProducts().size() >= minimumQuantity) {
            return sale.calculateTotal() * (percentage / 100.0);
        }
        return 0.0;
    }
}
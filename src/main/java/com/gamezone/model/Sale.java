package com.gamezone.model;

import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a sale made in the store. A sale is associated with one
 * client, one seller and a list of one or more products sold.
 */

public class Sale {

    private String id;
    private String date;
    private Client client;
    private Seller seller;
    private List<Product> products;
    private String appliedPromotionName;
    private double discountAmount;
    private double extendedWarrantyCost;
    private double finalTotal;

    /**
     * Creates a new sale.
     *
     * @param id       unique identifier of the sale
     * @param date     date of the sale
     * @param client   client who made the purchase
     * @param seller   seller who attended the sale
     * @param products list of products sold, must contain at least one item
     */
    public Sale(String id, String date, Client client, Seller seller, List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }
        this.id = id;
        this.date = date;
        this.client = client;
        this.seller = seller;
        this.products = products;
        this.discountAmount = 0.0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public String getAppliedPromotionName() { return appliedPromotionName; }
    public void setAppliedPromotionName(String appliedPromotionName) { this.appliedPromotionName = appliedPromotionName; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getExtendedWarrantyCost() { return extendedWarrantyCost; }
    public void setExtendedWarrantyCost(double extendedWarrantyCost) { this.extendedWarrantyCost = extendedWarrantyCost; }

    public double getFinalTotal() { return finalTotal; }
    public void setFinalTotal(double finalTotal) { this.finalTotal = finalTotal; }

    /**
     * Calculates the total of the sale by summing the price of every
     * product sold.
     *
     * @return the total amount of the sale
     */
    public double calculateTotal() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total;
    }

    /**
     * Builds a human-readable receipt in Spanish showing the subtotal, the
     * applied promotion (if any), the extended warranty surcharge (if any)
     * and the final total to pay.
     */
    public String generateReceipt() {
        double subtotal = calculateTotal();
        StringBuilder sb = new StringBuilder();
        sb.append("Recibo de venta ").append(getId()).append("\n");
        sb.append("Subtotal: ").append(subtotal).append("\n");
        if (discountAmount > 0) {
            sb.append("Promocion aplicada: ").append(appliedPromotionName)
              .append(" (-").append(discountAmount).append(")\n");
        }
        if (extendedWarrantyCost > 0) {
            sb.append("Garantia extendida: +").append(extendedWarrantyCost).append("\n");
        }
        sb.append("Total a pagar: ").append(finalTotal);
        return sb.toString();
    }

    /**
     * Checks whether this sale is still within the 30-day return window,
     * counted from its sale date until today.
     */
    
    public boolean canBeReturned() {
        LocalDate saleDate = LocalDate.parse(getDate());
        long daysSinceSale = ChronoUnit.DAYS.between(saleDate, LocalDate.now());
        return daysSinceSale >= 0 && daysSinceSale <= 30;
    }
}
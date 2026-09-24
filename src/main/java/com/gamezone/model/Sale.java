package com.gamezone.model;

import java.util.List;

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
     * applied promotion (if any) and the final total after discount.
     */
    
    public String generateReceipt() {
        double subtotal = calculateTotal();
        StringBuilder sb = new StringBuilder();
        sb.append("Recibo de venta ").append(getId()).append("\n");
        sb.append("Subtotal: ").append(subtotal).append("\n");
        if (discountAmount > 0) {
            sb.append("Promocion aplicada: ").append(appliedPromotionName)
              .append(" (").append(discountAmount).append(")\n");
        }
        double total = subtotal - discountAmount;
        sb.append("Total a pagar: ").append(total);
        return sb.toString();
    }
}
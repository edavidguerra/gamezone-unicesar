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
}
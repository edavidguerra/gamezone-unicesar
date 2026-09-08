package com.gamezone.model;

/**
 * Represents a generic product sold by GameZone Unicesar. This class is
 * abstract because a product must always be either a video game or a
 * console; a plain, unspecialized product does not exist in this domain.
 */

public abstract class Product {
private String id;
private String title;
private double price;
private int stock;

public Product(String id, String title, double price, int stock) {
this.id = id;
this.title = title;
this.price = price;
this.stock = stock;
}

/**
     * Creates a new product with its common attributes.
     *
     * @param id unique identifier of the product
     * @param title title of the product
     * @param price unit price of the product
     * @param stock quantity currently available in inventory
     */

public String getId() { return id; }
public void setId(String id) { this.id = id; }

public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }

public double getPrice() { return price; }
public void setPrice(double price) { this.price = price; }

public int getStock() { return stock; }
public void setStock(int stock) { this.stock = stock; }

/**
     * Checks whether there is enough stock to sell the given quantity.
     *
     * @param quantity quantity requested
     * @return true if there is enough stock, false otherwise
     */

public boolean hasStock(int quantity) {
return this.stock >= quantity;
}

/**
     * Reduces the stock by the given quantity after a sale.
     *
     * @param quantity quantity sold
     */

public void reduceStock(int quantity) {
this.stock = this.stock - quantity;
}

/**
     * Builds a complete description of the product, combining its common
     * attributes with the ones specific to each subclass. Every subclass
     * must provide its own implementation (polymorphism).
     *
     * @return a text description of the product
     */

public abstract String getDescription();
}
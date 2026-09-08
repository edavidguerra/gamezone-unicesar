package com.gamezone.model;

/**
 * Represents a console product, identified by its brand, model and
 * generation.
 */

public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Creates a new console.
     *
     * @param id unique identifier of the product
     * @param title title of the console
     * @param price unit price
     * @param stock quantity available in inventory
     * @param brand manufacturer brand
     * @param model model name
     * @param generation hardware generation
     */

    public Console(String id, String title, double price, int stock,
                    String brand, String model, String generation) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getGeneration() { return generation; }
    public void setGeneration(String generation) { this.generation = generation; }

    @Override
    public String getDescription() {
        return getTitle() + " [Console] - Brand: " + brand + ", Model: " + model + ", Generation: " + generation + ", Price: " + getPrice() + ", Stock: " + getStock();
    }
}
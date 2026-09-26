package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a warranty associated with a product sold in a specific
 * sale. The end date is computed automatically from the start date plus
 * the duration each subclass defines. This class is abstract because a
 * warranty is always either basic or extended.
 */

public abstract class Warranty {
    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public abstract int getDurationInMonths();
    public abstract String getWarrantyType();
    public abstract double getAdditionalCost();

    /**
     * Builds a human-readable warranty certificate in Spanish.
     */
    public String generateWarrantyCertificate() {
        return "Certificado de garantia " + id + "\n"
             + "Tipo: " + getWarrantyType() + "\n"
             + "Producto: " + product.getTitle() + "\n"
             + "Vigencia: " + startDate + " a " + endDate;
    }
}
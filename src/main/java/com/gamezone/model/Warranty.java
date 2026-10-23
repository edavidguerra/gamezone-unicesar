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

    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * Computes the end date from the start date and the duration of the
     * warranty type, so it always matches the current start date.
     *
     * @return the last day the warranty is valid
     */
    public LocalDate getEndDate() {
        return startDate.plusMonths(getDurationInMonths());
    }

    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(getEndDate());
    }

    public abstract int getDurationInMonths();
    public abstract String getWarrantyType();

    /**
     * Returns the technical code of the warranty type, used to store it.
     * Unlike getWarrantyType, it is not shown to the user.
     *
     * @return "BASIC" or "EXTENDED"
     */
    public abstract String getTypeCode();

    public abstract double getAdditionalCost();

    /**
     * Builds a human-readable warranty certificate in Spanish.
     */
    public String generateWarrantyCertificate() {
        return "Certificado de garantia " + id + "\n"
             + "Tipo: " + getWarrantyType() + "\n"
             + "Producto: " + product.getTitle() + "\n"
             + "Vigencia: " + startDate + " a " + getEndDate();
    }
}
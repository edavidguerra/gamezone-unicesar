package com.gamezone.model;

import java.time.LocalDate;

/**
 * Extended warranty (12 months) that carries a 10% surcharge on the product base price.
 */

public class ExtendedWarranty extends Warranty {

    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 12;
    }

    @Override
    public String getWarrantyType() {
        return "Extendida";
    }

    @Override
    public double getAdditionalCost() {
        if (getProduct() == null) {
            return 0.0;
        }
        return getProduct().getPrice() * 0.10;
    }
}
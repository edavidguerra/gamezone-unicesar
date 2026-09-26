package com.gamezone.model;

import java.time.LocalDate;

/**
 * Basic warranty included with products (typically 3 months) at no extra cost.
 */
public class BasicWarranty extends Warranty {

    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 3;
    }

    @Override
    public String getWarrantyType() {
        return "Basica";
    }

    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
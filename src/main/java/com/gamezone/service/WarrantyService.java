package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates warranty assignment and queries. A basic warranty is
 * assigned automatically to every console sold; an extended warranty is
 * assigned only when the customer requests and pays for it.
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private List<Warranty> warranties;

    public WarrantyService(WarrantyRepository repository) {
        this.repository = repository;
        this.warranties = repository.loadAll();
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = new BasicWarranty(generateId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = new ExtendedWarranty(generateId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            if (warranty.getProduct().getId().equals(productId)
                    && warranty.getSale().getId().equals(saleId)) {
                return warranty;
            }
        }
        return null;
    }

    public List<Warranty> listAllWarranties() {
        return warranties;
    }

    public List<Warranty> listActiveWarranties() {
        List<Warranty> active = new ArrayList<>();
        for (Warranty warranty : warranties) {
            if (warranty.isActive(LocalDate.now())) {
                active.add(warranty);
            }
        }
        return active;
    }

    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        List<Warranty> expiring = new ArrayList<>();
        LocalDate limit = LocalDate.now().plusDays(daysAhead);
        for (Warranty warranty : warranties) {
            if (!warranty.getEndDate().isBefore(LocalDate.now())
                    && !warranty.getEndDate().isAfter(limit)) {
                expiring.add(warranty);
            }
        }
        return expiring;
    }

    private String generateId() {
        return "W" + (warranties.size() + 1);
    }
}
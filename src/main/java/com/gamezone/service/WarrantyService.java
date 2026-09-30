package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.persistence.WarrantyRepository.WarrantyRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates warranty assignment and queries. Now receives
 * WarrantyRepository, SaleService and ProductService directly, and is
 * responsible for resolving the raw records loaded from disk into real
 * Warranty objects, instead of delegating that to the repository.
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final SaleService saleService;
    private final ProductService productService;
    private List<Warranty> warranties;

    public WarrantyService(WarrantyRepository repository, SaleService saleService,
            ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.warranties = resolveRecords(repository.loadRawRecords());
    }

    private List<Warranty> resolveRecords(List<WarrantyRecord> records) {
        List<Warranty> resolved = new ArrayList<>();
        for (WarrantyRecord record : records) {
            Product product = productService.findById(record.productId);
            Sale sale = saleService.findById(record.saleId);
            LocalDate startDate = LocalDate.parse(record.startDate);

            Warranty warranty = record.type.equals("BASIC")
                    ? new BasicWarranty(record.id, product, sale, startDate)
                    : new ExtendedWarranty(record.id, product, sale, startDate);
            resolved.add(warranty);
        }
        return resolved;
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

    /**
     * Cancels every warranty (basic and extended) of a product in a given
     * sale, for example because the product was returned, and persists the
     * change.
     *
     * @param productId identifier of the returned product
     * @param saleId    identifier of the sale the product belongs to
     * @return refundable cost: zero for a basic warranty and the additional
     *         cost for an extended warranty
     */
    public double cancelWarranties(String productId, String saleId) {
        double refundable = 0.0;
        List<Warranty> toRemove = new ArrayList<>();
        for (Warranty warranty : warranties) {
            if (warranty.getProduct().getId().equals(productId)
                    && warranty.getSale().getId().equals(saleId)) {
                refundable += warranty.getAdditionalCost();
                toRemove.add(warranty);
            }
        }
        if (!toRemove.isEmpty()) {
            warranties.removeAll(toRemove);
            repository.saveAll(warranties);
        }
        return refundable;
    }

    /**
     * Generates the next warranty id from the highest existing number, so
     * ids stay unique even after warranties have been cancelled.
     */
    private String generateId() {
        int max = 0;
        for (Warranty warranty : warranties) {
            try {
                max = Math.max(max, Integer.parseInt(warranty.getId().substring(1)));
            } catch (NumberFormatException e) {
                // Ignore ids that do not follow the W<number> pattern.
            }
        }
        return "W" + (max + 1);
    }
}
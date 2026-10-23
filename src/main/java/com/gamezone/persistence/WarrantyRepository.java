package com.gamezone.persistence;

import com.gamezone.model.Warranty;

import java.util.List;

/**
 * Storage of warranties. Implementations decide where and how they are kept.
 */
public interface WarrantyRepository {

    /**
     * Simple record-like holder for a raw, unresolved warranty line.
     */
    public static class WarrantyRecord {
        private final String type;
        private final String id;
        private final String productId;
        private final String saleId;
        private final String startDate;

        public WarrantyRecord(String type, String id, String productId, String saleId, String startDate) {
            this.type = type;
            this.id = id;
            this.productId = productId;
            this.saleId = saleId;
            this.startDate = startDate;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getProductId() {
            return productId;
        }

        public String getSaleId() {
            return saleId;
        }

        public String getStartDate() {
            return startDate;
        }
    }

    /**
     * Saves the full list of warranties, replacing what was stored.
     *
     * @param warranties warranties to persist
     */
    void saveAll(List<Warranty> warranties);

    /**
     * Loads the stored warranties as raw, unresolved records.
     *
     * @return the stored records, or an empty list if there are none
     */
    List<WarrantyRecord> loadRawRecords();
}
package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Warranty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Warranty records as plain ids (no longer resolving
 * Sale or Product references itself). This removes the circular
 * dependency with SaleService; reference resolution now happens in
 * WarrantyService.
 */
public class WarrantyRepository {

    private static final String FILE_PATH = "data/warranties.csv";

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

    public void saveAll(List<Warranty> warranties) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Warranty warranty : warranties) {
                String type = (warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED";
                writer.println(type + "|" + warranty.getId() + "|"
                        + warranty.getProduct().getId() + "|" + warranty.getSale().getId()
                        + "|" + warranty.getStartDate());
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudo escribir el archivo " + FILE_PATH
                    + " (operacion: guardado de garantias): " + e.getMessage(), e);
        }
    }

    public List<WarrantyRecord> loadRawRecords() {
        List<WarrantyRecord> records = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return records;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", -1);
                records.add(new WarrantyRecord(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudo leer el archivo " + FILE_PATH
                    + " (operacion: carga de garantias): " + e.getMessage(), e);
        }
        return records;
    }
}
package com.gamezone.persistence;

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
        public String type, id, productId, saleId, startDate;
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
            System.out.println("Error al guardar garantias: " + e.getMessage());
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
                WarrantyRecord record = new WarrantyRecord();
                record.type = parts[0];
                record.id = parts[1];
                record.productId = parts[2];
                record.saleId = parts[3];
                record.startDate = parts[4];
                records.add(record);
            }
        } catch (IOException e) {
            System.out.println("Error al leer garantias: " + e.getMessage());
        }
        return records;
    }
}
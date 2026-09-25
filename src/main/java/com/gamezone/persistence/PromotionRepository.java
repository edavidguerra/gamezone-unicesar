package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Promotion objects (percentage, category, bulk) to a
 * pipe-delimited text file, using a leading type token per line.
 */
public class PromotionRepository {

    private static final String FILE_PATH = "data/promotions.csv";

    public void saveAll(List<Promotion> promotions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Promotion promotion : promotions) {
                writer.println(toLine(promotion));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar promociones: " + e.getMessage());
        }
    }

    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return promotions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                promotions.add(fromLine(line));
            }
        } catch (IOException e) {
            System.out.println("Error al leer promociones: " + e.getMessage());
        }
        return promotions;
    }

    private String toLine(Promotion promotion) {
        String base = promotion.getId() + "|" + promotion.getName() + "|"
                + promotion.getStartDate() + "|" + promotion.getEndDate();
        if (promotion instanceof PercentageDiscount p) {
            return "PERCENTAGE|" + base + "|" + p.getPercentage();
        }
        if (promotion instanceof CategoryDiscount c) {
            return "CATEGORY|" + base + "|" + c.getPercentage() + "|" + c.getTargetCategory();
        }
        BulkPurchaseDiscount b = (BulkPurchaseDiscount) promotion;
        return "BULK|" + base + "|" + b.getMinimumQuantity() + "|" + b.getPercentage();
    }

    private Promotion fromLine(String line) {
        String[] parts = line.split("\\|", -1);
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        return switch (type) {
            case "PERCENTAGE" -> new PercentageDiscount(id, name, startDate, endDate,
                    Double.parseDouble(parts[5]));
            case "CATEGORY" -> new CategoryDiscount(id, name, startDate, endDate,
                    Double.parseDouble(parts[5]), parts[6]);
            case "BULK" -> new BulkPurchaseDiscount(id, name, startDate, endDate,
                    Integer.parseInt(parts[5]), Double.parseDouble(parts[6]));
            default -> throw new IllegalArgumentException("Unknown promotion type: " + type);
        };
    }
}
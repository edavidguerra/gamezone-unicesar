package com.gamezone.persistence;

import com.gamezone.model.*;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Warranty objects to a pipe-delimited text file.
 * NOTE: this initial version resolves the associated Sale through
 * SaleService, which creates a circular dependency between SaleService
 * and WarrantyRepository. This is corrected in ajuste A2.
 */
public class WarrantyRepository {

    private static final String FILE_PATH = "data/warranties.csv";

    private final SaleService saleService;
    private final ProductService productService;

    public WarrantyRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
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

    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return warranties;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", -1);
                String type = parts[0];
                String id = parts[1];
                Product product = productService.findById(parts[2]);
                Sale sale = saleService.findById(parts[3]);
                LocalDate startDate = LocalDate.parse(parts[4]);

                Warranty warranty = type.equals("BASIC")
                        ? new BasicWarranty(id, product, sale, startDate)
                        : new ExtendedWarranty(id, product, sale, startDate);
                warranties.add(warranty);
            }
        } catch (IOException e) {
            System.out.println("Error al leer garantias: " + e.getMessage());
        }
        return warranties;
    }
}
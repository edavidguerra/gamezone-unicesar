package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Return objects to a pipe-delimited text file,
 * resolving the original sale and returned products through the
 * services already loaded in memory.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates the repository with the services needed to resolve
     * references while loading.
     *
     * @param saleService used to resolve the original sale
     * @param productService used to resolve video games and consoles
     * @param accessoryService used to resolve accessories
     */
    public ReturnRepository(SaleService saleService, ProductService productService,
            AccessoryService accessoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Saves the full list of returns to data/returns.csv, overwriting the file.
     *
     * @param returns returns to persist
     */
    public void saveAll(List<Return> returns) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Return returnItem : returns) {
                StringBuilder productIds = new StringBuilder();
                for (Product product : returnItem.getReturnedProducts()) {
                    if (productIds.length() > 0) productIds.append(",");
                    productIds.append(product.getId());
                }
                writer.println(returnItem.getId() + "|" + returnItem.getDate() + "|"
                        + returnItem.getOriginalSale().getId() + "|" + productIds + "|"
                        + returnItem.getReason() + "|" + returnItem.getRefundAmount() + "|"
                        + returnItem.getWarrantyRefund());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar devoluciones: " + e.getMessage());
        }
    }

    /**
     * Loads all returns from data/returns.csv, resolving the original sale and
     * the returned products. Returns an empty list if the file does not exist.
     *
     * @return the returns read from disk
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return returns;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", -1);
                String id = parts[0];
                LocalDate date = LocalDate.parse(parts[1]);
                Sale sale = saleService.findById(parts[2]);

                List<Product> products = new ArrayList<>();
                for (String productId : parts[3].split(",")) {
                    if (!productId.isBlank()) {
                        products.add(findItem(productId));
                    }
                }
                String reason = parts[4];

                 // The refund amount is NOT trusted from the file: it is recalculated
                // with the current rule so that fixing the rule later (adjustment A5)
                // also updates the returns already saved on disk.
                Return returnItem = new Return(id, date, sale, products, reason);
                // Older lines have no warranty column; treat them as 0.
                double warrantyRefund = parts.length > 6 ? Double.parseDouble(parts[6]) : 0.0;
                returnItem.addWarrantyRefund(warrantyRefund);
                returns.add(returnItem);
            }
        } catch (IOException e) {
            System.out.println("Error al leer devoluciones: " + e.getMessage());
        }
        return returns;
    }

    /**
     * Resolves an item id as a product first and, if it is not found there,
     * as an accessory.
     */
    private Product findItem(String itemId) {
        Product product = productService.findById(itemId);
        if (product == null) {
            product = accessoryService.findById(itemId);
        }
        return product;
    }
}
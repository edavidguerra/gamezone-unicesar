package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Return objects to a pipe-delimited text file,
 * resolving the original sale and returned products from the lists
 * received when loading.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

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
            throw new PersistenceException("No se pudo escribir el archivo " + FILE_PATH
                    + " (operacion: guardado de devoluciones): " + e.getMessage(), e);
        }
    }

    /**
     * Loads all returns from data/returns.csv, resolving the original sale and
     * the returned products from the given lists. Returns an empty list if the
     * file does not exist.
     *
     * @param sales sales used to resolve the original sale of each return
     * @param catalog products and accessories used to resolve the returned items
     * @return the returns read from disk
     */
    public List<Return> loadAll(List<Sale> sales, List<Product> catalog) {
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
                Sale sale = findSale(sales, parts[2]);

                List<Product> products = new ArrayList<>();
                for (String productId : parts[3].split(",")) {
                    if (!productId.isBlank()) {
                        products.add(findItem(catalog, productId));
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
            throw new PersistenceException("No se pudo leer el archivo " + FILE_PATH
                    + " (operacion: carga de devoluciones): " + e.getMessage(), e);
        }
        return returns;
    }

    /**
     * Finds a sale by id in the given list.
     *
     * @return the sale, or null if there is none with that id
     */
    private Sale findSale(List<Sale> sales, String saleId) {
        for (Sale sale : sales) {
            if (sale.getId().equals(saleId)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * Finds a product or accessory by id in the given catalog.
     *
     * @return the item, or null if there is none with that id
     */
    private Product findItem(List<Product> catalog, String itemId) {
        for (Product item : catalog) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }
}
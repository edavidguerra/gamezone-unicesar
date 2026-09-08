package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing Sale data to a plain text file.
 */
public class SaleRepository {

    private static final String SALES_FILE = "data/sales.csv";

    /**
     * Saves the full list of sales to disk, overwriting the file.
     *
     * @param sales list of sales to persist
     */
    public void saveAll(List<Sale> sales) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SALES_FILE))) {
            for (Sale sale : sales) {
                StringBuilder productIds = new StringBuilder();
                for (Product product : sale.getProducts()) {
                    if (productIds.length() > 0) {
                        productIds.append(",");
                    }
                    productIds.append(product.getId());
                }
                writer.println(sale.getId() + "|" + sale.getDate() + "|"
                        + sale.getClient().getId() + "|" + sale.getSeller().getId() + "|"
                        + productIds);
            }
        } catch (IOException e) {
            System.out.println("Error saving sales: " + e.getMessage());
        }
    }

    /**
     * Loads all sales from disk, resolving client, seller and product
     * references from the lists already loaded by the other modules.
     *
     * @param clients  clients already loaded by PersonService
     * @param sellers  sellers already loaded by PersonService
     * @param products products already loaded by ProductService
     * @return list of reconstructed sales
     */
    public List<Sale> loadAll(List<Client> clients, List<Seller> sellers, List<Product> products) {
        List<Sale> sales = new ArrayList<>();
        File file = new File(SALES_FILE);
        if (!file.exists()) {
            return sales;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                Client client = findClient(clients, parts[2]);
                Seller seller = findSeller(sellers, parts[3]);
                List<Product> soldProducts = new ArrayList<>();
                for (String productId : parts[4].split(",")) {
                    Product product = findProduct(products, productId);
                    if (product != null) {
                        soldProducts.add(product);
                    }
                }
                sales.add(new Sale(parts[0], parts[1], client, seller, soldProducts));
            }
        } catch (IOException e) {
            System.out.println("Error loading sales: " + e.getMessage());
        }
        return sales;
    }

    private Client findClient(List<Client> clients, String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    private Seller findSeller(List<Seller> sellers, String id) {
        for (Seller seller : sellers) {
            if (seller.getId().equals(id)) {
                return seller;
            }
        }
        return null;
    }

    private Product findProduct(List<Product> products, String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
}
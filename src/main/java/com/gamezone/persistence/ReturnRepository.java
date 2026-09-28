package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
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

    public ReturnRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

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
                        + returnItem.getReason() + "|" + returnItem.getRefundAmount());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar devoluciones: " + e.getMessage());
        }
    }

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
                        products.add(productService.findById(productId));
                    }
                }
                String reason = parts[4];

                // El monto de reembolso NO se confia del archivo: se recalcula con la
                // regla vigente, para que corregirla mas adelante (ajuste A5) actualice
                // automaticamente hasta las devoluciones ya guardadas en disco.
                Return returnItem = new Return(id, date, sale, products, reason);
                returnItem.calculateRefundAmount();
                returns.add(returnItem);
            }
        } catch (IOException e) {
            System.out.println("Error al leer devoluciones: " + e.getMessage());
        }
        return returns;
    }
}
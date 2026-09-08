package com.gamezone.service;
import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;
import java.util.List;

/**
 * Contains the business rules related to products: registering new
 * items, listing the inventory and validating or updating stock.
 */

public class ProductService {

    private ProductRepository productRepository;
    private List<Product> products;

    /**
     * Creates the product service and loads previously stored products.
     *
     * @param productRepository repository used to persist products
     */

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadAll();
    }
}
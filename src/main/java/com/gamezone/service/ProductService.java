package com.gamezone.service;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
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

        /**
     * Registers a new video game and persists the updated inventory.
     *
     * @return the registered video game
     */

    public VideoGame registerVideoGame(String id, String title, double price, int stock,
                                        String platform, String genre, String ageRating) {
        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        productRepository.saveAll(products);
        return videoGame;
    }

    /**
     * Registers a new console and persists the updated inventory.
     *
     * @return the registered console
     */

    public Console registerConsole(String id, String title, double price, int stock,
                                    String brand, String model, String generation) {
        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        productRepository.saveAll(products);
        return console;
    }

    /**
     * @return the complete list of products in the inventory
     */

    public List<Product> listProducts() {
        return products;
    }

    /**
     * @param id product id
     * @return the product with the given id, or null if it does not exist
     */

    public Product findById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Checks whether the product with the given id has enough stock.
     *
     * @param id product id
     * @param quantity quantity requested
     * @return true if the product exists and has enough stock
     */

    public boolean hasStock(String id, int quantity) {
        Product product = findById(id);
        return product != null && product.hasStock(quantity);
    }

    /**
     * Reduces the stock of the given product and persists the change.
     *
     * @param id product id
     * @param quantity quantity sold
     */
    
    public void reduceStock(String id, int quantity) {
        Product product = findById(id);
        if (product != null) {
            product.reduceStock(quantity);
            productRepository.saveAll(products);
        }
    }
}
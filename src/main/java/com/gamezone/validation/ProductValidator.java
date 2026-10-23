package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Validation rules for products.
 */
public class ProductValidator {

    private ProductValidator() {
    }

    /**
     * Validates the data of a product before it is registered.
     *
     * @param id product identifier, required
     * @param title product title, required
     * @param price unit price, must be positive
     * @param stock units in stock, must not be negative
     * @throws InvalidDataException if any value is invalid
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacio");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("titulo", "no puede estar vacio");
        }
        if (price <= 0) {
            throw new InvalidDataException("precio", "debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    /**
     * Validates that a product has enough stock.
     *
     * @param product product to check
     * @param requestedQuantity units requested
     * @throws BusinessRuleException if the stock is insufficient
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (!product.hasStock(requestedQuantity)) {
            throw new BusinessRuleException("Stock insuficiente para el producto: " + product.getId());
        }
    }

    /**
     * Validates that a searched product exists.
     *
     * @param product result of the search
     * @param productId identifier that was searched
     * @throws ResourceNotFoundException if the product is null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}
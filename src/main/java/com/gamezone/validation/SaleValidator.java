package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Validation rules for sales. The customer of the specification is the
 * Client class of this project.
 */
public class SaleValidator {

    private SaleValidator() {
    }

    /**
     * Validates the data of a sale before it is registered.
     *
     * @param customer client of the sale, required
     * @param seller seller of the sale, required
     * @param products products sold, must not be empty
     * @throws InvalidDataException if the client or seller is missing
     * @throws BusinessRuleException if the sale has no products
     */
    public static void validateSaleData(Client customer, Seller seller, List<Product> products) {
        if (customer == null) {
            throw new InvalidDataException("cliente", "es obligatorio");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "es obligatorio");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("Una venta debe contener al menos un producto");
        }
    }

    /**
     * Validates that a searched sale exists.
     *
     * @param sale result of the search
     * @param saleId identifier that was searched
     * @throws ResourceNotFoundException if the sale is null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}
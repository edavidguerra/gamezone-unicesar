package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates return registration and reporting: validates the 30-day
 * window, restores inventory, and computes sales/returns balances.
 */
public class ReturnService {

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final ProductService productService;
    private List<Return> returns;

    public ReturnService(ReturnRepository repository, SaleService saleService,
            ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = repository.loadAll();
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("La venta " + saleId + " no existe.");
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException(
                    "La venta " + saleId + " supera los 30 dias permitidos para devolucion.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            if (product == null) {
                throw new IllegalArgumentException(
                        "El producto " + productId + " no pertenece a la venta " + saleId + ".");
            }
            returnedProducts.add(product);
        }

        Return returnItem = new Return(generateId(), LocalDate.now(), sale, returnedProducts, reason);
        returnItem.calculateRefundAmount();

        for (Product product : returnedProducts) {
            productService.restoreStock(product.getId(), 1);
        }

        returns.add(returnItem);
        repository.saveAll(returns);
        return returnItem;
    }

    public List<Return> viewAllReturns() {
        return returns;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (Return returnItem : returns) {
            if (returnItem.getOriginalSale().getId().equals(saleId)) {
                result.add(returnItem);
            }
        }
        return result;
    }

    /**
     * Net monthly balance: total sales minus total returns for the given
     * month and year.
     */
    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(month, year);
    }

    private double calculateMonthlySales(int month, int year) {
        double total = 0.0;
        for (Sale sale : saleService.listSales()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());
            if (saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                total += sale.getFinalTotal();
            }
        }
        return total;
    }

    private double calculateMonthlyReturns(int month, int year) {
        double total = 0.0;
        for (Return returnItem : returns) {
            if (returnItem.getDate().getMonthValue() == month
                    && returnItem.getDate().getYear() == year) {
                total += returnItem.getRefundAmount();
            }
        }
        return total;
    }

    private Product findProductInSale(Sale sale, String productId) {
        for (Product product : sale.getProducts()) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    private String generateId() {
        return "R" + (returns.size() + 1);
    }
}
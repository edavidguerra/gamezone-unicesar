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

        /**
     * Creates the service and loads the persisted returns.
     *
     * @param repository persistence of returns
     * @param saleService service used to find and list sales
     * @param productService service used to restore product stock
     */
    public ReturnService(ReturnRepository repository, SaleService saleService,
            ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = repository.loadAll();
    }

      /**
     * Registers a return: validates that the sale exists, is inside the
     * 30-day window and contains every product, restores stock, and
     * persists the new return.
     *
     * @param saleId identifier of the original sale
     * @param productIds identifiers of the products being returned
     * @param reason reason given by the customer
     * @return the registered return
     * @throws IllegalArgumentException with a Spanish message if any validation fails
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("La venta " + saleId + " no existe.");
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException(
                    "La venta " + saleId + " supera los 30 dias permitidos para devolucion.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto a devolver.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            if (product == null) {
                throw new IllegalArgumentException(
                        "El producto " + productId + " no pertenece a la venta " + saleId + ".");
            }
            long available = countInSale(sale, productId)
                    - countAlreadyReturned(saleId, productId)
                    - countInList(returnedProducts, productId);
            if (available <= 0) {
                throw new IllegalArgumentException(
                        "El producto " + productId + " ya fue devuelto en su totalidad de la venta "
                        + saleId + ".");
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

       /**
     * Returns every registered return.
     *
     * @return list of all returns
     */
    public List<Return> viewAllReturns() {
        return returns;
    }

       /**
        * Returns the returns whose original sale belongs to the given customer.
        *
        * @param customerId identifier of the customer
        * @return list of returns of that customer (empty if none)
        */
       public List<Return> viewReturnsByCustomer(String customerId) {
           List<Return> result = new ArrayList<>();
           for (Return returnItem : returns) {
               Sale sale = returnItem.getOriginalSale();
               if (sale.getClient() != null && sale.getClient().getId().equals(customerId)) {
                   result.add(returnItem);
               }
           }
           return result;
       }

     /**
     * Returns the returns associated with the given sale.
     *
     * @param saleId identifier of the sale
     * @return list of returns of that sale (empty if none)
     */
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
       /**
     * Net monthly balance: total sales minus total returns for the given
     * month and year.
     *
     * @param month month of the year (1-12)
     * @param year four-digit year
     * @return net balance for the period
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

    private long countInSale(Sale sale, String productId) {
        return countInList(sale.getProducts(), productId);
    }

    private long countInList(List<Product> products, String productId) {
        long count = 0;
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                count++;
            }
        }
        return count;
    }

    private long countAlreadyReturned(String saleId, String productId) {
        long count = 0;
        for (Return returnItem : returns) {
            if (returnItem.getOriginalSale().getId().equals(saleId)) {
                count += countInList(returnItem.getReturnedProducts(), productId);
            }
        }
        return count;
    }
    private String generateId() {
        return "R" + (returns.size() + 1);
    }
}
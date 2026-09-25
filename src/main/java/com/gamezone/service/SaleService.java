package com.gamezone.service;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the business rules related to sales: validating client and
 * seller, checking and discounting stock, and calculating totals.
 */
public class SaleService {

    private SaleRepository saleRepository;
    private ProductService productService;
    private PersonService personService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;
    private List<Sale> sales;

    public SaleService(SaleRepository saleRepository, ProductService productService,
                        PersonService personService, AccessoryService accessoryService,
                        PromotionService promotionService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.sales = saleRepository.loadAll(
                personService.listClients(),
                personService.listSellers(),
                productService.listProducts());
    }

    public Sale registerSale(String id, String clientId, String sellerId,
                              List<String> productIds, List<String> accessoryIds) {
        Client client = personService.findClientById(clientId);
        Seller seller = personService.findSellerById(sellerId);

        if (client == null || seller == null) {
            throw new IllegalArgumentException("Client or seller not found.");
        }
        boolean noProducts = productIds == null || productIds.isEmpty();
        boolean noAccessories = accessoryIds == null || accessoryIds.isEmpty();
        if (noProducts && noAccessories) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }
        if (productIds != null) {
            for (String productId : productIds) {
                if (!productService.hasStock(productId, 1)) {
                    throw new IllegalStateException("Insufficient stock for product: " + productId);
                }
            }
        }
        if (accessoryIds != null) {
            for (String accessoryId : accessoryIds) {
                if (!accessoryService.hasStock(accessoryId, 1)) {
                    throw new IllegalStateException("Insufficient stock for accessory: " + accessoryId);
                }
            }
        }

        List<Product> soldProducts = new ArrayList<>();
        if (productIds != null) {
            for (String productId : productIds) {
                soldProducts.add(productService.findById(productId));
                productService.reduceStock(productId, 1);
            }
        }
        if (accessoryIds != null) {
            for (String accessoryId : accessoryIds) {
                soldProducts.add(accessoryService.findById(accessoryId));
                accessoryService.updateStock(accessoryId, 1);
            }
        }

        Sale sale = new Sale(id, LocalDate.now().toString(), client, seller, soldProducts);

        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            double discount = bestPromotion.calculateDiscount(sale);
            sale.setAppliedPromotionName(bestPromotion.getName());
            sale.setDiscountAmount(discount);
        }

        sales.add(sale);
        saleRepository.saveAll(sales);
        return sale;
    }

    public List<Sale> listSales() {
        return sales;
    }

    public List<Sale> listSalesByClient(String clientId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getClient().getId().equals(clientId)) {
                result.add(sale);
            }
        }
        return result;
    }

    public List<Sale> listSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getSeller().getId().equals(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }
}
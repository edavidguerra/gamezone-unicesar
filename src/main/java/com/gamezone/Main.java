package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.ConsoleMenu;

/**
 * Entry point of the GameZone Unicesar application. Wires together the
 * persistence, service and UI layers and starts the console menu.
 *
 * Construction order note: SaleService is built first without a
 * WarrantyService (its constructor does not require one). Then
 * WarrantyRepository/WarrantyService are built, which do need
 * SaleService already constructed to resolve sale references. Finally
 * WarrantyService is injected back into SaleService through a setter.
 * This avoids the circular dependency described in ajuste A2.
 */
public class Main {

    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        PersonRepository personRepository = new PersonRepository();
        SaleRepository saleRepository = new SaleRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository();
        PromotionRepository promotionRepository = new PromotionRepository();
        WarrantyRepository warrantyRepository = new WarrantyRepository();

        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        SaleService saleService = new SaleService(saleRepository, productService,
                personService, accessoryService, promotionService);

        WarrantyService warrantyService = new WarrantyService(warrantyRepository, saleService, productService);
        saleService.setWarrantyService(warrantyService);

        ReturnRepository returnRepository = new ReturnRepository(saleService, productService, accessoryService);
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService, accessoryService);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService,
                accessoryService, promotionService, warrantyService, returnService);
        menu.start();
    }
}
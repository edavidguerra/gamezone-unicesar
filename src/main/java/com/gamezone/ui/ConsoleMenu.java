package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Accessory;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.Cable;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface. Displays menus and delegates every
 * operation to the corresponding service; never touches persistence
 * directly.
 */
public class ConsoleMenu {

    private ProductService productService;
    private PersonService personService;
    private SaleService saleService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;
    private WarrantyService warrantyService;
    private ReturnService returnService;
    private Scanner scanner;

    public ConsoleMenu(ProductService productService, PersonService personService,
                        SaleService saleService, AccessoryService accessoryService,
                        PromotionService promotionService, WarrantyService warrantyService,
                        ReturnService returnService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
        this.returnService = returnService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int option;
        do {
            System.out.println("\n==== GameZone Unicesar ====");
            System.out.println("1. Menu de productos");
            System.out.println("2. Menu de personas");
            System.out.println("3. Menu de ventas");
            System.out.println("4. Menu de accesorios");
            System.out.println("5. Menu de promociones");
            System.out.println("6. Menu de garantias");
            System.out.println("7. Menu de devoluciones");
            System.out.println("0. Salir");
            System.out.print("Elija una opcion: ");
            option = readInt();
            switch (option) {
                case 1 -> productsMenu();
                case 2 -> peopleMenu();
                case 3 -> salesMenu();
                case 4 -> accessoriesMenu();
                case 5 -> promotionsMenu();
                case 6 -> warrantiesMenu();
                case 7 -> returnsMenu();
                case 0 -> System.out.println("Hasta luego!");
                default -> System.out.println("Opcion invalida.");
            }
        } while (option != 0);
    }

    private void productsMenu() {
        System.out.println("\n-- Productos --");
        System.out.println("1. Registrar videojuego");
        System.out.println("2. Registrar consola");
        System.out.println("3. Listar productos");
        System.out.print("Elija una opcion: ");
        switch (readInt()) {
            case 1 -> execute(this::registerVideoGame);
            case 2 -> execute(this::registerConsole);
            case 3 -> execute(this::listProducts);
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerVideoGame() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Titulo: "); String title = scanner.nextLine();
        System.out.print("Precio: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Plataforma: "); String platform = scanner.nextLine();
        System.out.print("Genero: "); String genre = scanner.nextLine();
        System.out.print("Clasificacion por edad: "); String ageRating = scanner.nextLine();
        VideoGame videoGame = productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
        System.out.println("Registrado: " + videoGame.getDescription());
    }

    private void registerConsole() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Titulo: "); String title = scanner.nextLine();
        System.out.print("Precio: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Marca: "); String brand = scanner.nextLine();
        System.out.print("Modelo: "); String model = scanner.nextLine();
        System.out.print("Generacion: "); String generation = scanner.nextLine();
        Console console = productService.registerConsole(id, title, price, stock, brand, model, generation);
        System.out.println("Registrado: " + console.getDescription());
    }

    private void listProducts() {
        for (Product product : productService.listProducts()) {
            System.out.println(product.getDescription());
        }
    }

    private void peopleMenu() {
        System.out.println("\n-- Personas --");
        System.out.println("1. Registrar cliente");
        System.out.println("2. Listar clientes");
        System.out.println("3. Listar vendedores");
        System.out.print("Elija una opcion: ");
        switch (readInt()) {
            case 1 -> execute(this::registerClient);
            case 2 -> execute(this::listClients);
            case 3 -> execute(this::listSellers);
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerClient() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String name = scanner.nextLine();
        System.out.print("Telefono: "); String phone = scanner.nextLine();
        System.out.print("Correo: "); String email = scanner.nextLine();
        Client client = personService.registerClient(id, name, phone, email);
        System.out.println("Registrado: " + client.getRoleDescription());
    }

    private void listClients() {
        for (Client client : personService.listClients()) {
            System.out.println(client.getName() + " - " + client.getRoleDescription());
        }
    }

    private void listSellers() {
        for (Seller seller : personService.listSellers()) {
            System.out.println(seller.getName() + " - " + seller.getRoleDescription());
        }
    }

    private void salesMenu() {
        System.out.println("\n-- Ventas --");
        System.out.println("1. Registrar venta");
        System.out.println("2. Listar todas las ventas");
        System.out.println("3. Listar ventas por cliente");
        System.out.println("4. Listar ventas por vendedor");
        System.out.print("Elija una opcion: ");
        switch (readInt()) {
            case 1 -> execute(this::registerSale);
            case 2 -> execute(() -> printSales(saleService.listSales()));
            case 3 -> execute(() -> {
                System.out.print("Id del cliente: ");
                printSales(saleService.listSalesByClient(scanner.nextLine()));
            });
            case 4 -> execute(() -> {
                System.out.print("Id del vendedor: ");
                printSales(saleService.listSalesBySeller(scanner.nextLine()));
            });
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerSale() {
        System.out.print("Id de la venta: "); String id = scanner.nextLine();
        System.out.print("Id del cliente: "); String clientId = scanner.nextLine();
        System.out.print("Id del vendedor: "); String sellerId = scanner.nextLine();
        System.out.print("Cuantos productos? "); int count = Integer.parseInt(scanner.nextLine());
        List<String> productIds = new ArrayList<>();
        List<String> productIdsWithExtendedWarranty = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Id del producto #" + (i + 1) + ": ");
            String productId = scanner.nextLine();
            productIds.add(productId);
            Product product = productService.findById(productId);
            if (product instanceof Console) {
                System.out.print("Garantia extendida para " + product.getTitle() + "? (s/n): ");
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("s") || answer.equalsIgnoreCase("y")) {
                    productIdsWithExtendedWarranty.add(productId);
                }
            }
        }
        System.out.print("Cuantos accesorios? "); int accessoryCount = Integer.parseInt(scanner.nextLine());
        List<String> accessoryIds = new ArrayList<>();
        for (int i = 0; i < accessoryCount; i++) {
            System.out.print("Id del accesorio #" + (i + 1) + ": ");
            accessoryIds.add(scanner.nextLine());
        }
        Sale sale = saleService.registerSale(id, clientId, sellerId, productIds, accessoryIds,
                productIdsWithExtendedWarranty);
        System.out.println(sale.generateReceipt());
    }

    private void printSales(List<Sale> sales) {
        for (Sale sale : sales) {
            System.out.println(sale.getDate() + " | " + sale.getClient().getName()
                    + " | " + sale.getSeller().getName() + " | Total: " + sale.calculateTotal());
        }
    }

    private void accessoriesMenu() {
        System.out.println("\n-- Accesorios --");
        System.out.println("1. Registrar un control");
        System.out.println("2. Registrar un cable");
        System.out.println("3. Registrar una memoria");
        System.out.println("4. Listar todos los accesorios");
        System.out.println("5. Listar accesorios por tipo");
        System.out.println("6. Buscar accesorios compatibles con una consola");
        System.out.println("0. Volver");
        switch (readInt()) {
            case 1 -> execute(this::registerControllerFlow);
            case 2 -> execute(this::registerCableFlow);
            case 3 -> execute(this::registerMemoryFlow);
            case 4 -> execute(this::listAllAccessoriesFlow);
            case 5 -> execute(this::listAccessoriesByTypeFlow);
            case 6 -> execute(this::listCompatibleAccessoriesFlow);
            case 0 -> { /* back */ }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerControllerFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Titulo: "); String title = scanner.nextLine();
        System.out.print("Precio: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Tipo de conexion: "); String connectionType = scanner.nextLine();
        Controller controller = accessoryService.registerController(id, title, price, stock, connectionType);
        System.out.println("Registrado: " + controller.getDescription());
    }

    private void registerCableFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Titulo: "); String title = scanner.nextLine();
        System.out.print("Precio: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Longitud en metros: "); double lengthInMeters = Double.parseDouble(scanner.nextLine());
        System.out.print("Tipo de conector: "); String connectorType = scanner.nextLine();
        Cable cable = accessoryService.registerCable(id, title, price, stock, lengthInMeters, connectorType);
        System.out.println("Registrado: " + cable.getDescription());
    }

    private void registerMemoryFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Titulo: "); String title = scanner.nextLine();
        System.out.print("Precio: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Capacidad en gigabytes: "); int capacityInGigabytes = Integer.parseInt(scanner.nextLine());
        System.out.print("Tipo de memoria: "); String memoryType = scanner.nextLine();
        Memory memory = accessoryService.registerMemory(id, title, price, stock, capacityInGigabytes, memoryType);
        System.out.println("Registrado: " + memory.getDescription());
    }

    private void listAllAccessoriesFlow() {
        for (Accessory accessory : accessoryService.listAllAccessories()) {
            System.out.println(accessory.getDescription());
        }
    }

    private void listAccessoriesByTypeFlow() {
        System.out.print("Tipo (CONTROLLER, CABLE, MEMORY): ");
        String type = scanner.nextLine().toUpperCase();
        for (Accessory accessory : accessoryService.listAllAccessories()) {
            boolean matches = switch (type) {
                case "CONTROLLER" -> accessory instanceof Controller;
                case "CABLE" -> accessory instanceof Cable;
                case "MEMORY" -> accessory instanceof Memory;
                default -> false;
            };
            if (matches) {
                System.out.println(accessory.getDescription());
            }
        }
    }

    private void listCompatibleAccessoriesFlow() {
        System.out.print("Id de la consola: "); String consoleId = scanner.nextLine();
        for (Accessory accessory : accessoryService.findAccessoriesCompatibleWith(consoleId)) {
            System.out.println(accessory.getDescription());
        }
    }

    private void promotionsMenu() {
        System.out.println("\n-- Promociones --");
        System.out.println("1. Registrar descuento porcentual");
        System.out.println("2. Registrar descuento por categoria");
        System.out.println("3. Registrar descuento por volumen");
        System.out.println("4. Listar todas las promociones");
        System.out.println("5. Listar promociones vigentes");
        System.out.println("0. Volver");
        switch (readInt()) {
            case 1 -> execute(this::registerPercentageDiscountFlow);
            case 2 -> execute(this::registerCategoryDiscountFlow);
            case 3 -> execute(this::registerBulkPurchaseDiscountFlow);
            case 4 -> execute(this::listAllPromotionsFlow);
            case 5 -> execute(this::listActivePromotionsFlow);
            case 0 -> { /* back */ }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerPercentageDiscountFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String name = scanner.nextLine();
        System.out.print("Fecha de inicio (AAAA-MM-DD): "); LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha de fin (AAAA-MM-DD): "); LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Porcentaje: "); double percentage = Double.parseDouble(scanner.nextLine());
        PercentageDiscount promo = promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
        System.out.println("Registrado: " + promo.getName());
    }

    private void registerCategoryDiscountFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String name = scanner.nextLine();
        System.out.print("Fecha de inicio (AAAA-MM-DD): "); LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha de fin (AAAA-MM-DD): "); LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Porcentaje: "); double percentage = Double.parseDouble(scanner.nextLine());
        System.out.print("Categoria (VIDEOGAME, CONSOLE, ACCESSORY): "); String targetCategory = scanner.nextLine().toUpperCase();
        CategoryDiscount promo = promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        System.out.println("Registrado: " + promo.getName());
    }

    private void registerBulkPurchaseDiscountFlow() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String name = scanner.nextLine();
        System.out.print("Fecha de inicio (AAAA-MM-DD): "); LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha de fin (AAAA-MM-DD): "); LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Cantidad minima: "); int minimumQuantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Porcentaje: "); double percentage = Double.parseDouble(scanner.nextLine());
        BulkPurchaseDiscount promo = promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
        System.out.println("Registrado: " + promo.getName());
    }

    private void listAllPromotionsFlow() {
        for (Promotion promotion : promotionService.listAllPromotions()) {
            System.out.println(promotion.getName());
        }
    }

    private void listActivePromotionsFlow() {
        for (Promotion promotion : promotionService.listActivePromotions()) {
            System.out.println(promotion.getName());
        }
    }

    private void warrantiesMenu() {
        System.out.println("\n-- Garantias --");
        System.out.println("1. Buscar garantia por producto y venta");
        System.out.println("2. Listar todas las garantias");
        System.out.println("3. Listar garantias activas");
        System.out.println("4. Listar garantias proximas a vencer");
        System.out.println("0. Volver");
        switch (readInt()) {
            case 1 -> execute(this::findWarrantyByProductFlow);
            case 2 -> execute(this::listAllWarrantiesFlow);
            case 3 -> execute(this::listActiveWarrantiesFlow);
            case 4 -> execute(this::listWarrantiesExpiringSoonFlow);
            case 0 -> { /* back */ }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void findWarrantyByProductFlow() {
        System.out.print("Id del producto: "); String productId = scanner.nextLine();
        System.out.print("Id de la venta: "); String saleId = scanner.nextLine();
        Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
        if (warranty != null) {
            System.out.println(warranty.generateWarrantyCertificate());
        } else {
            System.out.println("No se encontro garantia para ese producto y venta.");
        }
    }

    private void listAllWarrantiesFlow() {
        for (Warranty warranty : warrantyService.listAllWarranties()) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    private void listActiveWarrantiesFlow() {
        for (Warranty warranty : warrantyService.listActiveWarranties()) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    private void listWarrantiesExpiringSoonFlow() {
        System.out.print("Dias hacia adelante: "); int daysAhead = Integer.parseInt(scanner.nextLine());
        for (Warranty warranty : warrantyService.listWarrantiesExpiringSoon(daysAhead)) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    /**
     * Returns submenu: registers returns, lists them by different criteria
     * and shows the monthly balance of sales against returns.
     */
    private void returnsMenu() {
        System.out.println("\n--- Gestion de devoluciones ---");
        System.out.println("1. Registrar devolucion");
        System.out.println("2. Consultar todas las devoluciones");
        System.out.println("3. Consultar devoluciones por cliente");
        System.out.println("4. Consultar devoluciones por venta");
        System.out.println("5. Consultar balance mensual");
        System.out.println("0. Volver");
        System.out.print("Elija una opcion: ");
        switch (readInt()) {
            case 1 -> execute(this::registerReturnFlow);
            case 2 -> execute(this::listAllReturnsFlow);
            case 3 -> execute(this::listReturnsByCustomerFlow);
            case 4 -> execute(this::listReturnsBySaleFlow);
            case 5 -> execute(this::showMonthlyBalanceFlow);
            case 0 -> { /* back */ }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void registerReturnFlow() {
        System.out.print("Id de la venta: "); String saleId = scanner.nextLine();
        System.out.print("Cuantos productos desea devolver? ");
        int count = Integer.parseInt(scanner.nextLine());
        List<String> productIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Id del producto #" + (i + 1) + ": ");
            productIds.add(scanner.nextLine());
        }
        System.out.print("Motivo de la devolucion: "); String reason = scanner.nextLine();
        Return returnItem = returnService.registerReturn(saleId, productIds, reason);
        System.out.println(returnItem.generateReturnReceipt());
    }

    private void listAllReturnsFlow() {
        printReturns(returnService.viewAllReturns(), "No hay devoluciones registradas.");
    }

    private void listReturnsByCustomerFlow() {
        System.out.print("Id del cliente: ");
        String customerId = scanner.nextLine();
        printReturns(returnService.viewReturnsByCustomer(customerId),
                "No hay devoluciones registradas para el cliente " + customerId + ".");
    }

    private void listReturnsBySaleFlow() {
        System.out.print("Id de la venta: ");
        String saleId = scanner.nextLine();
        printReturns(returnService.viewReturnsBySale(saleId),
                "No hay devoluciones registradas para la venta " + saleId + ".");
    }

    private void printReturns(List<Return> returns, String emptyMessage) {
        if (returns.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Return returnItem : returns) {
            System.out.println(returnItem.generateReturnReceipt());
            System.out.println("-----");
        }
    }

    private void showMonthlyBalanceFlow() {
        System.out.print("Mes (1-12): "); int month = Integer.parseInt(scanner.nextLine());
        System.out.print("Anio: "); int year = Integer.parseInt(scanner.nextLine());
        if (month < 1 || month > 12) {
            System.out.println("Mes invalido. Debe estar entre 1 y 12.");
            return;
        }
        double sales = returnService.calculateMonthlySales(month, year);
        double returns = returnService.calculateMonthlyReturns(month, year);
        double balance = returnService.generateMonthlyBalance(month, year);
        System.out.println("Total de ventas del mes: " + sales);
        System.out.println("Total de devoluciones del mes: " + returns);
        System.out.println("Balance neto del mes (ventas - devoluciones): " + balance);
    }

    /**
     * Runs one menu operation and translates every failure into a Spanish
     * message, so that an error never stops the application. The catch
     * blocks go from the most specific exception to the most general one.
     *
     * @param operation the menu operation to run
     */
    private void execute(Runnable operation) {
        try {
            operation.run();
        } catch (ResourceNotFoundException e) {
            System.out.println("[No encontrado] " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("[Operacion no permitida] " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("[Dato invalido] " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("[Error de almacenamiento] " + e.getMessage());
        } catch (GameZoneException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("[Dato invalido] Debe ingresar un numero valido.");
        } catch (DateTimeParseException e) {
            System.out.println("[Dato invalido] La fecha debe tener el formato AAAA-MM-DD.");
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
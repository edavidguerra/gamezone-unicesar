package com.gamezone.ui;

import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

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
    private Scanner scanner;

    public ConsoleMenu(ProductService productService, PersonService personService,
                        SaleService saleService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int option;
        do {
            System.out.println("\n==== GameZone Unicesar ====");
            System.out.println("1. Products menu");
            System.out.println("2. People menu");
            System.out.println("3. Sales menu");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            option = readInt();
            switch (option) {
                case 1 -> productsMenu();
                case 2 -> peopleMenu();
                case 3 -> salesMenu();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    private void productsMenu() {
        System.out.println("\n-- Products --");
        System.out.println("1. Register video game");
        System.out.println("2. Register console");
        System.out.println("3. List products");
        System.out.print("Choose an option: ");
        switch (readInt()) {
            case 1 -> registerVideoGame();
            case 2 -> registerConsole();
            case 3 -> listProducts();
            default -> System.out.println("Invalid option.");
        }
    }

    private void registerVideoGame() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Title: "); String title = scanner.nextLine();
        System.out.print("Price: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Platform: "); String platform = scanner.nextLine();
        System.out.print("Genre: "); String genre = scanner.nextLine();
        System.out.print("Age rating: "); String ageRating = scanner.nextLine();
        VideoGame videoGame = productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
        System.out.println("Registered: " + videoGame.getDescription());
    }

    private void registerConsole() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Title: "); String title = scanner.nextLine();
        System.out.print("Price: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Brand: "); String brand = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Generation: "); String generation = scanner.nextLine();
        Console console = productService.registerConsole(id, title, price, stock, brand, model, generation);
        System.out.println("Registered: " + console.getDescription());
    }

    private void listProducts() {
        for (Product product : productService.listProducts()) {
            System.out.println(product.getDescription());
        }
    }

    private void peopleMenu() {
        System.out.println("\n-- People --");
        System.out.println("1. Register client");
        System.out.println("2. List clients");
        System.out.println("3. List sellers");
        System.out.print("Choose an option: ");
        switch (readInt()) {
            case 1 -> registerClient();
            case 2 -> listClients();
            case 3 -> listSellers();
            default -> System.out.println("Invalid option.");
        }
    }

    private void registerClient() {
        System.out.print("Id: "); String id = scanner.nextLine();
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Phone: "); String phone = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        Client client = personService.registerClient(id, name, phone, email);
        System.out.println("Registered: " + client.getRoleDescription());
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
        System.out.println("\n-- Sales --");
        System.out.println("1. Register sale");
        System.out.println("2. List all sales");
        System.out.println("3. List sales by client");
        System.out.println("4. List sales by seller");
        System.out.print("Choose an option: ");
        switch (readInt()) {
            case 1 -> registerSale();
            case 2 -> printSales(saleService.listSales());
            case 3 -> { System.out.print("Client id: "); printSales(saleService.listSalesByClient(scanner.nextLine())); }
            case 4 -> { System.out.print("Seller id: "); printSales(saleService.listSalesBySeller(scanner.nextLine())); }
            default -> System.out.println("Invalid option.");
        }
    }

    private void registerSale() {
        System.out.print("Sale id: "); String id = scanner.nextLine();
        System.out.print("Client id: "); String clientId = scanner.nextLine();
        System.out.print("Seller id: "); String sellerId = scanner.nextLine();
        System.out.print("How many products? "); int count = Integer.parseInt(scanner.nextLine());
        List<String> productIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Product id #" + (i + 1) + ": ");
            productIds.add(scanner.nextLine());
        }
        Sale sale = saleService.registerSale(id, clientId, sellerId, productIds);
        System.out.println("Sale registered. Total: " + sale.calculateTotal());
    }

    private void printSales(List<Sale> sales) {
        for (Sale sale : sales) {
            System.out.println(sale.getDate() + " | " + sale.getClient().getName()
                    + " | " + sale.getSeller().getName() + " | Total: " + sale.calculateTotal());
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
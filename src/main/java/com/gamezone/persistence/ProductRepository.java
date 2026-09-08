package com.gamezone.persistence;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
private static final String PRODUCTS_FILE = "data/products.csv";

public void saveAll(List<Product> products) {

try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {

for (Product product : products) {
    
    if (product instanceof VideoGame videoGame) {
writer.println("VIDEOGAME|" + videoGame.getId() + "|" + videoGame.getTitle()
+ "|" + videoGame.getPrice() + "|" + videoGame.getStock()
+ "|" + videoGame.getPlatform() + "|" + videoGame.getGenre()
+ "|" + videoGame.getAgeRating());

    } else if (product instanceof Console console) {
writer.println("CONSOLE|" + console.getId() + "|" + console.getTitle()
+ "|" + console.getPrice() + "|" + console.getStock()
+ "|" + console.getBrand() + "|" + console.getModel()
+ "|" + console.getGeneration());
}
}

} catch (IOException e) {
System.out.println("Error saving products: " + e.getMessage());
    }
}

public List<Product> loadAll() {
List<Product> products = new ArrayList<>();
File file = new File(PRODUCTS_FILE);
if (!file.exists()) {
return products;
}

try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
String line;
while ((line = reader.readLine()) != null) {
if (line.isBlank()) {
continue;
}

String[] parts = line.split("\\|");
String type = parts[0];

if (type.equals("VIDEOGAME")) {
products.add(new VideoGame(parts[1], parts[2],
Double.parseDouble(parts[3]), Integer.parseInt(parts[4]),
parts[5], parts[6], parts[7]));

    } else if (type.equals("CONSOLE")) {
    products.add(new Console(parts[1], parts[2],
    Double.parseDouble(parts[3]), Integer.parseInt(parts[4]),
    parts[5], parts[6], parts[7]));
        }
    }

} catch (IOException e) {
    System.out.println("Error loading products: " + e.getMessage());
}

return products;
}
}
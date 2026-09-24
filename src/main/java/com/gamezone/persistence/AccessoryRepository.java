package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing Accessory data (Controller, Cable and
 * Memory) to a pipe-delimited text file. Each line starts with a type
 * token that identifies the concrete subclass, followed by its own
 * attributes and the list of compatible console ids.
 */
public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";

    /**
     * Saves the full list of accessories to disk, overwriting the file.
     * Creates the data folder first if it does not exist yet.
     *
     * @param accessories list of accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Accessory accessory : accessories) {
                writer.println(toLine(accessory));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar accesorios: " + e.getMessage());
        }
    }

    /**
     * Loads all accessories from disk. Returns an empty list if the file
     * does not exist yet (for example, on the first execution).
     *
     * @return list of accessories loaded from disk
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return accessories;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                accessories.add(fromLine(line));
            }
        } catch (IOException e) {
            System.out.println("Error al leer accesorios: " + e.getMessage());
        }
        return accessories;
    }

    /**
     * Converts an accessory into a single pipe-delimited line. Pattern
     * matching with instanceof is used to write the specific attributes
     * of each concrete accessory type.
     *
     * @param accessory accessory to convert
     * @return the line that represents the accessory in the file
     */
    private String toLine(Accessory accessory) {
        String consoleIds = String.join(",", accessory.getCompatibleConsoleIds());

        if (accessory instanceof Controller controller) {
            return "CONTROLLER|" + controller.getId() + "|" + controller.getTitle() + "|"
                    + controller.getPrice() + "|" + controller.getStock() + "|"
                    + controller.getConnectionType() + "|" + consoleIds;
        }
        if (accessory instanceof Cable cable) {
            return "CABLE|" + cable.getId() + "|" + cable.getTitle() + "|"
                    + cable.getPrice() + "|" + cable.getStock() + "|"
                    + cable.getLengthInMeters() + "|" + cable.getConnectorType() + "|"
                    + consoleIds;
        }
        Memory memory = (Memory) accessory;
        return "MEMORY|" + memory.getId() + "|" + memory.getTitle() + "|"
                + memory.getPrice() + "|" + memory.getStock() + "|"
                + memory.getCapacityInGigabytes() + "|" + memory.getMemoryType() + "|"
                + consoleIds;
    }

    /**
     * Rebuilds an accessory from a pipe-delimited line, using the leading
     * type token to decide which concrete subclass to create. The split
     * uses a limit of -1 so an empty compatible-consoles field at the end
     * of the line is not discarded.
     *
     * @param line line read from the file
     * @return the accessory represented by the line
     */
    private Accessory fromLine(String line) {
        String[] parts = line.split("\\|", -1);
        String type = parts[0];
        String id = parts[1];
        String title = parts[2];
        double price = Double.parseDouble(parts[3]);
        int stock = Integer.parseInt(parts[4]);

        Accessory accessory;
        String consoleIdsField;

        switch (type) {
            case "CONTROLLER" -> {
                accessory = new Controller(id, title, price, stock, parts[5]);
                consoleIdsField = parts[6];
            }
            case "CABLE" -> {
                accessory = new Cable(id, title, price, stock,
                        Double.parseDouble(parts[5]), parts[6]);
                consoleIdsField = parts[7];
            }
            case "MEMORY" -> {
                accessory = new Memory(id, title, price, stock,
                        Integer.parseInt(parts[5]), parts[6]);
                consoleIdsField = parts[7];
            }
            default -> throw new IllegalArgumentException("Unknown accessory type: " + type);
        }

        if (!consoleIdsField.isBlank()) {
            for (String consoleId : consoleIdsField.split(",")) {
                accessory.addCompatibleConsole(consoleId);
            }
        }
        return accessory;
    }
}
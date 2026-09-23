package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
}
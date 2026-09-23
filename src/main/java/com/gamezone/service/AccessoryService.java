package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the business rules related to accessories. It receives the
 * repository through its constructor, loads the stored accessories once,
 * and persists the full list after every change.
 */
public class AccessoryService {

    private final AccessoryRepository repository;
    private List<Accessory> accessories;

    /**
     * Creates the accessory service and loads previously stored
     * accessories from disk.
     *
     * @param repository repository used to persist accessories
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = repository.loadAll();
    }

    /**
     * Registers a new controller and persists the updated list.
     *
     * @param id unique identifier of the accessory
     * @param title commercial name of the accessory
     * @param price unit price
     * @param stock available units in inventory
     * @param connectionType physical connection type (for example "USB")
     * @return the registered controller
     */
    public Controller registerController(String id, String title, double price, int stock,
            String connectionType) {
        Controller controller = new Controller(id, title, price, stock, connectionType);
        accessories.add(controller);
        repository.saveAll(accessories);
        return controller;
    }

    /**
     * Registers a new cable and persists the updated list.
     *
     * @param id unique identifier of the accessory
     * @param title commercial name of the accessory
     * @param price unit price
     * @param stock available units in inventory
     * @param lengthInMeters cable length in meters
     * @param connectorType connector type on its ends (for example "HDMI")
     * @return the registered cable
     */
    public Cable registerCable(String id, String title, double price, int stock,
            double lengthInMeters, String connectorType) {
        Cable cable = new Cable(id, title, price, stock, lengthInMeters, connectorType);
        accessories.add(cable);
        repository.saveAll(accessories);
        return cable;
    }

    /**
     * Registers a new memory and persists the updated list.
     *
     * @param id unique identifier of the accessory
     * @param title commercial name of the accessory
     * @param price unit price
     * @param stock available units in inventory
     * @param capacityInGigabytes storage capacity in gigabytes
     * @param memoryType memory type (for example "MicroSD")
     * @return the registered memory
     */
    public Memory registerMemory(String id, String title, double price, int stock,
            int capacityInGigabytes, String memoryType) {
        Memory memory = new Memory(id, title, price, stock, capacityInGigabytes, memoryType);
        accessories.add(memory);
        repository.saveAll(accessories);
        return memory;
    }

    /**
     * Returns every registered accessory.
     *
     * @return the complete list of accessories
     */
    public List<Accessory> listAllAccessories() {
        return accessories;
    }

    /**
     * Returns the accessories that declare compatibility with the given
     * console.
     *
     * @param consoleId id of the console to check
     * @return list of accessories compatible with that console
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory.isCompatibleWith(consoleId)) {
                result.add(accessory);
            }
        }
        return result;
    }

    /**
     * Finds an accessory by its id.
     *
     * @param id accessory id
     * @return the accessory with the given id, or null if it does not exist
     */
    public Accessory findById(String id) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }
        return null;
    }

    /**
     * Checks whether an accessory exists and has enough units available.
     *
     * @param id accessory id
     * @param quantity quantity requested
     * @return true if the accessory exists and has enough stock
     */
    public boolean hasStock(String id, int quantity) {
        Accessory accessory = findById(id);
        return accessory != null && accessory.hasStock(quantity);
    }

    /**
     * Decreases the stock of an accessory, used when a sale consumes it,
     * and persists the updated list.
     *
     * @param accessoryId accessory id
     * @param quantity quantity to subtract
     */
    public void updateStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        if (accessory != null) {
            accessory.reduceStock(quantity);
            repository.saveAll(accessories);
        }
    }

    /**
     * Increases the stock of an accessory, used when a return brings it
     * back into inventory, and persists the updated list.
     *
     * @param accessoryId accessory id
     * @param quantity quantity to add back
     */
    public void restoreStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        if (accessory != null) {
            accessory.setStock(accessory.getStock() + quantity);
            repository.saveAll(accessories);
        }
    }
}
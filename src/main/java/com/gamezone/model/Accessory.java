package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic accessory sold by GameZone Unicesar. An accessory
 * extends Product to reuse its common attributes and behavior (id, title,
 * price, stock), and adds the list of consoles it is compatible with.
 * This class is abstract because a plain, unspecialized accessory does
 * not exist: every accessory is a controller, a cable or a memory.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    public Accessory(String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.compatibleConsoleIds = new ArrayList<>();
    }

    public List<String> getCompatibleConsoleIds() {
        return compatibleConsoleIds;
    }

    public void setCompatibleConsoleIds(List<String> compatibleConsoleIds) {
        this.compatibleConsoleIds = compatibleConsoleIds;
    }

    /**
     * Adds a console id to the list of consoles this accessory works with.
     */
    public void addCompatibleConsole(String consoleId) {
        this.compatibleConsoleIds.add(consoleId);
    }

    /**
     * Checks whether this accessory is compatible with the given console id.
     */
    public boolean isCompatibleWith(String consoleId) {
        return this.compatibleConsoleIds.contains(consoleId);
    }

    @Override
    public String getDescription() {
        return getTitle() + " [Accessory] - Price: " + getPrice()
                + ", Stock: " + getStock()
                + ", Compatible consoles: " + compatibleConsoleIds;
    }
}
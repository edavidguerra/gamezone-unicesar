package com.gamezone.model;

/**
 * Represents a game controller accessory. Adds the type of physical
 * connection it uses (for example "USB" or "Bluetooth").
 */
public class Controller extends Accessory {

    private String connectionType;

    public Controller(String id, String title, double price, int stock,
                       String connectionType) {
        super(id, title, price, stock);
        this.connectionType = connectionType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String getDescription() {
        return getTitle() + " [Controller] - Price: " + getPrice()
                + ", Stock: " + getStock()
                + ", Connection: " + connectionType
                + ", Compatible consoles: " + getCompatibleConsoleIds();
    }
}
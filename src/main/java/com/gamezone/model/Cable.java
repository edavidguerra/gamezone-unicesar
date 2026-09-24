package com.gamezone.model;

/**
 * Represents a cable accessory. Adds its length in meters and the type
 * of connector it has on its ends (for example "HDMI" or "USB-C").
 */
public class Cable extends Accessory {

    private double lengthInMeters;
    private String connectorType;

    public Cable(String id, String title, double price, int stock,
                 double lengthInMeters, String connectorType) {
        super(id, title, price, stock);
        this.lengthInMeters = lengthInMeters;
        this.connectorType = connectorType;
    }

    public double getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(double lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    @Override
    public String getDescription() {
        return getTitle() + " [Cable] - Price: " + getPrice()
                + ", Stock: " + getStock()
                + ", Length: " + lengthInMeters + "m"
                + ", Connector: " + connectorType
                + ", Compatible consoles: " + getCompatibleConsoleIds();
    }
}
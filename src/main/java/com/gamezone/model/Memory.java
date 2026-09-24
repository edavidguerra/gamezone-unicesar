package com.gamezone.model;

/**
* Represents a storage memory accessory. Adds its capacity in gigabytes
* and its memory type (for example "MicroSD" or "SSD").
*/

public class Memory extends Accessory {

    private int capacityInGigabytes;
    private String memoryType;

    public Memory(String id, String title, double price, int stock,
                int capacityInGigabytes, String memoryType) {
        super(id, title, price, stock);
        this.capacityInGigabytes = capacityInGigabytes;
        this.memoryType = memoryType;
}

public int getCapacityInGigabytes() {
    return capacityInGigabytes;
}

public void setCapacityInGigabytes(int capacityInGigabytes) {
    this.capacityInGigabytes = capacityInGigabytes;
}

public String getMemoryType() {
    return memoryType;
}

public void setMemoryType(String memoryType) {
    this.memoryType = memoryType;
}

@Override
public String getDescription() {
    return getTitle() + " [Memory] - Price: " + getPrice()
            + ", Stock: " + getStock()
            + ", Capacity: " + capacityInGigabytes + "GB"
            + ", Type: " + memoryType
            + ", Compatible consoles: " + getCompatibleConsoleIds();
    }
}

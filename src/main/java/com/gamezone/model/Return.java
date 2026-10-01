package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the return of one or more products from a previously
 * registered sale. The original sale reference has no setter because
 * a return can never be reassigned to a different sale once created.
 */
public class Return {
    private String id;
    private LocalDate date;
    private final Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;
    private double warrantyRefund;

    /**
     * Creates a return and calculates its refund amount.
     *
     * @param id               unique identifier of the return
     * @param date             date on which the return is registered
     * @param originalSale     sale the returned products belong to
     * @param returnedProducts products being returned (may be a subset of the sale)
     * @param reason           reason given by the customer
     * @throws IllegalArgumentException if any argument is missing or invalid
     */
    public Return(String id, LocalDate date, Sale originalSale,
                  List<Product> returnedProducts, String reason) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador de la devolucion es obligatorio.");
        }
        if (date == null) {
            throw new IllegalArgumentException("La fecha de la devolucion es obligatoria.");
        }
        if (originalSale == null) {
            throw new IllegalArgumentException("La devolucion debe referenciar una venta original.");
        }
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("Debe devolver al menos un producto.");
        }
        if (returnedProducts.contains(null)) {
            throw new IllegalArgumentException("La lista de productos devueltos contiene un producto invalido.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo de la devolucion es obligatorio.");
        }
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /** @return the identifier of this return */
    public String getId() { return id; }

    /** @return the date on which this return was registered */
    public LocalDate getDate() { return date; }

    /** @return the sale this return refers to (never reassigned) */
    public Sale getOriginalSale() { return originalSale; }

    /** @return the products returned, which may be a subset of the sale */
    public List<Product> getReturnedProducts() { return returnedProducts; }

    /** @return the reason given for the return */
    public String getReason() { return reason; }

    /** @return the amount refunded to the customer */
    public double getRefundAmount() { return refundAmount; }

    /** @return the part of the refund that comes from cancelled extended warranties */
    public double getWarrantyRefund() { return warrantyRefund; }

    /**
     * Adds the amount refunded for the cancelled warranties of the returned
     * consoles and recalculates the total refund.
     *
     * @param amount refundable warranty cost, must not be negative
     * @throws IllegalArgumentException if the amount is negative
     */
    public void addWarrantyRefund(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("El reembolso de garantia no puede ser negativo.");
        }
        this.warrantyRefund += amount;
        calculateRefundAmount();
    }

    /**
     * Calculates the refund amount. Each returned item is refunded
     * proportionally to the discount of the original sale:
     * price * (1 - discount / subtotal). The extended warranty cost is not
     * discounted; the refund for cancelled warranties is added on top.
     *
     * @return the refund amount, also stored in the refundAmount attribute
     */
    public double calculateRefundAmount() {
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += calculateItemRefund(product);
        }
        total += warrantyRefund;
        this.refundAmount = total;
        return total;
    }

    /**
     * Returns the fraction of the sale subtotal that was discounted:
     * discount / subtotal, or 0.0 when the sale had no discount.
     *
     * @return the discount rate of the original sale, between 0.0 and 1.0
     */
    public double getDiscountRate() {
        double subtotal = originalSale.calculateTotal();
        if (subtotal <= 0.0) {
            return 0.0;
        }
        return originalSale.getDiscountAmount() / subtotal;
    }

    /**
     * Calculates the part of an item's list price that was discounted in the
     * original sale.
     *
     * @param product returned item
     * @return proportional discount of the item's price
     */
    public double calculateItemDiscount(Product product) {
        return product.getPrice() * getDiscountRate();
    }

    /**
     * Calculates the amount refunded for one item: its list price minus its
     * proportional discount.
     *
     * @param product returned item
     * @return refund of the item: price * (1 - discount / subtotal)
     */
    public double calculateItemRefund(Product product) {
        return product.getPrice() - calculateItemDiscount(product);
    }

    /**
     * Builds a human readable return receipt in Spanish. For each item it
     * shows the list price, the proportional discount and the refunded
     * amount, followed by the reason and the total refund.
     *
     * @return the formatted receipt
     */
    public String generateReturnReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recibo de devolucion: ").append(id).append("\n");
        sb.append("Fecha: ").append(date).append("\n");
        sb.append("Venta original: ").append(originalSale.getId()).append("\n");
        sb.append("Productos devueltos:\n");
        for (Product product : returnedProducts) {
            sb.append("  ").append(product.getId()).append(" - ")
              .append(product.getTitle())
              .append(" | Precio de lista: ").append(product.getPrice())
              .append(" | Descuento proporcional: -").append(calculateItemDiscount(product))
              .append(" | Reembolso: ").append(calculateItemRefund(product)).append("\n");
        }
        if (warrantyRefund > 0) {
            sb.append("Reembolso por garantias canceladas: ").append(warrantyRefund).append("\n");
        }
        sb.append("Motivo: ").append(reason).append("\n");
        sb.append("Monto reembolsado: ").append(refundAmount);
        return sb.toString();
    }
}
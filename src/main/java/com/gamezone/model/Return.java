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

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public Sale getOriginalSale() { return originalSale; }
    public List<Product> getReturnedProducts() { return returnedProducts; }
    public String getReason() { return reason; }
    public double getRefundAmount() { return refundAmount; }

    /**
     * Calculates the refund amount by summing the list price of every
     * returned product. Adjusted later by ajuste A5 to be proportional
     * to any discount applied on the original sale.
     */
    public double calculateRefundAmount() {
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += product.getPrice();
        }
        this.refundAmount = total;
        return total;
    }

    /**
     * Builds a human-readable return receipt in Spanish.
     */
    public String generateReturnReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recibo de devolucion ").append(id).append("\n");
        sb.append("Venta original: ").append(originalSale.getId()).append("\n");
        sb.append("Motivo: ").append(reason).append("\n");
        sb.append("Monto reembolsado: ").append(refundAmount);
        return sb.toString();
    }
}
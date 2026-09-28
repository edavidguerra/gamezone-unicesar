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

    public Return(String id, LocalDate date, Sale originalSale,
                  List<Product> returnedProducts, String reason) {
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
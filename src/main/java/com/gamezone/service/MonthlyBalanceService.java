package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

import java.time.LocalDate;

/**
 * Computes the monthly report: sales, returns and the net balance.
 */
public class MonthlyBalanceService {

    private final SaleLookup saleLookup;
    private final ReturnService returnService;

    /**
     * Creates the service.
     *
     * @param saleLookup lookup used to read the sales
     * @param returnService service used to read the returns
     */
    public MonthlyBalanceService(SaleLookup saleLookup, ReturnService returnService) {
        this.saleLookup = saleLookup;
        this.returnService = returnService;
    }

    /**
     * Net monthly balance: total sales minus total returns for the given
     * month and year.
     *
     * @param month month of the year (1-12)
     * @param year four-digit year
     * @return net balance for the period
     * @throws InvalidDataException if the month or year is invalid
     */
    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(month, year);
    }

    /**
     * Total sales of the month, using the final total of each sale (with
     * discounts and extended warranty cost included).
     *
     * @param month month of the year (1-12)
     * @param year  four-digit year
     * @return sum of the final totals of the sales made in that month
     * @throws InvalidDataException if the month or year is invalid
     */
    public double calculateMonthlySales(int month, int year) {
        validatePeriod(month, year);
        double total = 0.0;
        for (Sale sale : saleLookup.listSales()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());
            if (saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                total += sale.getFinalTotal();
            }
        }
        return total;
    }

    /**
     * Total returns of the month, using the refunded amount of each return.
     *
     * @param month month of the year (1-12)
     * @param year  four-digit year
     * @return sum of the amounts refunded in that month
     * @throws InvalidDataException if the month or year is invalid
     */
    public double calculateMonthlyReturns(int month, int year) {
        validatePeriod(month, year);
        double total = 0.0;
        for (Return returnItem : returnService.viewAllReturns()) {
            if (returnItem.getDate().getMonthValue() == month
                    && returnItem.getDate().getYear() == year) {
                total += returnItem.getRefundAmount();
            }
        }
        return total;
    }

    private void validatePeriod(int month, int year) {
        if (month < 1 || month > 12) {
            throw new InvalidDataException("mes", "debe estar entre 1 y 12");
        }
        if (year < 1) {
            throw new InvalidDataException("anio", "debe ser positivo");
        }
    }
}
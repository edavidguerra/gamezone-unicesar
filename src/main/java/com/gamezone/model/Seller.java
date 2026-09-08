package com.gamezone.model;

/**
 * Represents a seller (store employee) identified by an employee code
 * and a work shift.
 */
public class Seller extends Person {
    private String employeeCode;
    private String shift;

    /**
     * Creates a new seller.
     *
     * @param id unique identifier of the person
     * @param name full name
     * @param phone contact phone number
     * @param employeeCode internal employee code
     * @param shift assigned work shift
     */
    public Seller(String id, String name, String phone, String employeeCode, String shift) {
        super(id, name, phone);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    @Override
    public String getRoleDescription() {
        return "Seller - Employee code: " + employeeCode + ", Shift: " + shift;
    }
}
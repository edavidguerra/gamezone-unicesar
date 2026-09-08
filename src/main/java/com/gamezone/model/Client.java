package com.gamezone.model;

/**
 * Represents a client of the store, identified by an email address
 * used to contact them.
 */
public class Client extends Person {
    private String email;

    /**
     * Creates a new client.
     *
     * @param id unique identifier of the person
     * @param name full name
     * @param phone contact phone number
     * @param email contact email address
     */
    public Client(String id, String name, String phone, String email) {
        super(id, name, phone);
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getRoleDescription() {
        return "Client - Email: " + email;
    }
}
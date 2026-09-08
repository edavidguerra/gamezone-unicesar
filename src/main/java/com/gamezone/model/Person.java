package com.gamezone.model;

/**
 * Represents a generic person related to GameZone Unicesar. This class
 * is abstract because the business never has a "person" without a
 * specific role: every person is either a client or a seller.
 */
public abstract class Person {
    private String id;
    private String name;
    private String phone;

    /**
     * Creates a new person with the attributes shared by every role.
     *
     * @param id unique identifier of the person
     * @param name full name
     * @param phone contact phone number
     */
    public Person(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Describes the specific role of this person (client or seller) and
     * its particular data. Every subclass must provide its own
     * implementation (polymorphism).
     *
     * @return a text description of the role of this person
     */
    public abstract String getRoleDescription();
// }
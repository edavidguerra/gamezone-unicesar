package com.gamezone.exceptions;

/**
 * Thrown when an operation violates a business rule, such as insufficient
 * stock or a duplicated client.
 */
public class BusinessRuleException extends GameZoneException {

    /**
     * @param message description of the violated rule
     */
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE");
    }
}
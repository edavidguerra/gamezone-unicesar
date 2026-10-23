package com.gamezone.exceptions;

/**
 * Thrown when an input value does not meet format, range or type criteria.
 */
public class InvalidDataException extends GameZoneException {

    /**
     * Creates the exception with a descriptive Spanish message.
     *
     * @param fieldName name of the invalid field
     * @param description what is wrong with the value
     */
    public InvalidDataException(String fieldName, String description) {
        super("El campo " + fieldName + " no es valido: " + description + ".", "INVALID_DATA");
    }
}
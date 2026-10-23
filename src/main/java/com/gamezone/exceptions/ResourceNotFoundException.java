package com.gamezone.exceptions;

/**
 * Thrown when a requested resource does not exist in the system.
 */
public class ResourceNotFoundException extends GameZoneException {

    /**
     * Creates the exception with a descriptive Spanish message.
     *
     * @param resourceType kind of resource searched (product, client, seller, sale)
     * @param identifier identifier that was searched
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("El recurso de tipo " + resourceType + " con identificador " + identifier
                + " no fue encontrado.", "RESOURCE_NOT_FOUND");
    }
}
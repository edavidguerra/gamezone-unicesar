package com.gamezone.exceptions;

/**
 * Thrown when reading or writing a data file fails.
 */
public class PersistenceException extends GameZoneException {

    /**
     * @param message context of the failure (file and operation)
     * @param cause original exception, kept for traceability
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE");
        initCause(cause);
    }
}
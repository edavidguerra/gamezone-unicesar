package com.gamezone.exceptions;

/**
 * Root of the GameZone exception hierarchy. Carries an error code that
 * identifies the category of the failure.
 */
public abstract class GameZoneException extends RuntimeException {

    private final String errorCode;

    /**
     * Creates an exception with a message and the default error code.
     *
     * @param message description of the problem
     */
    public GameZoneException(String message) {
        this(message, "GAMEZONE_ERROR");
    }

    /**
     * Creates an exception with a message and an explicit error code.
     *
     * @param message description of the problem
     * @param errorCode code that identifies the error category
     */
    public GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * @return the error code of this exception
     */
    public String getErrorCode() {
        return errorCode;
    }
}
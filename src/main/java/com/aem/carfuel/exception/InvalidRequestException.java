package com.aem.carfuel.exception;

/**
 * Exception thrown when invalid data is provided in a request.
 */
public class InvalidRequestException extends RuntimeException {
    
    /**
     * Constructs a new InvalidRequestException with a message.
     *
     * @param message the error message
     */
    public InvalidRequestException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidRequestException with a message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

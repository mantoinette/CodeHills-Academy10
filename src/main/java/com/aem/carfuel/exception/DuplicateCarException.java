package com.aem.carfuel.exception;

/**
 * Exception thrown when attempting to create a duplicate car.
 */
public class DuplicateCarException extends RuntimeException {
    
    /**
     * Constructs a new DuplicateCarException with a default message.
     *
     * @param brand the car brand
     * @param model the car model
     * @param year the manufacturing year
     */
    public DuplicateCarException(String brand, String model, Integer year) {
        super(String.format("Car already exists: %s %s (%d)", brand, model, year));
    }
    
    /**
     * Constructs a new DuplicateCarException with a custom message.
     *
     * @param message the custom error message
     */
    public DuplicateCarException(String message) {
        super(message);
    }
}

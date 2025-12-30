package com.aem.carfuel.exception;

/**
 * Exception thrown when a car is not found in the system.
 */
public class CarNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new CarNotFoundException with a default message.
     *
     * @param id the ID of the car that was not found
     */
    public CarNotFoundException(Long id) {
        super("Car not found with id: " + id);
    }
    
    /**
     * Constructs a new CarNotFoundException with a custom message.
     *
     * @param message the custom error message
     */
    public CarNotFoundException(String message) {
        super(message);
    }
}

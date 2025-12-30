package com.aem.carfuel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a single fuel refill entry for a car.
 * Tracks the amount of fuel, cost, odometer reading, and timestamp.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelEntry {
    /**
     * Unique identifier for this fuel entry
     */
    private Long id;
    
    /**
     * Amount of fuel in liters
     */
    private Double liters;
    
    /**
     * Total cost of the fuel purchase
     */
    private Double price;
    
    /**
     * Odometer reading at the time of refill (in kilometers)
     */
    private Integer odometer;
    
    /**
     * Timestamp when the fuel entry was recorded
     */
    private LocalDateTime timestamp;
}

package com.aem.carfuel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a car in the system.
 * Tracks basic car information and maintains a history of fuel entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    /**
     * Unique identifier for this car
     */
    private Long id;
    
    /**
     * Car manufacturer brand (e.g., Toyota, BMW)
     */
    private String brand;
    
    /**
     * Car model name (e.g., Corolla, X5)
     */
    private String model;
    
    /**
     * Manufacturing year
     */
    private Integer year;
    
    /**
     * List of all fuel entries for this car
     */
    @Builder.Default
    private List<FuelEntry> fuelEntries = new ArrayList<>();
    
    /**
     * Timestamp when the car was added to the system
     */
    private LocalDateTime createdAt;
    
    /**
     * Helper method to add a fuel entry to this car
     */
    public void addFuelEntry(FuelEntry entry) {
        if (this.fuelEntries == null) {
            this.fuelEntries = new ArrayList<>();
        }
        this.fuelEntries.add(entry);
    }
}

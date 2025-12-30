package com.aem.carfuel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for fuel statistics.
 * Provides aggregated information about a car's fuel consumption.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelStats {
    /**
     * Total fuel consumed in liters
     */
    private Double totalFuel;
    
    /**
     * Total money spent on fuel
     */
    private Double totalCost;
    
    /**
     * Average fuel consumption in liters per 100 kilometers.
     * Calculated as: (totalFuel / distanceTraveled) * 100
     * Returns 0.0 if calculation is not possible (insufficient data)
     */
    private Double avgConsumption;
    
    /**
     * Total number of fuel entries recorded
     */
    private Integer entriesCount;
}

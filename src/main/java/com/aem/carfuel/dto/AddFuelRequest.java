package com.aem.carfuel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a fuel entry to a car.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFuelRequest {
    
    /**
     * Amount of fuel in liters (required, must be positive)
     */
    @NotNull(message = "Liters is required")
    @Positive(message = "Liters must be positive")
    private Double liters;
    
    /**
     * Total cost of the fuel purchase (required, must be positive)
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    /**
     * Odometer reading in kilometers (required, must be at least 0)
     */
    @NotNull(message = "Odometer is required")
    @Min(value = 0, message = "Odometer must be at least 0")
    private Integer odometer;
}

package com.aem.carfuel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new car.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest {
    
    /**
     * Car manufacturer brand (required)
     */
    @NotBlank(message = "Brand is required")
    private String brand;
    
    /**
     * Car model name (required)
     */
    @NotBlank(message = "Model is required")
    private String model;
    
    /**
     * Manufacturing year (required, must be at least 1900)
     */
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be at least 1900")
    private Integer year;
}

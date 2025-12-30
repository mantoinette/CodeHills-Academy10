package com.aem.carfuel.service;

import com.aem.carfuel.exception.CarNotFoundException;
import com.aem.carfuel.exception.DuplicateCarException;
import com.aem.carfuel.exception.InvalidRequestException;
import com.aem.carfuel.model.Car;
import com.aem.carfuel.model.FuelEntry;
import com.aem.carfuel.model.FuelStats;
import com.aem.carfuel.storage.InMemoryCarStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service layer for car and fuel management business logic.
 * Handles car creation, fuel entry management, and statistics calculation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {
    
    private final InMemoryCarStorage storage;
    
    /**
     * Create a new car in the system.
     *
     * @param brand the car brand
     * @param model the car model
     * @param year the manufacturing year
     * @return the created car with assigned ID
     * @throws DuplicateCarException if a car with same brand, model, and year already exists
     */
    public Car createCar(String brand, String model, Integer year) {
        log.info("Creating new car: {} {} ({})", brand, model, year);
        
        // Check for duplicate car
        if (storage.existsByBrandModelYear(brand, model, year)) {
            log.warn("Duplicate car creation attempt: {} {} ({})", brand, model, year);
            throw new DuplicateCarException(brand, model, year);
        }
        
        Car car = Car.builder()
                .brand(brand)
                .model(model)
                .year(year)
                .createdAt(LocalDateTime.now())
                .build();
        
        Car savedCar = storage.save(car);
        log.info("Car created with ID: {}", savedCar.getId());
        
        return savedCar;
    }
    
    /**
     * Get all cars in the system.
     *
     * @return list of all cars
     */
    public List<Car> getAllCars() {
        log.info("Fetching all cars");
        return storage.findAll();
    }
    
    /**
     * Get a car by its ID.
     *
     * @param id the car ID
     * @return the car
     * @throws CarNotFoundException if car not found
     */
    public Car getCarById(Long id) {
        log.info("Fetching car with ID: {}", id);
        return storage.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }
    
    /**
     * Add a fuel entry to a car.
     *
     * @param carId the car ID
     * @param liters the amount of fuel in liters
     * @param price the total cost
     * @param odometer the odometer reading in kilometers
     * @return the updated car
     * @throws CarNotFoundException if car not found
     * @throws InvalidRequestException if odometer reading is less than previous readings
     */
    public Car addFuelEntry(Long carId, Double liters, Double price, Integer odometer) {
        log.info("Adding fuel entry to car {}: {} liters, {} price, {} km", 
                 carId, liters, price, odometer);
        
        Car car = getCarById(carId);
        
        // Validate odometer reading - should not decrease
        if (!car.getFuelEntries().isEmpty()) {
            Integer maxOdometer = car.getFuelEntries().stream()
                    .map(FuelEntry::getOdometer)
                    .max(Integer::compareTo)
                    .orElse(0);
            
            if (odometer < maxOdometer) {
                String message = String.format(
                    "Invalid odometer reading: %d km. Cannot be less than previous reading: %d km",
                    odometer, maxOdometer
                );
                log.warn("Invalid fuel entry for car {}: {}", carId, message);
                throw new InvalidRequestException(message);
            }
        }
        
        FuelEntry entry = FuelEntry.builder()
                .id(storage.generateFuelId())
                .liters(liters)
                .price(price)
                .odometer(odometer)
                .timestamp(LocalDateTime.now())
                .build();
        
        car.addFuelEntry(entry);
        storage.save(car);
        
        log.info("Fuel entry added successfully. Car now has {} entries", 
                 car.getFuelEntries().size());
        
        return car;
    }
    
    /**
     * Calculate fuel statistics for a car.
     * 
     * Algorithm:
     * - Total Fuel: Sum of all fuel entries (liters)
     * - Total Cost: Sum of all fuel entries (price)
     * - Average Consumption: (Total Fuel / Distance) × 100
     *   where Distance = Last Odometer - First Odometer
     * 
     * Special Cases:
     * - 0 or 1 entry: avgConsumption = 0.0 (insufficient data)
     * - Invalid distance (≤0): avgConsumption = 0.0
     *
     * @param carId the car ID
     * @return fuel statistics
     * @throws CarNotFoundException if car not found
     */
    public FuelStats calculateStats(Long carId) {
        log.info("Calculating fuel statistics for car {}", carId);
        
        Car car = getCarById(carId);
        List<FuelEntry> entries = car.getFuelEntries();
        
        if (entries == null || entries.isEmpty()) {
            log.info("No fuel entries found for car {}", carId);
            return FuelStats.builder()
                    .totalFuel(0.0)
                    .totalCost(0.0)
                    .avgConsumption(0.0)
                    .entriesCount(0)
                    .build();
        }
        
        // Calculate total fuel and cost
        double totalFuel = entries.stream()
                .mapToDouble(FuelEntry::getLiters)
                .sum();
        
        double totalCost = entries.stream()
                .mapToDouble(FuelEntry::getPrice)
                .sum();
        
        // Calculate average consumption
        double avgConsumption = 0.0;
        
        if (entries.size() >= 2) {
            // Sort entries by timestamp to get first and last odometer readings
            List<FuelEntry> sortedEntries = entries.stream()
                    .sorted(Comparator.comparing(FuelEntry::getTimestamp))
                    .toList();
            
            Integer firstOdometer = sortedEntries.get(0).getOdometer();
            Integer lastOdometer = sortedEntries.get(sortedEntries.size() - 1).getOdometer();
            
            int distance = lastOdometer - firstOdometer;
            
            if (distance > 0) {
                // Average consumption in liters per 100 km
                avgConsumption = (totalFuel / distance) * 100;
                log.info("Distance traveled: {} km, Avg consumption: {:.2f} L/100km", 
                         distance, avgConsumption);
            } else {
                log.warn("Invalid distance ({} km) for car {}. Cannot calculate avg consumption.", 
                         distance, carId);
            }
        } else {
            log.info("Only {} entry found for car {}. Cannot calculate avg consumption.", 
                     entries.size(), carId);
        }
        
        FuelStats stats = FuelStats.builder()
                .totalFuel(totalFuel)
                .totalCost(totalCost)
                .avgConsumption(avgConsumption)
                .entriesCount(entries.size())
                .build();
        
        log.info("Statistics calculated: Total Fuel={} L, Total Cost={}, " +
                 "Avg Consumption={} L/100km, Entries={}",
                 totalFuel, totalCost, avgConsumption, entries.size());
        
        return stats;
    }
}

package com.aem.carfuel.storage;

import com.aem.carfuel.model.Car;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory storage for cars.
 * Uses ConcurrentHashMap for thread safety and AtomicLong for ID generation.
 * Data is not persisted - all data is lost when the application restarts.
 */
@Component
public class InMemoryCarStorage {
    /**
     * Thread-safe map to store cars by their ID
     */
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    
    /**
     * Thread-safe counter for generating unique car IDs
     */
    private final AtomicLong carIdGenerator = new AtomicLong(1);
    
    /**
     * Thread-safe counter for generating unique fuel entry IDs
     */
    private final AtomicLong fuelIdGenerator = new AtomicLong(1);
    
    /**
     * Save or update a car in storage.
     * If the car doesn't have an ID, a new one will be generated.
     *
     * @param car the car to save
     * @return the saved car with its ID
     */
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(generateCarId());
        }
        cars.put(car.getId(), car);
        return car;
    }
    
    /**
     * Find a car by its ID.
     *
     * @param id the car ID
     * @return an Optional containing the car if found, empty otherwise
     */
    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(cars.get(id));
    }
    
    /**
     * Get all cars in storage.
     *
     * @return a list of all cars
     */
    public List<Car> findAll() {
        return new ArrayList<>(cars.values());
    }
    
    /**
     * Check if a car exists with the given ID.
     *
     * @param id the car ID to check
     * @return true if the car exists, false otherwise
     */
    public boolean existsById(Long id) {
        return cars.containsKey(id);
    }
    
    /**
     * Generate a new unique car ID.
     *
     * @return a new car ID
     */
    public Long generateCarId() {
        return carIdGenerator.getAndIncrement();
    }
    
    /**
     * Generate a new unique fuel entry ID.
     *
     * @return a new fuel entry ID
     */
    public Long generateFuelId() {
        return fuelIdGenerator.getAndIncrement();
    }
    
    /**
     * Delete all cars from storage.
     * Useful for testing purposes.
     */
    public void deleteAll() {
        cars.clear();
    }
    
    /**
     * Get the total number of cars in storage.
     *
     * @return the number of cars
     */
    public int count() {
        return cars.size();
    }
    
    /**
     * Check if a car exists with the given brand, model, and year.
     *
     * @param brand the car brand
     * @param model the car model
     * @param year the manufacturing year
     * @return true if a car exists with these attributes, false otherwise
     */
    public boolean existsByBrandModelYear(String brand, String model, Integer year) {
        return cars.values().stream()
            .anyMatch(car -> car.getBrand().equalsIgnoreCase(brand)
                && car.getModel().equalsIgnoreCase(model)
                && car.getYear().equals(year));
    }
}

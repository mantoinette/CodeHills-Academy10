package com.aem.carfuel.controller;

import com.aem.carfuel.dto.AddFuelRequest;
import com.aem.carfuel.dto.CreateCarRequest;
import com.aem.carfuel.model.Car;
import com.aem.carfuel.model.FuelStats;
import com.aem.carfuel.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for car and fuel management.
 * Provides endpoints for CRUD operations on cars and fuel entries.
 */
@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
public class CarController {
    
    private final CarService carService;
    
    /**
     * Create a new car.
     * 
     * POST /api/cars
     * 
     * Request Body:
     * {
     *   "brand": "Toyota",
     *   "model": "Corolla",
     *   "year": 2018
     * }
     *
     * @param request the car creation request
     * @return the created car with status 201
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody CreateCarRequest request) {
        log.info("REST API: Creating car - {} {} ({})", 
                 request.getBrand(), request.getModel(), request.getYear());
        
        Car car = carService.createCar(
            request.getBrand(),
            request.getModel(),
            request.getYear()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }
    
    /**
     * Get all cars in the system.
     * 
     * GET /api/cars
     *
     * @return list of all cars with status 200
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        log.info("REST API: Fetching all cars");
        
        List<Car> cars = carService.getAllCars();
        
        return ResponseEntity.ok(cars);
    }
    
    /**
     * Get a specific car by ID.
     * 
     * GET /api/cars/{id}
     *
     * @param id the car ID
     * @return the car with status 200, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        log.info("REST API: Fetching car with ID: {}", id);
        
        Car car = carService.getCarById(id);
        
        return ResponseEntity.ok(car);
    }
    
    /**
     * Add a fuel entry to a car.
     * 
     * POST /api/cars/{id}/fuel
     * 
     * Request Body:
     * {
     *   "liters": 40.0,
     *   "price": 52.5,
     *   "odometer": 45000
     * }
     *
     * @param id the car ID
     * @param request the fuel entry request
     * @return the updated car with status 200, or 404 if car not found
     */
    @PostMapping("/{id}/fuel")
    public ResponseEntity<Car> addFuelEntry(
            @PathVariable Long id,
            @Valid @RequestBody AddFuelRequest request) {
        
        log.info("REST API: Adding fuel entry to car {} - {} liters, {} price, {} km",
                 id, request.getLiters(), request.getPrice(), request.getOdometer());
        
        Car car = carService.addFuelEntry(
            id,
            request.getLiters(),
            request.getPrice(),
            request.getOdometer()
        );
        
        return ResponseEntity.ok(car);
    }
    
    /**
     * Get fuel statistics for a car.
     * 
     * GET /api/cars/{id}/fuel/stats
     *
     * @param id the car ID
     * @return fuel statistics with status 200, or 404 if car not found
     */
    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<FuelStats> getFuelStats(@PathVariable Long id) {
        log.info("REST API: Fetching fuel statistics for car {}", id);
        
        FuelStats stats = carService.calculateStats(id);
        
        return ResponseEntity.ok(stats);
    }
}

package com.aem.carfuel.servlet;

import com.aem.carfuel.exception.CarNotFoundException;
import com.aem.carfuel.model.FuelStats;
import com.aem.carfuel.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Manual servlet implementation for retrieving fuel statistics.
 * Demonstrates traditional servlet handling without Spring MVC annotations.
 * 
 * Endpoint: GET /servlet/fuel-stats?carId={id}
 * 
 * This servlet uses the same CarService as the REST controller,
 * demonstrating code reuse between traditional servlets and Spring REST APIs.
 */
@Slf4j
public class FuelStatsServlet extends HttpServlet {
    
    private CarService carService;
    private ObjectMapper objectMapper;
    
    /**
     * Setter for dependency injection from ServletConfig
     */
    public void setCarService(CarService carService) {
        this.carService = carService;
    }
    
    /**
     * Initialize the servlet with an ObjectMapper for JSON serialization
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDateTime
        log.info("FuelStatsServlet initialized");
    }
    
    /**
     * Handle GET requests to retrieve fuel statistics.
     * 
     * Query Parameters:
     * - carId (required): The ID of the car
     * 
     * Response:
     * - 200 OK: Returns FuelStats as JSON
     * - 400 Bad Request: If carId is missing or invalid
     * - 404 Not Found: If car doesn't exist
     * - 500 Internal Server Error: For unexpected errors
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        log.info("Servlet: Received request for fuel stats");
        
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Manual parameter extraction
            String carIdParam = request.getParameter("carId");
            
            // Manual validation
            if (carIdParam == null || carIdParam.trim().isEmpty()) {
                log.warn("Servlet: Missing carId parameter");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\":\"carId query parameter is required\",\"status\":400}");
                return;
            }
            
            // Parse carId
            Long carId;
            try {
                carId = Long.parseLong(carIdParam);
            } catch (NumberFormatException e) {
                log.warn("Servlet: Invalid carId format: {}", carIdParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\":\"carId must be a valid number\",\"status\":400}");
                return;
            }
            
            log.info("Servlet: Calculating stats for car {}", carId);
            
            // Use service layer (same as REST controller)
            FuelStats stats = carService.calculateStats(carId);
            
            // Manual JSON serialization
            String jsonResponse = objectMapper.writeValueAsString(stats);
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(jsonResponse);
            
            log.info("Servlet: Successfully returned stats for car {}", carId);
            
        } catch (CarNotFoundException e) {
            log.warn("Servlet: Car not found - {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write(String.format("{\"message\":\"%s\",\"status\":404}", e.getMessage()));
            
        } catch (Exception e) {
            log.error("Servlet: Unexpected error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(String.format("{\"message\":\"Internal server error: %s\",\"status\":500}", 
                                   e.getMessage()));
        } finally {
            out.flush();
        }
    }
    
    /**
     * Provide information about supported methods
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Only GET method is supported\",\"status\":405}");
    }
}

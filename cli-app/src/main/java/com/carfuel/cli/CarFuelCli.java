package com.carfuel.cli;

import com.carfuel.cli.command.CommandParser;
import com.carfuel.cli.http.ApiClient;
import com.carfuel.cli.server.ServerManager;

/**
 * Main entry point for the standalone Car Fuel CLI application.
 * This is completely separate from the Spring Boot backend.
 * 
 * Usage Examples:
 *   java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018
 *   java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
 *   java -jar carfuel-cli-1.0.0.jar fuel-stats --carId 1
 */
public class CarFuelCli {
    
    private static final String API_BASE_URL = "http://localhost:8080";
    private static final String SERVER_JAR_NAME = "carfuel-0.0.1-SNAPSHOT.jar";
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }
        
        try {
            // Initialize components
            ServerManager serverManager = new ServerManager(API_BASE_URL, SERVER_JAR_NAME);
            ApiClient apiClient = new ApiClient(API_BASE_URL);
            CommandParser commandParser = new CommandParser(apiClient);
            
            // Check if server is running
            if (!serverManager.isServerRunning()) {
                System.out.println("Backend server is not running.");
                
                if (serverManager.canStartServer()) {
                    System.out.print("Start the server now? (y/n): ");
                    
                    try {
                        int ch = System.in.read();
                        if (ch == 'y' || ch == 'Y') {
                            System.out.println("Starting backend server...");
                            serverManager.startServer();
                            System.out.println("Server started successfully!");
                        } else {
                            System.err.println("Server is required. Exiting.");
                            System.exit(1);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to start server: " + e.getMessage());
                        System.exit(1);
                    }
                } else {
                    System.err.println("Server JAR not found. Please ensure " + SERVER_JAR_NAME + 
                            " is in the current directory or ../build/libs/");
                    System.exit(1);
                }
            }
            
            // Parse and execute command
            String command = args[0];
            commandParser.executeCommand(command, args);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            if (System.getProperty("debug") != null) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    private static void printUsage() {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Car Fuel Management System - CLI                       ║");
        System.out.println("║              Standalone Java Application                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Usage: java -jar carfuel-cli-1.0.0.jar <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println();
        System.out.println("  create-car     Create a new car");
        System.out.println("    --brand <name>       Car brand (required)");
        System.out.println("    --model <name>       Car model (required)");
        System.out.println("    --year <yyyy>        Manufacturing year (required)");
        System.out.println();
        System.out.println("    Example:");
        System.out.println("      java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018");
        System.out.println();
        System.out.println("  add-fuel       Add fuel entry to a car");
        System.out.println("    --carId <id>         Car ID (required)");
        System.out.println("    --liters <amount>    Fuel in liters (required)");
        System.out.println("    --price <cost>       Total cost (required)");
        System.out.println("    --odometer <km>      Odometer reading (required)");
        System.out.println();
        System.out.println("    Example:");
        System.out.println("      java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000");
        System.out.println();
        System.out.println("  fuel-stats     View fuel statistics");
        System.out.println("    --carId <id>         Car ID (required)");
        System.out.println();
        System.out.println("    Example:");
        System.out.println("      java -jar carfuel-cli-1.0.0.jar fuel-stats --carId 1");
        System.out.println();
        System.out.println("  list-cars      List all cars");
        System.out.println();
        System.out.println("    Example:");
        System.out.println("      java -jar carfuel-cli-1.0.0.jar list-cars");
        System.out.println();
    }
}

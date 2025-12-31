package com.carfuel.cli.command;

import com.carfuel.cli.http.ApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses and executes CLI commands.
 */
public class CommandParser {
    
    private final ApiClient apiClient;
    private final Gson gson;
    
    public CommandParser(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * Execute a command with arguments.
     */
    public void executeCommand(String command, String[] allArgs) throws Exception {
        Map<String, String> params = parseArguments(allArgs);
        
        switch (command) {
            case "create-car":
                createCar(params);
                break;
            case "add-fuel":
                addFuel(params);
                break;
            case "fuel-stats":
                fuelStats(params);
                break;
            case "list-cars":
                listCars();
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command + 
                        ". Use: create-car, add-fuel, fuel-stats, or list-cars");
        }
    }
    
    private void createCar(Map<String, String> params) throws Exception {
        String brand = requireParam(params, "brand");
        String model = requireParam(params, "model");
        String year = requireParam(params, "year");
        
        JsonObject json = new JsonObject();
        json.addProperty("brand", brand);
        json.addProperty("model", model);
        json.addProperty("year", Integer.parseInt(year));
        
        HttpResponse<String> response = apiClient.post("/api/cars", gson.toJson(json));
        
        if (response.statusCode() == 201) {
            JsonObject car = gson.fromJson(response.body(), JsonObject.class);
            System.out.println("✅ Car created successfully!");
            System.out.println("   ID:    " + car.get("id").getAsLong());
            System.out.println("   Brand: " + car.get("brand").getAsString());
            System.out.println("   Model: " + car.get("model").getAsString());
            System.out.println("   Year:  " + car.get("year").getAsInt());
        } else {
            handleError(response);
        }
    }
    
    private void addFuel(Map<String, String> params) throws Exception {
        String carId = requireParam(params, "carId");
        String liters = requireParam(params, "liters");
        String price = requireParam(params, "price");
        String odometer = requireParam(params, "odometer");
        
        JsonObject json = new JsonObject();
        json.addProperty("liters", Double.parseDouble(liters));
        json.addProperty("price", Double.parseDouble(price));
        json.addProperty("odometer", Integer.parseInt(odometer));
        
        HttpResponse<String> response = apiClient.post("/api/cars/" + carId + "/fuel", gson.toJson(json));
        
        if (response.statusCode() == 200) {
            JsonObject car = gson.fromJson(response.body(), JsonObject.class);
            int entriesCount = car.getAsJsonArray("fuelEntries").size();
            System.out.println("Fuel entry added successfully!");
            System.out.println("   Car:           " + car.get("brand").getAsString() + " " + 
                             car.get("model").getAsString());
            System.out.println("   Total entries: " + entriesCount);
        } else {
            handleError(response);
        }
    }
    
    private void fuelStats(Map<String, String> params) throws Exception {
        String carId = requireParam(params, "carId");
        
        HttpResponse<String> response = apiClient.get("/api/cars/" + carId + "/fuel/stats");
        
        if (response.statusCode() == 200) {
            JsonObject stats = gson.fromJson(response.body(), JsonObject.class);
            
            double totalFuel = stats.get("totalFuel").getAsDouble();
            double totalCost = stats.get("totalCost").getAsDouble();
            double avgConsumption = stats.get("avgConsumption").getAsDouble();
            int entriesCount = stats.get("entriesCount").getAsInt();
            
            System.out.println();
            System.out.println("═══════════════════════════════════════");
            System.out.println("        Fuel Statistics                ");
            System.out.println("═══════════════════════════════════════");
            System.out.println();
            System.out.printf("Total fuel:          %.1f L%n", totalFuel);
            System.out.printf("Total cost:          %.2f%n", totalCost);
            System.out.printf("Average consumption: %.1f L/100km%n", avgConsumption);
            System.out.printf("Entries count:       %d%n", entriesCount);
            System.out.println();
        } else {
            handleError(response);
        }
    }
    
    private void listCars() throws Exception {
        HttpResponse<String> response = apiClient.get("/api/cars");
        
        if (response.statusCode() == 200) {
            JsonArray cars = gson.fromJson(response.body(), JsonArray.class);
            
            if (cars.size() == 0) {
                System.out.println("No cars registered yet.");
                System.out.println("Create one with: java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018");
                return;
            }
            
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("                    Registered Cars                        ");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println();
            System.out.printf("%-5s %-15s %-15s %-6s %-8s%n", "ID", "Brand", "Model", "Year", "Entries");
            System.out.println("───────────────────────────────────────────────────────────");
            
            for (int i = 0; i < cars.size(); i++) {
                JsonObject car = cars.get(i).getAsJsonObject();
                System.out.printf("%-5d %-15s %-15s %-6d %-8d%n",
                    car.get("id").getAsLong(),
                    car.get("brand").getAsString(),
                    car.get("model").getAsString(),
                    car.get("year").getAsInt(),
                    car.getAsJsonArray("fuelEntries").size()
                );
            }
            System.out.println();
        } else {
            handleError(response);
        }
    }
    
    private Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();
        
        for (int i = 1; i < args.length; i += 2) {
            if (i + 1 < args.length && args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = args[i + 1];
                params.put(key, value);
            }
        }
        
        return params;
    }
    
    private String requireParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: --" + key);
        }
        return value;
    }
    
    private void handleError(HttpResponse<String> response) {
        try {
            JsonObject error = gson.fromJson(response.body(), JsonObject.class);
            String message = error.has("message") ? error.get("message").getAsString() : "Unknown error";
            System.err.println("Error (" + response.statusCode() + "): " + message);
        } catch (Exception e) {
            System.err.println("Error (" + response.statusCode() + "): " + response.body());
        }
    }
}

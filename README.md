# Car Fuel Management System

A Spring Boot REST API application for tracking vehicle fuel consumption and performance metrics. This system demonstrates both modern Spring MVC REST controllers and traditional servlet implementations working together.

---

## 📚 Quick Links

- 🚀 **First Time?** → [Quick Start (5 minutes)](#-quick-start-5-minutes)
- 🔍 **Need API Reference?** → [API-REFERENCE.md](API-REFERENCE.md) - Quick endpoint lookup
- 💻 **Using CLI?** → [cli-app/README.md](cli-app/README.md) - CLI application guide
- 🏗️ **Architecture Details?** → [IMPLEMENTATION.md](IMPLEMENTATION.md) - Technical deep dive

## 🧭 Onboarding Map

| If you need… | Go here |
|--------------|---------|
| Fast start | [Quick Start (5 minutes)](#-quick-start-5-minutes) |
| Endpoint details | [API-REFERENCE.md](API-REFERENCE.md) |
| Architecture & rationale | [IMPLEMENTATION.md](IMPLEMENTATION.md) |
| CLI usage | [cli-app/README.md](cli-app/README.md) |

**Pick a path:**
- Want to run everything now? Follow Quick Start below, then smoke-test with the `curl` commands.
- Prefer the CLI first? Build `cli-app` and point it at `http://localhost:8080`.
- Exploring the code? Skim the Project Structure section, then jump into `CarController` and `CarService`.

**This document** contains complete API documentation with examples and the main onboarding flow.

---

## 🚀 Quick Start (5 minutes)

> macOS/Linux commands use `./gradlew`. On Windows, run `gradlew.bat`.

1. **Check prerequisites**
  ```bash
  java -version    # need Java 21+
  ./gradlew --version
  ```
2. **Run the backend**
  ```bash
  ./gradlew build
  ./gradlew bootRun
  ```
  Server starts on http://localhost:8080
3. **Smoke test the API**
  ```bash
  curl -X POST http://localhost:8080/api/cars \
    -H "Content-Type: application/json" \
    -d '{"brand":"Toyota","model":"Corolla","year":2018}'
  curl http://localhost:8080/api/cars
  ```
4. **(Optional) Try the CLI**
  ```bash
  cd cli-app
  ./gradlew build
  java -jar build/libs/carfuel-cli-1.0.0.jar list-cars
  ```

**Two apps:** `carfuel` (Spring Boot backend) and `cli-app` (standalone CLI) are separate projects; the CLI talks to the backend over HTTP.

---

## 🚀 Features

- ✅ **Car Management**: Create and track multiple vehicles
- ⛽ **Fuel Entry Tracking**: Record fuel purchases with odometer readings
- 📊 **Statistics Calculation**: Automatic calculation of fuel consumption metrics
- 🔄 **Dual API Implementation**: REST API + Traditional Servlet
- 💾 **In-Memory Storage**: Thread-safe storage using ConcurrentHashMap
- ⚡ **Real-time Calculations**: Average fuel consumption (L/100km)
- 🛡️ **Error Handling**: Comprehensive validation and exception handling

## 📋 Prerequisites

- **Java**: JDK 21 or higher
- **Gradle**: 8.x (wrapper included)
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse (optional)

## 🏗️ Project Structure

```
src/main/java/com/aem/carfuel/
├── CarfuelApplication.java          # Spring Boot entry point
├── config/
│   └── ServletConfig.java           # Manual servlet registration
├── controller/
│   └── CarController.java           # REST API endpoints
├── dto/
│   ├── AddFuelRequest.java          # Request DTO for fuel entries
│   └── CreateCarRequest.java        # Request DTO for car creation
├── exception/
│   ├── CarNotFoundException.java    # Custom exception
│   ├── ErrorResponse.java           # Error response DTO
│   └── GlobalExceptionHandler.java  # Global exception handling
├── model/
│   ├── Car.java                     # Car entity
│   ├── FuelEntry.java               # Fuel entry entity
│   └── FuelStats.java               # Statistics DTO
├── service/
│   └── CarService.java              # Business logic layer
├── servlet/
│   └── FuelStatsServlet.java        # Traditional servlet implementation
└── storage/
    └── InMemoryCarStorage.java      # In-memory data storage
```

## 🔧 Installation & Setup

### 1. Clone or Download the Project

```bash
cd /path/to/carfuel
```

### 2. Build the Project

```bash
# macOS/Linux
./gradlew clean build

# Windows (Command Prompt or PowerShell)
gradlew.bat clean build
```

### 3. Run the Application

```bash
# macOS/Linux
./gradlew bootRun

# Windows (Command Prompt or PowerShell)
gradlew.bat bootRun
```

Or run the JAR file:

```bash
java -jar build/libs/carfuel-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## 📚 API Documentation

### REST API Endpoints

#### 1. Create a Car

**POST** `/api/cars`

Creates a new car in the system.

**Request Body:**
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": [],
  "createdAt": "2025-12-30T10:30:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}'
```

---

#### 2. Get All Cars

**GET** `/api/cars`

Retrieves all cars in the system.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2018,
    "fuelEntries": [],
    "createdAt": "2025-12-30T10:30:00"
  }
]
```

**cURL Example:**
```bash
curl http://localhost:8080/api/cars
```

---

#### 3. Get Car by ID

**GET** `/api/cars/{id}`

Retrieves a specific car by its ID.

**Response:** `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": [],
  "createdAt": "2025-12-30T10:30:00"
}
```

**cURL Example:**
```bash
curl http://localhost:8080/api/cars/1
```

---

#### 4. Add Fuel Entry

**POST** `/api/cars/{id}/fuel`

Adds a fuel entry to a specific car.

**Request Body:**
```json
{
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000
}
```

**Response:** `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": [
    {
      "id": 1,
      "liters": 40.0,
      "price": 52.5,
      "odometer": 45000,
      "timestamp": "2025-12-30T11:00:00"
    }
  ],
  "createdAt": "2025-12-30T10:30:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40.0,"price":52.5,"odometer":45000}'
```

---

#### 5. Get Fuel Statistics (REST)

**GET** `/api/cars/{id}/fuel/stats`

Calculates and returns fuel statistics for a car.

**Response:** `200 OK` or `404 Not Found`
```json
{
  "totalFuel": 80.0,
  "totalCost": 105.0,
  "avgConsumption": 8.0,
  "entriesCount": 2
}
```

**cURL Example:**
```bash
curl http://localhost:8080/api/cars/1/fuel/stats
```

---

### Traditional Servlet Endpoint

#### 6. Get Fuel Statistics (Servlet)

**GET** `/servlet/fuel-stats?carId={id}`

Alternative endpoint using traditional servlet implementation. Returns the same data as the REST endpoint but demonstrates manual servlet handling.

**Query Parameters:**
- `carId` (required): The car ID

**Response:** `200 OK`, `400 Bad Request`, or `404 Not Found`
```json
{
  "totalFuel": 80.0,
  "totalCost": 105.0,
  "avgConsumption": 8.0,
  "entriesCount": 2
}
```

**cURL Example:**
```bash
curl "http://localhost:8080/servlet/fuel-stats?carId=1"
```

---

## 📊 Fuel Statistics Calculation

The system calculates the following metrics:

### **Total Fuel**
Sum of all fuel entries in liters.

### **Total Cost**
Sum of all fuel purchase costs.

### **Average Consumption**
Calculated as: `(Total Fuel / Distance Traveled) × 100`

Where:
- Distance Traveled = Last Odometer Reading - First Odometer Reading

**Special Cases:**
- **0 or 1 entry**: `avgConsumption = 0.0` (insufficient data)
- **Invalid distance (≤0)**: `avgConsumption = 0.0` (odometer readings must increase)

**Example:**
- Entry 1: 40L at 45000 km
- Entry 2: 45L at 45500 km
- Entry 3: 42L at 46000 km

```
Total Fuel = 127L
Total Cost = Sum of all prices
Distance = 46000 - 45000 = 1000 km
Average Consumption = (127 / 1000) × 100 = 12.7 L/100km
```

---

## 🧪 Testing the Application

### Complete Test Sequence

> macOS/Linux: run as-is. On Windows, use Git Bash/WSL or translate each `curl` command accordingly.

```bash
# 1. Create a car
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}'

# 2. List all cars
curl http://localhost:8080/api/cars

# 3. Add first fuel entry
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40.0,"price":52.5,"odometer":45000}'

# 4. Add second fuel entry
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":45.0,"price":59.0,"odometer":45500}'

# 5. Get statistics via REST API
curl http://localhost:8080/api/cars/1/fuel/stats

# 6. Get statistics via Servlet
curl "http://localhost:8080/servlet/fuel-stats?carId=1"

# 7. Test 404 error handling
curl http://localhost:8080/api/cars/999/fuel/stats

# 8. Test validation error
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"","model":"Test","year":1800}'
```

---

## 🛡️ Error Handling

The application provides comprehensive error handling:

### Error Response Format
```json
{
  "message": "Error description",
  "status": 404,
  "timestamp": "2025-12-30T12:00:00"
}
```

### HTTP Status Codes

| Code | Description | Example |
|------|-------------|---------|
| 200 | Success | Data retrieved successfully |
| 201 | Created | Car created successfully |
| 400 | Bad Request | Invalid input data or validation failure |
| 404 | Not Found | Car with specified ID doesn't exist |
| 405 | Method Not Allowed | Using POST on servlet endpoint |
| 500 | Internal Server Error | Unexpected server error |

---

## 🔍 Key Technical Features

### Thread-Safe Storage
Uses `ConcurrentHashMap` and `AtomicLong` for thread-safe operations:
```java
private final Map<Long, Car> cars = new ConcurrentHashMap<>();
private final AtomicLong carIdGenerator = new AtomicLong(1);
```

### Validation
Input validation using Jakarta Bean Validation:
```java
@NotBlank(message = "Brand is required")
private String brand;

@Positive(message = "Liters must be positive")
private Double liters;
```

### Dual API Approach
- **REST Controller**: Modern Spring MVC approach with annotations
- **Traditional Servlet**: Manual request/response handling
- **Shared Service Layer**: Both approaches use the same `CarService`

### Lombok Integration
Reduces boilerplate code:
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car { ... }
```

---

## 📝 Architecture Decisions

### Why Both REST and Servlet?
The project demonstrates:
1. **Modern approach**: Spring MVC with `@RestController`
2. **Traditional approach**: Manual servlet implementation
3. **Code reuse**: Both share the same service layer

### Why In-Memory Storage?
- Simplifies development and testing
- No database setup required
- Demonstrates thread-safe collections
- Easy to extend with actual persistence

### Why Separate DTOs?
- Separates API contract from domain model
- Enables validation at API boundary
- Prevents exposing internal structure

---

## � Understanding Key Functions

### Where to Find Core Logic

#### 1. Car Creation & Management
**File:** [src/main/java/com/aem/carfuel/service/CarService.java](src/main/java/com/aem/carfuel/service/CarService.java)

```java
// Creates new car with duplicate checking
public Car createCar(CreateCarRequest request)

// Retrieves all cars
public List<Car> getAllCars()

// Finds specific car (throws exception if not found)
public Car getCarById(Long id)
```

**Key Features:**
- ✅ Duplicate car detection (same brand + model + year)
- ✅ Automatic ID generation
- ✅ Timestamp on creation

#### 2. Fuel Entry Management
**File:** [src/main/java/com/aem/carfuel/service/CarService.java](src/main/java/com/aem/carfuel/service/CarService.java)

```java
// Adds fuel entry with validation
public Car addFuelEntry(Long carId, AddFuelRequest request)
```

**Validation Rules:**
- ✅ Odometer can't decrease (prevents invalid data)
- ✅ Liters must be positive
- ✅ Automatic timestamp generation

#### 3. Statistics Calculation
**File:** [src/main/java/com/aem/carfuel/service/CarService.java](src/main/java/com/aem/carfuel/service/CarService.java)

```java
// Calculates fuel consumption statistics
public FuelStats calculateStats(Car car)
```

**Algorithm:**
```java
// Formula: (Total Fuel / Distance) × 100
double totalFuel = sum of all liters;
double totalDistance = latest odometer - earliest odometer;
double avgConsumption = (totalFuel / totalDistance) * 100;
```

**Edge Cases Handled:**
- Single entry: Returns 0.0 consumption (no distance)
- No entries: Returns zeros for all metrics
- Thread-safe: Uses synchronized collections

#### 4. Data Storage Operations
**File:** [src/main/java/com/aem/carfuel/storage/InMemoryCarStorage.java](src/main/java/com/aem/carfuel/storage/InMemoryCarStorage.java)

```java
// Save or update car
public Car save(Car car)

// Find car by ID
public Optional<Car> findById(Long id)

// Get all cars
public List<Car> findAll()

// Check for duplicate
public boolean existsByBrandModelYear(String brand, String model, Integer year)
```

**Thread Safety:**
- Uses `ConcurrentHashMap` for concurrent access
- `AtomicLong` for atomic ID generation
- No explicit locks needed

#### 5. REST API Endpoints
**File:** [src/main/java/com/aem/carfuel/controller/CarController.java](src/main/java/com/aem/carfuel/controller/CarController.java)

```java
@PostMapping("/api/cars")              // Create car
@GetMapping("/api/cars")               // List all cars
@GetMapping("/api/cars/{id}")          // Get specific car
@PostMapping("/api/cars/{id}/fuel")    // Add fuel entry
@GetMapping("/api/cars/{id}/fuel/stats") // Get statistics
```

**Features:**
- Automatic JSON conversion (Jackson)
- Bean validation (`@Valid`)
- Proper HTTP status codes (201, 200, 404, 400)

#### 6. Traditional Servlet
**File:** [src/main/java/com/aem/carfuel/servlet/FuelStatsServlet.java](src/main/java/com/aem/carfuel/servlet/FuelStatsServlet.java)

```java
@Override
protected void doGet(HttpServletRequest request, 
                     HttpServletResponse response)
```

**Manual Handling:**
- Parameter parsing: `request.getParameter("carId")`
- Content-Type setting: `response.setContentType("application/json")`
- Status codes: `response.setStatus()`
- Manual JSON: `ObjectMapper.writeValue()`

**Compare with REST:**
- Servlet: Manual everything
- REST: Framework handles it
- Both: Share same service layer

### Function Call Flow Example

**Creating a Car:**
```
Client Request (POST /api/cars)
    ↓
CarController.createCar(@RequestBody CreateCarRequest)
    ↓
CarService.createCar(request)
    ↓ [checks duplicate]
InMemoryCarStorage.existsByBrandModelYear()
    ↓ [if unique]
InMemoryCarStorage.save(car)
    ↓
Return Car object
    ↓ [auto-converted to JSON]
HTTP 201 Response
```

**Getting Statistics:**
```
Client Request (GET /api/cars/1/fuel/stats)
    ↓
CarController.getFuelStats(carId=1)
    ↓
CarService.getCarById(1)
    ↓
InMemoryCarStorage.findById(1)
    ↓ [if found]
CarService.calculateStats(car)
    ↓ [calculates metrics]
Return FuelStats object
    ↓ [auto-converted to JSON]
HTTP 200 Response
```

### Understanding Error Handling

**File:** [src/main/java/com/aem/carfuel/exception/GlobalExceptionHandler.java](src/main/java/com/aem/carfuel/exception/GlobalExceptionHandler.java)

```java
@ExceptionHandler(CarNotFoundException.class)       // → 404
@ExceptionHandler(DuplicateCarException.class)      // → 409
@ExceptionHandler(InvalidRequestException.class)    // → 400
@ExceptionHandler(MethodArgumentNotValidException.class) // → 400
```

**Error Response Format:**
```json
{
  "message": "Car with ID 999 not found",
  "status": 404,
  "timestamp": "2025-12-30T10:30:00"
}
```

---

## �🐛 Troubleshooting

### Application won't start
```bash
# Check if port 8080 is already in use
lsof -i :8080

# Kill the process or change port in application.properties
server.port=8081
```

### Build fails
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Lombok not working in IDE
- Install Lombok plugin for your IDE
- Enable annotation processing in IDE settings

---

## 👨‍💻 Technical Stack

- **Framework**: Spring Boot 4.0.1
- **Java Version**: 21
- **Build Tool**: Gradle 8.x
- **Dependencies**:
  - Spring Web MVC
  - Spring Boot Validation
  - Jakarta Servlet API 6.0
  - Lombok
  - Jackson (JSON processing)

---

**Built with ❤️ using Spring Boot**

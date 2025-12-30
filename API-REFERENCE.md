# API Quick Reference

## Base URL
```
http://localhost:8080
```

## REST API Endpoints

### 1. Create Car
```http
POST /api/cars
Content-Type: application/json

{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Response:** 201 Created
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

---

### 2. Get All Cars
```http
GET /api/cars
```

**Response:** 200 OK
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

---

### 3. Get Car by ID
```http
GET /api/cars/{id}
```

**Response:** 200 OK or 404 Not Found

---

### 4. Add Fuel Entry
```http
POST /api/cars/{id}/fuel
Content-Type: application/json

{
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000
}
```

**Response:** 200 OK or 404 Not Found

---

### 5. Get Fuel Statistics (REST)
```http
GET /api/cars/{id}/fuel/stats
```

**Response:** 200 OK or 404 Not Found
```json
{
  "totalFuel": 127.0,
  "totalCost": 166.5,
  "avgConsumption": 12.7,
  "entriesCount": 3
}
```

---

## Traditional Servlet Endpoint

### 6. Get Fuel Statistics (Servlet)
```http
GET /servlet/fuel-stats?carId={id}
```

**Response:** Same as REST endpoint

---

## Error Responses

### 404 Not Found
```json
{
  "message": "Car not found with id: 999",
  "status": 404,
  "timestamp": "2025-12-30T12:00:00"
}
```

### 400 Bad Request
```json
{
  "message": "Brand is required",
  "status": 400,
  "timestamp": "2025-12-30T12:00:00"
}
```

### 500 Internal Server Error
```json
{
  "message": "Internal server error: ...",
  "status": 500,
  "timestamp": "2025-12-30T12:00:00"
}
```

---

## Validation Rules

### Car Creation
- `brand`: Required, non-blank
- `model`: Required, non-blank
- `year`: Required, minimum 1900

### Fuel Entry
- `liters`: Required, must be positive
- `price`: Required, must be positive
- `odometer`: Required, minimum 0

---

## cURL Examples

### Create a car and add fuel entries
```bash
# Create car
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}'

# Add fuel entries
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40.0,"price":52.5,"odometer":45000}'

curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":45.0,"price":59.0,"odometer":45500}'

# Get statistics
curl http://localhost:8080/api/cars/1/fuel/stats
curl "http://localhost:8080/servlet/fuel-stats?carId=1"
```

---

## Statistics Calculation

**Formula:** `avgConsumption = (totalFuel / distance) × 100`

Where:
- `totalFuel` = Sum of all fuel entries (liters)
- `distance` = Last odometer - First odometer (km)
- `avgConsumption` = Liters per 100 kilometers

**Special Cases:**
- Less than 2 entries: `avgConsumption = 0.0`
- Distance ≤ 0: `avgConsumption = 0.0`

**Example:**
```
Entry 1: 40L at 45000 km
Entry 2: 45L at 45500 km  
Entry 3: 42L at 46000 km

Total Fuel = 127L
Distance = 46000 - 45000 = 1000 km
Average = (127 / 1000) × 100 = 12.7 L/100km
```

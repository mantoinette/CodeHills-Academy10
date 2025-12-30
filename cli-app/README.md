# Car Fuel CLI - Standalone Java Application

## 📦 Overview

This is a **completely separate** standalone Java CLI application that interacts with the Car Fuel backend API via HTTP. It is **NOT part of the Spring Boot application** and compiles independently.

### Project Structure

```
carfuel/                          # Main Spring Boot backend
└── src/...

cli-app/                          # Standalone CLI (SEPARATE)
├── build.gradle                  # Independent build config
├── settings.gradle
├── gradlew, gradlew.bat
└── src/main/java/
    └── com/carfuel/cli/
        ├── CarFuelCli.java                # Main entry point
        ├── command/
        │   └── CommandParser.java         # Command execution
        ├── http/
        │   └── ApiClient.java             # HTTP client (java.net.http)
        └── server/
            └── ServerManager.java         # Server lifecycle management
```

## 🔧 Build Instructions

### 1. Build the CLI Application

```bash
cd cli-app
./gradlew clean build
```

This creates: `build/libs/carfuel-cli-1.0.0.jar` (293KB)

### 2. Build the Backend Server (if not already built)

```bash
cd ..  # Back to project root
./gradlew clean build
```

This creates: `build/libs/carfuel-0.0.1-SNAPSHOT.jar` (24MB)

## 🚀 Running the CLI

### Quick Start

```bash
# From project root directory
java -jar cli-app/build/libs/carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018
```

### If Server Not Running

The CLI will detect this and offer to start it:

```
⚠️  Backend server is not running.
Start the server now? (y/n): y
🚀 Starting backend server...
✅ Server started successfully!
✅ Car created successfully!
```

## 📋 Commands

### 1. Create Car

```bash
java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018
```

**Output:**
```
✅ Car created successfully!
   ID:    1
   Brand: Toyota
   Model: Corolla
   Year:  2018
```

### 2. Add Fuel Entry

```bash
java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
```

**Output:**
```
✅ Fuel entry added successfully!
   Car:           Toyota Corolla
   Total entries: 1
```

### 3. View Fuel Statistics

```bash
java -jar carfuel-cli-1.0.0.jar fuel-stats --carId 1
```

**Output:**
```
═══════════════════════════════════════
        Fuel Statistics                
═══════════════════════════════════════

Total fuel:          120.0 L
Total cost:          155.00
Average consumption: 6.4 L/100km
Entries count:       3
```

### 4. List All Cars

```bash
java -jar carfuel-cli-1.0.0.jar list-cars
```

**Output:**
```
═══════════════════════════════════════════════════════════
                    Registered Cars                        
═══════════════════════════════════════════════════════════

ID    Brand           Model           Year   Entries
───────────────────────────────────────────────────────────
1     Toyota          Corolla         2018   3       
2     Honda           Civic           2020   0       
```

## 🎯 Complete Example

```bash
cd cli-app/build/libs

# Create a car
java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018

# Add fuel entries
java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 45 --price 59.0 --odometer 45500
java -jar carfuel-cli-1.0.0.jar add-fuel --carId 1 --liters 42 --price 55.0 --odometer 46000

# View statistics
java -jar carfuel-cli-1.0.0.jar fuel-stats --carId 1

# List cars
java -jar carfuel-cli-1.0.0.jar list-cars
```

## 🏗️ Architecture

### Separation of Concerns

```
┌──────────────────────────┐
│  cli-app/                │  Standalone Java Application
│  (Separate Project)      │  - Main: CarFuelCli.java
│                          │  - Uses: java.net.http.HttpClient
│  carfuel-cli-1.0.0.jar   │  - Dependencies: Only Gson (JSON)
│  (293KB)                 │  - No Spring Boot!
└────────────┬─────────────┘
             │
             │ HTTP REST Calls
             │ (localhost:8080)
             ↓
┌──────────────────────────┐
│  carfuel/                │  Spring Boot Backend
│  (Main Project)          │  - REST API
│                          │  - Business Logic
│  carfuel-*.jar          │  - In-Memory Storage
│  (24MB)                 │
└──────────────────────────┘
```

### Key Points

1. **Separate Compilation**: CLI app has its own `build.gradle` and builds independently
2. **No Spring Dependencies**: Uses only standard Java + Gson for JSON
3. **HTTP Communication**: Uses `java.net.http.HttpClient` (Java 11+)
4. **Server Detection**: Can check if backend is running and start it if needed
5. **Lightweight**: Only 293KB vs 24MB backend

## 🔍 Technical Details

### Dependencies

**CLI Application** (`cli-app/build.gradle`):
```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'  // JSON only
}
```

**NO Spring Boot!** Just pure Java + minimal JSON library.

### HTTP Client

Uses standard Java HttpClient:
```java
HttpClient httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .build();
```

### Server Management

The CLI can:
- Check if server is running (HTTP health check)
- Find server JAR in multiple locations
- Start server as background process
- Wait for server to be ready

## 📂 File Locations

When running CLI, it looks for backend JAR in:
1. Current directory: `./carfuel-0.0.1-SNAPSHOT.jar`
2. Parent build: `../build/libs/carfuel-0.0.1-SNAPSHOT.jar`
3. Build directory: `build/libs/carfuel-0.0.1-SNAPSHOT.jar`

## ⚡ Quick Commands

### Copy CLI JAR to convenient location

```bash
# Copy to project root for easy access
cp cli-app/build/libs/carfuel-cli-1.0.0.jar .

# Now you can run from root:
java -jar carfuel-cli-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018
```

### Create an alias

```bash
# Add to ~/.zshrc or ~/.bashrc
alias carfuel='java -jar /Users/pro/projects/carfuel/cli-app/build/libs/carfuel-cli-1.0.0.jar'

# Then use:
carfuel create-car --brand Toyota --model Corolla --year 2018
carfuel fuel-stats --carId 1
```

## ✅ Requirements Met

✅ **Separate executable** - Independent JAR file  
✅ **Separate main class** - `CarFuelCli.java` (not `CarfuelApplication.java`)  
✅ **Uses HttpClient** - `java.net.http.HttpClient`  
✅ **All required commands** - create-car, add-fuel, fuel-stats  
✅ **Server management** - Can check and start backend  
✅ **Compiled separately** - Own build.gradle  
✅ **No Spring dependency** - Pure Java + Gson

## 🎉 Summary

- **2 separate applications**: Backend (Spring Boot) + CLI (Pure Java)
- **2 separate JARs**: 24MB backend + 293KB CLI
- **2 separate builds**: Independent compilation
- **1 common interface**: REST API over HTTP

The CLI is a true standalone application that happens to communicate with your backend!

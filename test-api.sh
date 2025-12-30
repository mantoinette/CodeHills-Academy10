#!/bin/bash

# Car Fuel Management System - API Test Script
# Run this script to test all endpoints

BASE_URL="http://localhost:8080"

echo "========================================="
echo "Car Fuel Management System - API Tests"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}1. Creating a car...${NC}"
curl -X POST $BASE_URL/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}2. Getting all cars...${NC}"
curl $BASE_URL/api/cars -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}3. Getting car with ID 1...${NC}"
curl $BASE_URL/api/cars/1 -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}4. Adding first fuel entry (40L at 45000 km)...${NC}"
curl -X POST $BASE_URL/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40.0,"price":52.5,"odometer":45000}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}5. Adding second fuel entry (45L at 45500 km)...${NC}"
curl -X POST $BASE_URL/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":45.0,"price":59.0,"odometer":45500}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}6. Adding third fuel entry (42L at 46000 km)...${NC}"
curl -X POST $BASE_URL/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":42.0,"price":55.0,"odometer":46000}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}7. Getting fuel statistics via REST API...${NC}"
curl $BASE_URL/api/cars/1/fuel/stats -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}8. Getting fuel statistics via Servlet...${NC}"
curl "$BASE_URL/servlet/fuel-stats?carId=1" -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}9. Testing 404 error (non-existent car)...${NC}"
curl $BASE_URL/api/cars/999 -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}10. Testing validation error (missing brand)...${NC}"
curl -X POST $BASE_URL/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"","model":"Test","year":2020}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}11. Testing servlet with missing carId parameter...${NC}"
curl "$BASE_URL/servlet/fuel-stats" -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}12. Testing duplicate car creation (409 Conflict)...${NC}"
curl -X POST $BASE_URL/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}13. Testing invalid odometer (decreasing reading)...${NC}"
curl -X POST $BASE_URL/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":30.0,"price":40.0,"odometer":44000}' \
  -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${BLUE}14. Testing invalid path parameter type...${NC}"
curl $BASE_URL/api/cars/invalid -w "\nHTTP Status: %{http_code}\n\n"

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}All API tests completed!${NC}"
echo -e "${GREEN}=========================================${NC}"

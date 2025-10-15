#!/bin/bash

# 12306 Train Ticket Booking System - Quick Start Script
# This script helps you quickly start all services

set -e

echo "=========================================="
echo "  12306 Train Ticket Booking System"
echo "  Quick Start Script"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_green() {
    echo -e "${GREEN}$1${NC}"
}

print_yellow() {
    echo -e "${YELLOW}$1${NC}"
}

print_red() {
    echo -e "${RED}$1${NC}"
}

# Check if required commands are available
check_requirements() {
    print_yellow "Checking requirements..."
    
    if ! command -v java &> /dev/null; then
        print_red "Error: Java is not installed. Please install JDK 17 or higher."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_red "Error: Java version must be 17 or higher. Current version: $JAVA_VERSION"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_red "Error: Maven is not installed."
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        print_yellow "Warning: Docker is not installed. You'll need to start MySQL, Redis, and Nacos manually."
    fi
    
    print_green "✓ Requirements check passed!"
    echo ""
}

# Start infrastructure using Docker Compose
start_infrastructure() {
    print_yellow "Starting infrastructure (MySQL, Redis, Nacos)..."
    
    if command -v docker-compose &> /dev/null || command -v docker &> /dev/null; then
        if [ -f "docker-compose.yml" ]; then
            docker-compose up -d
            print_green "✓ Infrastructure started!"
            print_yellow "Waiting 30 seconds for services to be ready..."
            sleep 30
        else
            print_red "Error: docker-compose.yml not found!"
            exit 1
        fi
    else
        print_yellow "Skipping Docker infrastructure. Make sure MySQL, Redis, and Nacos are running."
    fi
    echo ""
}

# Build the project
build_project() {
    print_yellow "Building the project..."
    
    if [ ! -f "pom.xml" ]; then
        print_red "Error: pom.xml not found! Are you in the project root directory?"
        exit 1
    fi
    
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_green "✓ Build successful!"
    else
        print_red "✗ Build failed!"
        exit 1
    fi
    echo ""
}

# Start a service in the background
start_service() {
    local service_name=$1
    local service_port=$2
    local jar_file="train-${service_name}/target/train-${service_name}-1.0.0.jar"
    
    if [ ! -f "$jar_file" ]; then
        print_red "Error: $jar_file not found! Please build the project first."
        return 1
    fi
    
    print_yellow "Starting $service_name service on port $service_port..."
    
    nohup java -jar "$jar_file" > "logs/${service_name}.log" 2>&1 &
    echo $! > "logs/${service_name}.pid"
    
    sleep 5
    
    if ps -p $(cat "logs/${service_name}.pid") > /dev/null; then
        print_green "✓ $service_name service started!"
    else
        print_red "✗ Failed to start $service_name service. Check logs/${service_name}.log"
        return 1
    fi
}

# Start all services
start_services() {
    print_yellow "Starting all microservices..."
    
    # Create logs directory
    mkdir -p logs
    
    # Start services in order
    start_service "gateway" "8000"
    start_service "user" "8001"
    start_service "ticket" "8002"
    start_service "order" "8003"
    start_service "payment" "8004"
    
    echo ""
    print_green "=========================================="
    print_green "  All services started successfully!"
    print_green "=========================================="
    echo ""
    print_yellow "Service URLs:"
    echo "  Gateway:  http://localhost:8000"
    echo "  User:     http://localhost:8001"
    echo "  Ticket:   http://localhost:8002"
    echo "  Order:    http://localhost:8003"
    echo "  Payment:  http://localhost:8004"
    echo "  Nacos:    http://localhost:8848/nacos"
    echo ""
    print_yellow "Logs location: ./logs/"
    echo ""
    print_yellow "To stop all services, run: ./stop.sh"
    echo ""
}

# Check service status
check_status() {
    print_yellow "Checking service status..."
    echo ""
    
    services=("gateway" "user" "ticket" "order" "payment")
    
    for service in "${services[@]}"; do
        if [ -f "logs/${service}.pid" ]; then
            pid=$(cat "logs/${service}.pid")
            if ps -p $pid > /dev/null 2>&1; then
                print_green "✓ $service service is running (PID: $pid)"
            else
                print_red "✗ $service service is not running"
            fi
        else
            print_yellow "? $service service PID file not found"
        fi
    done
    echo ""
}

# Main execution
main() {
    case "${1:-start}" in
        start)
            check_requirements
            start_infrastructure
            build_project
            start_services
            ;;
        build)
            check_requirements
            build_project
            ;;
        status)
            check_status
            ;;
        *)
            echo "Usage: $0 {start|build|status}"
            echo ""
            echo "Commands:"
            echo "  start   - Start infrastructure, build project, and start all services"
            echo "  build   - Build the project only"
            echo "  status  - Check service status"
            exit 1
            ;;
    esac
}

main "$@"

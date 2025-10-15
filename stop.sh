#!/bin/bash

# 12306 Train Ticket Booking System - Stop Script
# This script stops all running services

set -e

echo "=========================================="
echo "  12306 Train Ticket Booking System"
echo "  Stop Script"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_green() {
    echo -e "${GREEN}$1${NC}"
}

print_yellow() {
    echo -e "${YELLOW}$1${NC}"
}

print_red() {
    echo -e "${RED}$1${NC}"
}

# Stop a service
stop_service() {
    local service_name=$1
    
    if [ -f "logs/${service_name}.pid" ]; then
        local pid=$(cat "logs/${service_name}.pid")
        
        if ps -p $pid > /dev/null 2>&1; then
            print_yellow "Stopping $service_name service (PID: $pid)..."
            kill $pid
            
            # Wait for process to stop
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 30 ]; do
                sleep 1
                ((count++))
            done
            
            if ps -p $pid > /dev/null 2>&1; then
                print_yellow "Force stopping $service_name service..."
                kill -9 $pid
            fi
            
            print_green "✓ $service_name service stopped"
        else
            print_yellow "✓ $service_name service is not running"
        fi
        
        rm -f "logs/${service_name}.pid"
    else
        print_yellow "? $service_name service PID file not found"
    fi
}

# Stop all services
stop_services() {
    print_yellow "Stopping all microservices..."
    echo ""
    
    services=("payment" "order" "ticket" "user" "gateway")
    
    for service in "${services[@]}"; do
        stop_service "$service"
    done
    
    echo ""
    print_green "All services stopped!"
}

# Stop infrastructure
stop_infrastructure() {
    print_yellow "Stopping infrastructure..."
    
    if command -v docker-compose &> /dev/null || command -v docker &> /dev/null; then
        if [ -f "docker-compose.yml" ]; then
            docker-compose down
            print_green "✓ Infrastructure stopped!"
        fi
    else
        print_yellow "Docker not found. Skipping infrastructure shutdown."
    fi
    echo ""
}

# Main execution
main() {
    case "${1:-all}" in
        all)
            stop_services
            stop_infrastructure
            ;;
        services)
            stop_services
            ;;
        infrastructure)
            stop_infrastructure
            ;;
        *)
            echo "Usage: $0 {all|services|infrastructure}"
            echo ""
            echo "Commands:"
            echo "  all             - Stop all services and infrastructure (default)"
            echo "  services        - Stop microservices only"
            echo "  infrastructure  - Stop Docker infrastructure only"
            exit 1
            ;;
    esac
    
    print_green "=========================================="
    print_green "  Shutdown complete!"
    print_green "=========================================="
}

main "$@"

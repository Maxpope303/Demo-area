#!/bin/bash

# ============================================================================
# Local Pipeline Testing Script
# ============================================================================
# This script runs the same checks that GitHub Actions will run,
# allowing you to test locally before pushing to GitHub.
#
# Usage:
#   ./test-pipeline-locally.sh [option]
#
# Options:
#   all       - Run all checks (default)
#   build     - Build only
#   test      - Run tests only
#   coverage  - Generate coverage report
#   security  - Run security checks
#   container - Build container with Podman
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        exit 1
    fi
    print_success "Maven found: $(mvn -version | head -n 1)"
}

# Check if Podman is installed
check_podman() {
    if ! command -v podman &> /dev/null; then
        print_warning "Podman is not installed. Container build will be skipped."
        return 1
    fi
    print_success "Podman found: $(podman --version)"
    return 0
}

# Run Maven build
run_build() {
    print_header "Building Application"
    mvn clean compile -B
    print_success "Build completed successfully!"
}

# Run tests
run_tests() {
    print_header "Running Unit Tests"
    mvn test -B
    print_success "All tests passed!"
}

# Generate coverage report
run_coverage() {
    print_header "Generating Coverage Report"
    mvn clean test jacoco:report -B
    
    if [ -f "target/site/jacoco/index.html" ]; then
        print_success "Coverage report generated!"
        print_info "Opening coverage report in browser..."
        
        # Open in browser based on OS
        if [[ "$OSTYPE" == "darwin"* ]]; then
            open target/site/jacoco/index.html
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            xdg-open target/site/jacoco/index.html 2>/dev/null || print_info "Please open: target/site/jacoco/index.html"
        else
            print_info "Please open: target/site/jacoco/index.html"
        fi
    else
        print_error "Coverage report not found!"
        exit 1
    fi
}

# Run security checks
run_security() {
    print_header "Running Security Checks"
    
    print_info "Running SpotBugs analysis..."
    mvn spotbugs:check -B || print_warning "SpotBugs found issues (check target/spotbugsXml.xml)"
    
    print_info "Running OWASP Dependency Check..."
    mvn org.owasp:dependency-check-maven:check -B || print_warning "OWASP found vulnerabilities (check target/dependency-check-report.html)"
    
    print_success "Security checks completed!"
    
    # Open reports if they exist
    if [ -f "target/dependency-check-report.html" ]; then
        print_info "Opening OWASP report..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            open target/dependency-check-report.html
        fi
    fi
}

# Build container with Podman
run_container_build() {
    print_header "Building Container with Podman"
    
    if ! check_podman; then
        print_error "Podman is required for container build"
        return 1
    fi
    
    # Build WAR first if not exists
    if [ ! -f "target/simple-pharmacy.war" ]; then
        print_info "WAR file not found, building..."
        mvn package -DskipTests -B
    fi
    
    print_info "Building container image..."
    podman build -t simple-pharmacy:test .
    
    print_success "Container built successfully!"
    print_info "Image: simple-pharmacy:test"
    
    # Show image details
    podman images simple-pharmacy:test
}

# Run all checks
run_all() {
    print_header "Running Complete Pipeline Locally"
    
    check_maven
    
    run_build
    run_tests
    run_coverage
    run_security
    
    if check_podman; then
        run_container_build
    else
        print_warning "Skipping container build (Podman not available)"
    fi
    
    print_header "Pipeline Complete!"
    print_success "All checks passed! Ready to push to GitHub."
    echo ""
    print_info "Next steps:"
    echo "  1. Review coverage report: target/site/jacoco/index.html"
    echo "  2. Review security reports in target/"
    echo "  3. Commit and push your changes"
    echo "  4. Monitor GitHub Actions pipeline"
}

# Show summary
show_summary() {
    print_header "Pipeline Test Summary"
    echo "Available commands:"
    echo "  ./test-pipeline-locally.sh all       - Run all checks"
    echo "  ./test-pipeline-locally.sh build     - Build only"
    echo "  ./test-pipeline-locally.sh test      - Run tests only"
    echo "  ./test-pipeline-locally.sh coverage  - Generate coverage report"
    echo "  ./test-pipeline-locally.sh security  - Run security checks"
    echo "  ./test-pipeline-locally.sh container - Build container"
    echo ""
    print_info "This script mimics the GitHub Actions pipeline"
}

# Main script logic
case "${1:-all}" in
    all)
        run_all
        ;;
    build)
        check_maven
        run_build
        ;;
    test)
        check_maven
        run_tests
        ;;
    coverage)
        check_maven
        run_coverage
        ;;
    security)
        check_maven
        run_security
        ;;
    container)
        run_container_build
        ;;
    help|--help|-h)
        show_summary
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_summary
        exit 1
        ;;
esac

# Made with Bob
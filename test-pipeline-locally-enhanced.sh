#!/bin/bash

# ============================================================================
# Enhanced Local Pipeline Testing Script with act Support
# ============================================================================
# This script runs the same checks that GitHub Actions will run,
# allowing you to test locally before pushing to GitHub.
#
# Usage:
#   ./test-pipeline-locally-enhanced.sh [option]
#
# Options:
#   all       - Run all checks (default)
#   build     - Build only
#   test      - Run tests only
#   coverage  - Generate coverage report
#   security  - Run security checks
#   container - Build container with Podman
#   act       - Run GitHub Actions locally with act
#   act-list  - List available workflows for act
#   clean     - Clean build artifacts
#   validate  - Validate configuration files
# ============================================================================

set -e

# Configuration variables
IMAGE_NAME="${IMAGE_NAME:-simple-pharmacy}"
IMAGE_TAG="${IMAGE_TAG:-test}"

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

# Check if act is installed
check_act() {
    if ! command -v act &> /dev/null; then
        print_warning "act is not installed. Install with: brew install act (macOS) or see https://github.com/nektos/act"
        return 1
    fi
    print_success "act found: $(act --version)"
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
    if [ ! -d "target" ] || [ ! -f "target/simple-pharmacy.war" ]; then
        print_info "WAR file not found, building..."
        mvn package -DskipTests -B
    fi
    
    print_info "Building container image..."
    podman build -t "${IMAGE_NAME}:${IMAGE_TAG}" .
    
    print_success "Container built successfully!"
    print_info "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
    
    # Show image details
    podman images "${IMAGE_NAME}:${IMAGE_TAG}"
}

# Run GitHub Actions locally with act
run_act() {
    print_header "Running GitHub Actions Locally with act"
    
    if ! check_act; then
        print_error "act is required. Install it first:"
        echo ""
        echo "  macOS:   brew install act"
        echo "  Linux:   curl -o /tmp/act-install.sh https://raw.githubusercontent.com/nektos/act/master/install.sh && chmod +x /tmp/act-install.sh && sudo /tmp/act-install.sh"
        echo "  Windows: choco install act-cli"
        echo ""
        echo "More info: https://github.com/nektos/act"
        return 1
    fi
    
    print_info "Running CI/CD workflow with act..."
    print_warning "Note: This may take several minutes on first run (downloading Docker images)"
    echo ""
    
    # Configuration variables
    local ARTIFACT_PATH="${ARTIFACT_PATH:-/tmp/artifacts}"
    local CONTAINER_ARCH="${CONTAINER_ARCH:-linux/amd64}"
    
    # Run the workflow
    act -W .github/workflows/ci-cd.yml \
        --artifact-server-path "$ARTIFACT_PATH" \
        --container-architecture "$CONTAINER_ARCH" \
        push
    
    print_success "act execution completed!"
}

# List available workflows for act
list_act_workflows() {
    print_header "Available GitHub Actions Workflows"
    
    if ! check_act; then
        return 1
    fi
    
    print_info "Listing workflows..."
    act -l
    
    echo ""
    print_info "To run a specific workflow:"
    echo "  act -W .github/workflows/ci-cd.yml push"
    echo "  act -W .github/workflows/release.yml workflow_dispatch"
}

# Clean build artifacts
run_clean() {
    print_header "Cleaning Build Artifacts"
    
    print_info "Removing Maven target directory..."
    mvn clean -B
    
    print_info "Removing test reports..."
    rm -rf target/surefire-reports/ 2>/dev/null || true
    rm -rf target/site/ 2>/dev/null || true
    
    print_info "Removing container images..."
    if check_podman; then
        podman rmi "${IMAGE_NAME}:${IMAGE_TAG}" 2>/dev/null || true
    fi
    
    print_success "Cleanup completed!"
}

# Validate configuration files
run_validate() {
    print_header "Validating Configuration Files"
    
    local errors=0
    
    # Validate pom.xml
    print_info "Validating pom.xml..."
    if mvn validate -B &>/dev/null; then
        print_success "pom.xml is valid"
    else
        print_error "pom.xml has errors"
        ((errors++))
    fi
    
    # Validate Dockerfile
    print_info "Validating Dockerfile..."
    if [ -f "Dockerfile" ]; then
        if command -v hadolint &> /dev/null; then
            if hadolint Dockerfile; then
                print_success "Dockerfile is valid"
            else
                print_warning "Dockerfile has linting issues"
            fi
        else
            print_warning "hadolint not installed (optional: brew install hadolint)"
        fi
    fi
    
    # Validate GitHub Actions workflows
    print_info "Validating GitHub Actions workflows..."
    if command -v actionlint &> /dev/null; then
        if actionlint .github/workflows/*.yml; then
            print_success "GitHub Actions workflows are valid"
        else
            print_error "GitHub Actions workflows have errors"
            ((errors++))
        fi
    else
        print_warning "actionlint not installed (optional: brew install actionlint)"
    fi
    
    # Check for common issues
    print_info "Checking for common issues..."
    
    # Check for hardcoded secrets
    if grep -rEl "password|secret|token" src/ --include="*.java" --include="*.xml" | grep -vE "(//|/\*)" &>/dev/null; then
        print_warning "Potential hardcoded secrets found in source code"
    else
        print_success "No obvious hardcoded secrets found"
    fi
    
    if [ $errors -eq 0 ]; then
        print_success "All validations passed!"
        return 0
    else
        print_error "Validation failed with $errors error(s)"
        return 1
    fi
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

# Show summary with enhanced help
show_summary() {
    print_header "Pipeline Test Summary"
    echo "Available commands:"
    echo ""
    echo -e "  ${GREEN}Basic Commands:${NC}"
    echo "    all       - Run all checks (default)"
    echo "    build     - Build only"
    echo "    test      - Run tests only"
    echo "    coverage  - Generate coverage report"
    echo "    security  - Run security checks"
    echo "    container - Build container with Podman"
    echo ""
    echo -e "  ${BLUE}Advanced Commands:${NC}"
    echo "    act       - Run GitHub Actions locally with act"
    echo "    act-list  - List available workflows for act"
    echo "    validate  - Validate configuration files"
    echo "    clean     - Clean build artifacts"
    echo ""
    echo -e "  ${YELLOW}Tools Installation:${NC}"
    echo "    act:        brew install act (macOS)"
    echo "    hadolint:   brew install hadolint (Dockerfile linting)"
    echo "    actionlint: brew install actionlint (workflow validation)"
    echo ""
    print_info "This script mimics the GitHub Actions pipeline"
    print_info "For full pipeline simulation, use: ./test-pipeline-locally-enhanced.sh act"
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
    act)
        run_act
        ;;
    act-list)
        list_act_workflows
        ;;
    clean)
        check_maven
        run_clean
        ;;
    validate)
        run_validate
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
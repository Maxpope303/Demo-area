#!/bin/bash

# ============================================================================
# Java Modernization Demo Reset Script
# ============================================================================
# This script manages switching between Java 8 baseline and modernized states
# for demonstration purposes using Git branches.
#
# Usage:
#   ./demo-reset.sh [command]
#
# Commands:
#   init          - Initialize demo branches (run once)
#   status        - Show current demo state
#   baseline      - Switch to Java 8 baseline state
#   modernized    - Switch to modernized state
#   save          - Save current changes to active branch
#   clean         - Clean up Docker containers and images
#   help          - Show this help message
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Branch names
BASELINE_BRANCH="demo-java8-baseline"
MODERNIZED_BRANCH="demo-modernized"

# Function to print colored messages
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

# Function to check if we're in a git repository
check_git_repo() {
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not a git repository. Please run 'git init' first."
        exit 1
    fi
}

# Function to detect container runtime
detect_container_runtime() {
    if command -v docker &> /dev/null; then
        echo "docker"
    elif command -v podman &> /dev/null; then
        echo "podman"
    else
        echo "none"
    fi
}

# Function to clean Docker/Podman resources
clean_containers() {
    print_header "Cleaning Container Resources"
    
    CONTAINER_CMD=$(detect_container_runtime)
    
    if [ "$CONTAINER_CMD" = "none" ]; then
        print_warning "No container runtime found. Skipping cleanup."
        return
    fi
    
    print_info "Using container runtime: $CONTAINER_CMD"
    
    # Stop and remove containers
    if [ "$CONTAINER_CMD" = "docker" ]; then
        if docker-compose ps -q 2>/dev/null | grep -q .; then
            print_info "Stopping docker-compose services..."
            docker-compose down 2>/dev/null || true
        fi
    fi
    
    # Remove simple-pharmacy containers
    CONTAINERS=$($CONTAINER_CMD ps -a --filter "name=simple-pharmacy" -q)
    if [ -n "$CONTAINERS" ]; then
        print_info "Removing simple-pharmacy containers..."
        $CONTAINER_CMD rm -f $CONTAINERS 2>/dev/null || true
    fi
    
    # Remove simple-pharmacy images
    IMAGES=$($CONTAINER_CMD images --filter "reference=simple-pharmacy*" -q)
    if [ -n "$IMAGES" ]; then
        print_info "Removing simple-pharmacy images..."
        $CONTAINER_CMD rmi -f $IMAGES 2>/dev/null || true
    fi
    
    # Clean Maven build artifacts
    if [ -d "target" ]; then
        print_info "Cleaning Maven target directory..."
        rm -rf target
    fi
    
    print_success "Cleanup complete!"
}

# Function to initialize demo branches
init_demo() {
    print_header "Initializing Demo Branches"
    
    check_git_repo
    
    # Check if branches already exist
    if git show-ref --verify --quiet refs/heads/$BASELINE_BRANCH; then
        print_warning "Branch '$BASELINE_BRANCH' already exists."
        read -p "Do you want to recreate it? This will overwrite existing branch. (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "Skipping baseline branch creation."
        else
            git branch -D $BASELINE_BRANCH 2>/dev/null || true
        fi
    fi
    
    # Get current branch
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    
    # Check for uncommitted changes
    if ! git diff-index --quiet HEAD --; then
        print_warning "You have uncommitted changes."
        print_info "These changes will be included in the baseline branch."
        read -p "Continue? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_error "Aborted."
            exit 1
        fi
        
        # Commit changes
        print_info "Committing current changes..."
        git add -A
        git commit -m "Java 8 baseline state for demo" || true
    fi
    
    # Create baseline branch from current state
    if ! git show-ref --verify --quiet refs/heads/$BASELINE_BRANCH; then
        print_info "Creating baseline branch: $BASELINE_BRANCH"
        git branch $BASELINE_BRANCH
        print_success "Baseline branch created!"
    fi
    
    # Create modernized branch if it doesn't exist
    if ! git show-ref --verify --quiet refs/heads/$MODERNIZED_BRANCH; then
        print_info "Creating modernized branch: $MODERNIZED_BRANCH"
        git branch $MODERNIZED_BRANCH
        print_success "Modernized branch created!"
        print_info "You can switch to this branch and perform modernization."
    else
        print_warning "Branch '$MODERNIZED_BRANCH' already exists."
    fi
    
    print_success "Demo initialization complete!"
    echo ""
    print_info "Next steps:"
    echo "  1. Run: ./demo-reset.sh baseline    - To ensure you're on baseline"
    echo "  2. Run: ./demo-reset.sh modernized  - To switch to modernized branch"
    echo "  3. Perform your modernization changes"
    echo "  4. Run: ./demo-reset.sh save        - To save modernization changes"
    echo "  5. Run: ./demo-reset.sh baseline    - To reset back for next demo"
}

# Function to show current status
show_status() {
    print_header "Demo Status"
    
    check_git_repo
    
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    
    echo "Current branch: $CURRENT_BRANCH"
    echo ""
    
    # Check which state we're in
    if [ "$CURRENT_BRANCH" = "$BASELINE_BRANCH" ]; then
        print_success "You are on the JAVA 8 BASELINE branch"
        echo "  - Java Version: 1.8"
        echo "  - Runtime: WebSphere Traditional"
        echo "  - State: Original/Demo Start"
    elif [ "$CURRENT_BRANCH" = "$MODERNIZED_BRANCH" ]; then
        print_success "You are on the MODERNIZED branch"
        echo "  - Java Version: 17/21 (after modernization)"
        echo "  - Runtime: Liberty (after modernization)"
        echo "  - State: Upgraded/Demo End"
    else
        print_warning "You are on branch: $CURRENT_BRANCH"
        echo "  - This is not a demo branch"
    fi
    
    echo ""
    
    # Check for uncommitted changes
    if ! git diff-index --quiet HEAD --; then
        print_warning "You have uncommitted changes:"
        git status --short
    else
        print_success "Working directory is clean"
    fi
    
    echo ""
    print_info "Available branches:"
    git branch -l "$BASELINE_BRANCH" "$MODERNIZED_BRANCH" 2>/dev/null || echo "  No demo branches found. Run: ./demo-reset.sh init"
}

# Function to switch to baseline
switch_to_baseline() {
    print_header "Switching to Java 8 Baseline"
    
    check_git_repo
    
    # Check for uncommitted changes
    if ! git diff-index --quiet HEAD --; then
        print_warning "You have uncommitted changes."
        read -p "Do you want to save them first? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            save_changes
        else
            print_warning "Uncommitted changes will be lost!"
            read -p "Continue anyway? (y/N): " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                print_error "Aborted."
                exit 1
            fi
        fi
    fi
    
    # Clean containers before switching
    clean_containers
    
    # Switch to baseline branch
    print_info "Switching to branch: $BASELINE_BRANCH"
    git checkout $BASELINE_BRANCH
    git clean -fd
    
    print_success "Switched to Java 8 baseline!"
    echo ""
    print_info "Current state:"
    echo "  - Java Version: 1.8"
    echo "  - Runtime: WebSphere Traditional"
    echo "  - Ready for demo"
    echo ""
    print_info "To build and run: ./build-and-run.sh"
}

# Function to switch to modernized
switch_to_modernized() {
    print_header "Switching to Modernized State"
    
    check_git_repo
    
    # Check for uncommitted changes
    if ! git diff-index --quiet HEAD --; then
        print_warning "You have uncommitted changes."
        read -p "Do you want to save them first? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            save_changes
        else
            print_warning "Uncommitted changes will be lost!"
            read -p "Continue anyway? (y/N): " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                print_error "Aborted."
                exit 1
            fi
        fi
    fi
    
    # Clean containers before switching
    clean_containers
    
    # Switch to modernized branch
    print_info "Switching to branch: $MODERNIZED_BRANCH"
    git checkout $MODERNIZED_BRANCH
    git clean -fd
    
    print_success "Switched to modernized state!"
    echo ""
    print_info "You can now perform modernization or view modernized code"
    echo ""
    print_info "To save changes: ./demo-reset.sh save"
}

# Function to save changes
save_changes() {
    print_header "Saving Changes"
    
    check_git_repo
    
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    
    if ! git diff-index --quiet HEAD --; then
        print_info "Committing changes on branch: $CURRENT_BRANCH"
        git add -A
        
        # Prompt for commit message
        read -p "Enter commit message (or press Enter for default): " COMMIT_MSG
        if [ -z "$COMMIT_MSG" ]; then
            if [ "$CURRENT_BRANCH" = "$MODERNIZED_BRANCH" ]; then
                COMMIT_MSG="Modernization changes"
            else
                COMMIT_MSG="Updates to $CURRENT_BRANCH"
            fi
        fi
        
        git commit -m "$COMMIT_MSG"
        print_success "Changes saved!"
    else
        print_info "No changes to save."
    fi
}

# Function to show help
show_help() {
    cat << EOF

Java Modernization Demo Reset Script
=====================================

This script helps you manage demonstration states for Java modernization.

USAGE:
    ./demo-reset.sh [command]

COMMANDS:
    init          Initialize demo branches (run once at setup)
    status        Show current demo state and branch
    baseline      Switch to Java 8 baseline state (demo start)
    modernized    Switch to modernized state (demo end)
    save          Save current changes to active branch
    clean         Clean up Docker/Podman containers and images
    help          Show this help message

WORKFLOW:
    1. Initial Setup:
       ./demo-reset.sh init

    2. Start Demo (Java 8 state):
       ./demo-reset.sh baseline
       ./build-and-run.sh

    3. Perform Modernization:
       ./demo-reset.sh modernized
       # Make your modernization changes
       ./demo-reset.sh save

    4. Reset for Next Demo:
       ./demo-reset.sh baseline

BRANCHES:
    $BASELINE_BRANCH    - Java 8 + WebSphere Traditional
    $MODERNIZED_BRANCH       - Java 17/21 + Liberty (after upgrade)

EOF
}

# Main script logic
case "${1:-help}" in
    init)
        init_demo
        ;;
    status)
        show_status
        ;;
    baseline)
        switch_to_baseline
        ;;
    modernized)
        switch_to_modernized
        ;;
    save)
        save_changes
        ;;
    clean)
        clean_containers
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac

# Made with Bob

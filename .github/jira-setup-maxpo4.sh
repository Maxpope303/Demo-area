#!/bin/bash

################################################################################
# Jira Project Setup Script - MAXPO4 Project
# 
# Pre-configured for your Jira instance
# Project: MAXPO4
# Instance: home.atlassian.com
#
# Usage:
#   1. Make executable: chmod +x jira-setup-maxpo4.sh
#   2. Run: ./jira-setup-maxpo4.sh
#   3. Enter your email and API token when prompted
################################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Pre-configured values
PROJECT_KEY="MAXPO4"
JIRA_BASE_URL="https://api.atlassian.com/ex/jira/fc8858ea-c073-4c75-9528-2ee52c4d5157"

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ ${NC}$1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_header() {
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""
}

# Function to check if jq is installed
check_dependencies() {
    if ! command -v jq &> /dev/null; then
        print_error "jq is not installed. Please install it first:"
        echo "  macOS: brew install jq"
        echo "  Linux: sudo apt-get install jq"
        exit 1
    fi
    
    if ! command -v curl &> /dev/null; then
        print_error "curl is not installed. Please install it first."
        exit 1
    fi
}

# Function to get user credentials
get_jira_credentials() {
    print_header "Jira Authentication"
    
    echo "Pre-configured for:"
    echo "  Project: $PROJECT_KEY"
    echo "  Instance: home.atlassian.com"
    echo ""
    
    read -p "Your Jira Email: " JIRA_EMAIL
    
    read -sp "Your Jira API Token: " JIRA_API_TOKEN
    echo ""
    echo ""
    
    print_info "Credentials saved. Testing connection..."
}

# Function to test Jira connection
test_connection() {
    local response=$(curl -s -w "\n%{http_code}" -u "$JIRA_EMAIL:$JIRA_API_TOKEN" \
        "$JIRA_BASE_URL/rest/api/3/myself")
    
    local http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Connection successful!"
        local user_name=$(echo "$response" | head -n-1 | jq -r '.displayName')
        print_info "Logged in as: $user_name"
        return 0
    else
        print_error "Connection failed (HTTP $http_code)"
        echo "$response" | head -n-1 | jq -r '.errorMessages[]? // .message? // "Unknown error"'
        return 1
    fi
}

# Function to get project ID
get_project_id() {
    local response=$(curl -s -u "$JIRA_EMAIL:$JIRA_API_TOKEN" \
        "$JIRA_BASE_URL/rest/api/3/project/$PROJECT_KEY")
    
    PROJECT_ID=$(echo "$response" | jq -r '.id // empty')
    
    if [ -z "$PROJECT_ID" ]; then
        print_error "Project '$PROJECT_KEY' not found."
        echo "Response: $response"
        exit 1
    fi
    
    print_success "Found project: $PROJECT_KEY (ID: $PROJECT_ID)"
}

# Function to get issue type IDs
get_issue_types() {
    local response=$(curl -s -u "$JIRA_EMAIL:$JIRA_API_TOKEN" \
        "$JIRA_BASE_URL/rest/api/3/issuetype")
    
    EPIC_TYPE_ID=$(echo "$response" | jq -r '.[] | select(.name=="Epic") | .id')
    STORY_TYPE_ID=$(echo "$response" | jq -r '.[] | select(.name=="Story") | .id')
    TASK_TYPE_ID=$(echo "$response" | jq -r '.[] | select(.name=="Task") | .id')
    
    # Fallback to generic types if specific ones not found
    if [ -z "$STORY_TYPE_ID" ]; then
        STORY_TYPE_ID=$(echo "$response" | jq -r '.[0].id')
    fi
    if [ -z "$TASK_TYPE_ID" ]; then
        TASK_TYPE_ID=$(echo "$response" | jq -r '.[1].id')
    fi
    
    print_info "Issue types: Epic=$EPIC_TYPE_ID, Story=$STORY_TYPE_ID, Task=$TASK_TYPE_ID"
}

# Function to create an issue
create_issue() {
    local issue_type=$1
    local summary=$2
    local description=$3
    local parent_key=$4
    local labels=$5
    
    local json_data=$(cat <<EOF
{
  "fields": {
    "project": {
      "id": "$PROJECT_ID"
    },
    "summary": "$summary",
    "description": {
      "type": "doc",
      "version": 1,
      "content": [
        {
          "type": "paragraph",
          "content": [
            {
              "type": "text",
              "text": "$description"
            }
          ]
        }
      ]
    },
    "issuetype": {
      "id": "$issue_type"
    }
EOF
)
    
    # Add parent if provided (for stories under epic)
    if [ ! -z "$parent_key" ]; then
        json_data="$json_data,
    \"parent\": {
      \"key\": \"$parent_key\"
    }"
    fi
    
    # Add labels if provided
    if [ ! -z "$labels" ]; then
        json_data="$json_data,
    \"labels\": [\"$labels\"]"
    fi
    
    json_data="$json_data
  }
}"
    
    local response=$(curl -s -X POST -u "$JIRA_EMAIL:$JIRA_API_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$json_data" \
        "$JIRA_BASE_URL/rest/api/3/issue")
    
    local issue_key=$(echo "$response" | jq -r '.key // empty')
    
    if [ ! -z "$issue_key" ]; then
        echo "$issue_key"
        return 0
    else
        print_error "Failed to create issue: $summary"
        echo "$response" | jq -r '.errors // .errorMessages // .'
        return 1
    fi
}

# Function to create epic
create_epic() {
    print_header "Creating Epic"
    
    local summary="WebSphere to Liberty Modernization"
    local description="Complete modernization project to migrate Simple Pharmacy application from WebSphere Traditional (Java 8) to Open Liberty (Java 17). Includes CI/CD automation, containerization, and security improvements."
    
    if [ ! -z "$EPIC_TYPE_ID" ]; then
        EPIC_KEY=$(create_issue "$EPIC_TYPE_ID" "$summary" "$description" "" "modernization")
        
        if [ ! -z "$EPIC_KEY" ]; then
            print_success "Created Epic: $EPIC_KEY - $summary"
        fi
    else
        print_warning "Epic type not available, skipping epic creation"
        EPIC_KEY=""
    fi
}

# Function to create all stories
create_stories() {
    print_header "Creating User Stories"
    
    declare -a stories=(
        "CI/CD Pipeline Setup|Setup automated CI/CD pipeline with GitHub Actions including build, test, security scanning, and deployment automation.|cicd,automation"
        "Java 17 Migration|Migrate application from Java 8 to Java 17, update dependencies, and ensure compatibility with modern Java features.|java17,migration"
        "Unit Test Coverage|Implement comprehensive unit tests for all components with minimum 30% code coverage using JUnit 5.|testing,quality"
        "Security Scanning Integration|Integrate OWASP dependency check and SpotBugs for automated security vulnerability scanning.|security,scanning"
        "Container Image Build|Create Dockerfile and automate container image builds using Podman with multi-stage builds.|containers,docker"
        "Release Automation|Implement automated release process with version tagging, changelog generation, and artifact publishing.|release,automation"
        "Slack Notifications|Setup Slack integration for build status, deployment notifications, and release announcements.|notifications,slack"
        "Jira Integration|Integrate Jira with GitHub Actions for automatic ticket updates and release note generation.|jira,integration"
    )
    
    for story_data in "${stories[@]}"; do
        IFS='|' read -r summary description labels <<< "$story_data"
        
        local story_key=$(create_issue "$STORY_TYPE_ID" "$summary" "$description" "$EPIC_KEY" "$labels")
        
        if [ ! -z "$story_key" ]; then
            print_success "Created Story: $story_key - $summary"
            sleep 1  # Rate limiting
        fi
    done
}

# Function to create sample tasks
create_tasks() {
    print_header "Creating Sample Tasks"
    
    declare -a tasks=(
        "Configure GitHub Actions workflow|Setup initial ci-cd.yml workflow file with basic build and test jobs|cicd"
        "Add code coverage reporting|Integrate JaCoCo for code coverage with 30% minimum threshold|testing"
        "Setup Dependabot|Configure dependabot.yml for automated dependency updates|automation"
        "Create Dockerfile|Write multi-stage Dockerfile for Liberty runtime|containers"
        "Generate API token|Create Jira API token for GitHub Actions integration|jira"
    )
    
    for task_data in "${tasks[@]}"; do
        IFS='|' read -r summary description labels <<< "$task_data"
        
        local task_key=$(create_issue "$TASK_TYPE_ID" "$summary" "$description" "" "$labels")
        
        if [ ! -z "$task_key" ]; then
            print_success "Created Task: $task_key - $summary"
            sleep 1  # Rate limiting
        fi
    done
}

# Function to display summary
display_summary() {
    print_header "Setup Complete!"
    
    echo "Your Jira project has been populated with:"
    echo ""
    echo "  📋 1 Epic: WebSphere to Liberty Modernization"
    echo "  📝 8 User Stories covering the full modernization"
    echo "  ✓ 5 Sample Tasks for immediate work"
    echo ""
    echo "Next steps:"
    echo "  1. Visit: https://home.atlassian.com/o/5203a3ef-d23f-4df9-902b-ad965a9db090/s/fc8858ea-c073-4c75-9528-2ee52c4d5157/project/$PROJECT_KEY"
    echo "  2. Review and prioritize the stories"
    echo "  3. Start working on tasks"
    echo "  4. Use commit messages like: '$PROJECT_KEY-1: Your commit message'"
    echo ""
    echo "GitHub Secrets to add:"
    echo "  JIRA_BASE_URL=$JIRA_BASE_URL"
    echo "  JIRA_USER_EMAIL=$JIRA_EMAIL"
    echo "  JIRA_API_TOKEN=<your_token>"
    echo "  JIRA_PROJECT_KEY=$PROJECT_KEY"
    echo ""
    print_success "All done! Your Jira project is ready."
}

# Main execution
main() {
    clear
    print_header "Jira Project Setup for MAXPO4"
    
    print_info "This script will populate your MAXPO4 project with modernization tasks."
    echo ""
    
    # Check dependencies
    check_dependencies
    
    # Get credentials
    get_jira_credentials
    
    # Test connection
    if ! test_connection; then
        print_error "Setup aborted due to connection failure."
        exit 1
    fi
    
    echo ""
    read -p "Continue with project setup? (y/n): " confirm
    if [[ ! $confirm =~ ^[Yy]$ ]]; then
        print_warning "Setup cancelled by user."
        exit 0
    fi
    
    # Get project details
    get_project_id
    get_issue_types
    
    # Create issues
    create_epic
    create_stories
    create_tasks
    
    # Display summary
    display_summary
}

# Run main function
main

# Made with Bob

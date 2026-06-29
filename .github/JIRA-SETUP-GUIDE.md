# Jira Setup Guide

Complete guide for setting up Jira integration with your Simple Pharmacy modernization project.

## 🚀 Quick Start

### Prerequisites

1. **Jira Account** (Free tier works fine)
   - Sign up at: https://www.atlassian.com/software/jira/free
   - Or use existing account

2. **Required Tools**
   ```bash
   # Check if installed
   jq --version    # JSON processor
   curl --version  # HTTP client
   
   # Install if needed (macOS)
   brew install jq curl
   
   # Install if needed (Linux)
   sudo apt-get install jq curl
   ```

3. **Jira API Token**
   - Go to: https://id.atlassian.com/manage-profile/security/api-tokens
   - Click "Create API token"
   - Name it: "GitHub Actions"
   - Copy and save the token securely

---

## 📋 Step 1: Create Jira Project (If Needed)

If you don't have a project yet:

1. **Log into Jira** → Click "Projects" → "Create project"
2. **Choose template**: "Scrum" or "Kanban" (both work)
3. **Project details**:
   - **Name**: `Simple Pharmacy Modernization`
   - **Key**: `PHARM` (or `MOD`, `DEMO` - your choice)
   - **Type**: Team-managed or Company-managed
4. **Click**: "Create"

---

## 🤖 Step 2: Run Automated Setup Script

The easiest way to populate your Jira project:

```bash
# Navigate to the .github directory
cd simple-pharmacy-twas-j8-struts-V2/.github

# Make script executable
chmod +x jira-setup.sh

# Run the setup script
./jira-setup.sh
```

### What the Script Does

The script will:
1. ✅ Test your Jira connection
2. ✅ Create 1 Epic: "WebSphere to Liberty Modernization"
3. ✅ Create 8 User Stories covering:
   - CI/CD Pipeline Setup
   - Java 17 Migration
   - Unit Test Coverage
   - Security Scanning Integration
   - Container Image Build
   - Release Automation
   - Slack Notifications
   - Jira Integration
4. ✅ Create 5 Sample Tasks for immediate work
5. ✅ Add proper labels and descriptions

### Script Prompts

You'll be asked for:
```
Jira Base URL: https://yoursite.atlassian.net
Jira Email: your-email@gmail.com
Jira API Token: [paste your token]
Project Key: PHARM
```

---

## 🔧 Step 3: Configure GitHub Secrets

Add these secrets to your GitHub repository:

**Go to**: Repository → Settings → Secrets and variables → Actions → New repository secret

### Required Secrets

| Secret Name | Value | Example |
|-------------|-------|---------|
| `JIRA_BASE_URL` | Your Jira URL | `https://yoursite.atlassian.net` |
| `JIRA_USER_EMAIL` | Your Jira email | `your-email@gmail.com` |
| `JIRA_API_TOKEN` | Your API token | `ATATT3xFfGF0...` |
| `JIRA_PROJECT_KEY` | Your project key | `PHARM` |

### How to Add Secrets

```bash
# Via GitHub UI:
1. Go to: https://github.com/YOUR-USERNAME/YOUR-REPO/settings/secrets/actions
2. Click "New repository secret"
3. Name: JIRA_BASE_URL
4. Value: https://yoursite.atlassian.net
5. Click "Add secret"
6. Repeat for other secrets
```

---

## 📝 Step 4: Test Jira Integration

### Manual Test

Test your Jira API access:

```bash
# Set your credentials
export JIRA_URL="https://yoursite.atlassian.net"
export JIRA_EMAIL="your-email@gmail.com"
export JIRA_TOKEN="your_api_token"

# Test authentication
curl -u $JIRA_EMAIL:$JIRA_TOKEN \
  "$JIRA_URL/rest/api/3/myself" | jq .

# Get a ticket
curl -u $JIRA_EMAIL:$JIRA_TOKEN \
  "$JIRA_URL/rest/api/3/issue/PHARM-1" | jq .

# Add a test comment
curl -X POST -u $JIRA_EMAIL:$JIRA_TOKEN \
  -H "Content-Type: application/json" \
  -d '{
    "body": {
      "type": "doc",
      "version": 1,
      "content": [{
        "type": "paragraph",
        "content": [{
          "type": "text",
          "text": "Test comment from API"
        }]
      }]
    }
  }' \
  "$JIRA_URL/rest/api/3/issue/PHARM-1/comment"
```

### Test with GitHub Actions

Create a test commit:

```bash
# Use your project key in commit message
git commit -m "PHARM-1: Test Jira integration"
git push

# Check GitHub Actions logs
# Should see Jira comment added to PHARM-1
```

---

## 🔄 Step 5: Workflow Integration

The Jira integration will automatically:

### On Every Commit/PR
- Extract issue key from commit message (e.g., `PHARM-1`)
- Add comment to Jira ticket with:
  - Build status (✅ Success / ❌ Failed)
  - Test results
  - Code coverage
  - Link to GitHub Actions run

### On PR Events
- **PR Opened** → Transition ticket to "In Review"
- **PR Merged** → Transition ticket to "Done"
- **PR Closed** → Add comment with closure reason

### On Release
- Query Jira for tickets in release
- Generate changelog from ticket summaries
- Add release link to all tickets

---

## 📊 Jira Workflow Configuration

### Recommended Workflow States

```
To Do → In Progress → In Review → Done
```

### Transition Configuration

If your workflow uses different names, update these in GitHub secrets:

```bash
JIRA_TRANSITION_IN_PROGRESS=11  # ID for "In Progress"
JIRA_TRANSITION_IN_REVIEW=21    # ID for "In Review"
JIRA_TRANSITION_DONE=31         # ID for "Done"
```

### Find Transition IDs

```bash
# Get available transitions for a ticket
curl -u $JIRA_EMAIL:$JIRA_TOKEN \
  "$JIRA_URL/rest/api/3/issue/PHARM-1/transitions" | jq .
```

---

## 💡 Usage Examples

### Commit Message Format

Always include the Jira issue key:

```bash
# Good examples
git commit -m "PHARM-1: Setup GitHub Actions workflow"
git commit -m "PHARM-2: Add unit tests for MedicineAction"
git commit -m "PHARM-3: Migrate to Java 17"

# Also works
git commit -m "feat: Add prescription feature (PHARM-4)"
git commit -m "fix: Resolve null pointer in OrderAction [PHARM-5]"

# Won't trigger Jira integration
git commit -m "Update README"  # No issue key
```

### Branch Naming

Optional but recommended:

```bash
git checkout -b feature/PHARM-1-github-actions
git checkout -b bugfix/PHARM-10-order-validation
```

---

## 🎯 What Gets Created

### Epic Structure

```
📋 PHARM-1: WebSphere to Liberty Modernization
   ├── 📝 PHARM-2: CI/CD Pipeline Setup
   ├── 📝 PHARM-3: Java 17 Migration
   ├── 📝 PHARM-4: Unit Test Coverage
   ├── 📝 PHARM-5: Security Scanning Integration
   ├── 📝 PHARM-6: Container Image Build
   ├── 📝 PHARM-7: Release Automation
   ├── 📝 PHARM-8: Slack Notifications
   └── 📝 PHARM-9: Jira Integration
```

### Sample Tasks

```
✓ PHARM-10: Configure GitHub Actions workflow
✓ PHARM-11: Add code coverage reporting
✓ PHARM-12: Setup Dependabot
✓ PHARM-13: Create Dockerfile
✓ PHARM-14: Generate API token
```

---

## 🔍 Troubleshooting

### Connection Failed

**Problem**: Script can't connect to Jira

**Solutions**:
```bash
# Check URL format (no trailing slash)
✓ https://yoursite.atlassian.net
✗ https://yoursite.atlassian.net/

# Verify API token is correct
# Regenerate if needed at:
# https://id.atlassian.com/manage-profile/security/api-tokens

# Test manually
curl -u your-email@gmail.com:your_token \
  https://yoursite.atlassian.net/rest/api/3/myself
```

### Project Not Found

**Problem**: "Project 'PHARM' not found"

**Solutions**:
1. Verify project exists in Jira
2. Check project key is correct (case-sensitive)
3. Ensure you have access to the project

### Permission Denied

**Problem**: Can't create issues

**Solutions**:
1. Check your Jira permissions
2. Ensure you're a project member
3. Verify API token has correct scopes

### Rate Limiting

**Problem**: "Too many requests"

**Solutions**:
- Script includes 1-second delays
- Jira Cloud limit: 10 req/sec per user
- Wait a minute and retry

---

## 🔐 Security Best Practices

1. ✅ **Never commit credentials** to Git
2. ✅ **Use GitHub Secrets** for sensitive data
3. ✅ **Rotate API tokens** every 90 days
4. ✅ **Use separate token** for CI/CD (not personal)
5. ✅ **Enable audit logging** in Jira
6. ✅ **Review permissions** regularly

---

## 📚 Additional Resources

- **Jira REST API**: https://developer.atlassian.com/cloud/jira/platform/rest/v3/
- **GitHub Actions**: https://docs.github.com/en/actions
- **Atlassian Marketplace**: https://marketplace.atlassian.com/
- **API Token Management**: https://id.atlassian.com/manage-profile/security/api-tokens

---

## 🆘 Getting Help

If you encounter issues:

1. **Check script output** for error messages
2. **Review GitHub Actions logs** in the Actions tab
3. **Test API manually** with curl commands
4. **Verify secrets** are set correctly in GitHub
5. **Check Jira audit log** for API activity

---

## 🎉 Success Checklist

- [ ] Jira project created
- [ ] API token generated
- [ ] Setup script executed successfully
- [ ] GitHub secrets configured
- [ ] Test commit with issue key pushed
- [ ] Jira ticket updated automatically
- [ ] Workflow transitions working

Once all checked, your Jira integration is complete! 🚀

---

**Last Updated**: 2026-06-11  
**Maintained By**: DevOps Team  
**Version**: 1.0.0

<!-- Made with Bob -->
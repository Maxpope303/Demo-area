# 🚀 Jira Integration - Quick Start

Get your Jira integration up and running in 5 minutes!

## ⚡ Super Quick Setup

### 1. Get Your Jira Details (2 minutes)

```bash
# You need these 4 things:
1. Jira URL:    https://yoursite.atlassian.net
2. Your Email:  your-email@gmail.com
3. API Token:   (generate at link below)
4. Project Key: PHARM (or create project first)
```

**Generate API Token**: https://id.atlassian.com/manage-profile/security/api-tokens
- Click "Create API token"
- Name: "GitHub Actions"
- Copy the token

### 2. Run Setup Script (2 minutes)

```bash
# Navigate to project
cd simple-pharmacy-twas-j8-struts-V2/.github

# Run setup
./jira-setup.sh

# Enter your details when prompted
```

The script creates:
- ✅ 1 Epic
- ✅ 8 User Stories
- ✅ 5 Tasks
- ✅ All with proper labels and descriptions

### 3. Add GitHub Secrets (1 minute)

Go to: **GitHub Repo → Settings → Secrets → Actions → New secret**

Add these 4 secrets:

| Name | Value |
|------|-------|
| `JIRA_BASE_URL` | `https://yoursite.atlassian.net` |
| `JIRA_USER_EMAIL` | `your-email@gmail.com` |
| `JIRA_API_TOKEN` | `your_token_here` |
| `JIRA_PROJECT_KEY` | `PHARM` |

### 4. Test It! (30 seconds)

```bash
# Make a commit with your project key
git commit -m "PHARM-1: Test Jira integration"
git push

# Check:
# 1. GitHub Actions runs
# 2. Jira ticket PHARM-1 gets a comment
# 3. Success! 🎉
```

---

## 📋 What You Get

### Automatic Jira Updates

**On Every Commit:**
- ✅ Extracts issue key from commit message
- ✅ Adds comment with build status
- ✅ Links to GitHub Actions run
- ✅ Shows test results and coverage

**On Pull Requests:**
- ✅ PR opened → Ticket moves to "In Review"
- ✅ PR merged → Ticket moves to "Done"
- ✅ PR closed → Adds comment with reason

**On Releases:**
- ✅ Queries Jira for tickets in release
- ✅ Generates changelog from tickets
- ✅ Links release to all tickets

---

## 💡 Usage Tips

### Commit Message Format

```bash
# ✅ Good - Will update Jira
git commit -m "PHARM-1: Add unit tests"
git commit -m "PHARM-2: Fix order validation"
git commit -m "feat: New feature (PHARM-3)"

# ❌ Bad - Won't update Jira
git commit -m "Update code"
git commit -m "Fix bug"
```

### Branch Naming (Optional)

```bash
git checkout -b feature/PHARM-1-github-actions
git checkout -b bugfix/PHARM-5-validation
```

---

## 🎯 Created Tickets

After running the setup script, you'll have:

```
📋 PHARM-1: WebSphere to Liberty Modernization (Epic)
   ├── 📝 PHARM-2: CI/CD Pipeline Setup
   ├── 📝 PHARM-3: Java 17 Migration
   ├── 📝 PHARM-4: Unit Test Coverage
   ├── 📝 PHARM-5: Security Scanning Integration
   ├── 📝 PHARM-6: Container Image Build
   ├── 📝 PHARM-7: Release Automation
   ├── 📝 PHARM-8: Slack Notifications
   └── 📝 PHARM-9: Jira Integration

✓ PHARM-10: Configure GitHub Actions workflow
✓ PHARM-11: Add code coverage reporting
✓ PHARM-12: Setup Dependabot
✓ PHARM-13: Create Dockerfile
✓ PHARM-14: Generate API token
```

---

## 🔧 Troubleshooting

### Script Fails

```bash
# Check dependencies
jq --version    # Install: brew install jq
curl --version

# Test connection manually
curl -u your-email@gmail.com:your_token \
  https://yoursite.atlassian.net/rest/api/3/myself
```

### No Jira Updates

```bash
# Check GitHub secrets are set
# Go to: Repo → Settings → Secrets → Actions

# Verify commit has issue key
git log --oneline | grep PHARM

# Check GitHub Actions logs
# Go to: Actions tab → Latest run → View logs
```

### Wrong Project Key

```bash
# Find your project key in Jira
# Go to: Project Settings → Details
# Look for "Key" field (e.g., PHARM, MOD, DEMO)
```

---

## 📚 Full Documentation

For detailed setup and advanced features:
- **Complete Guide**: [JIRA-SETUP-GUIDE.md](JIRA-SETUP-GUIDE.md)
- **Workflow File**: [workflows/jira-integration.yml](workflows/jira-integration.yml)
- **Setup Script**: [jira-setup.sh](jira-setup.sh)

---

## ✅ Success Checklist

- [ ] API token generated
- [ ] Setup script executed
- [ ] GitHub secrets added
- [ ] Test commit pushed
- [ ] Jira ticket updated
- [ ] Integration working! 🎉

---

**Time to Complete**: ~5 minutes  
**Difficulty**: Easy  
**Prerequisites**: Jira account (free tier works)

<!-- Made with Bob -->
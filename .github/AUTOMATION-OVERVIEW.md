# CI/CD Automation Overview

## 🚀 Complete Automation Suite

This repository includes a comprehensive CI/CD automation setup with:

### 1. 🔄 Continuous Integration & Deployment
**File**: `.github/workflows/ci-cd.yml`

- ✅ Automated testing with JUnit 5
- 📊 Code coverage tracking (30% minimum)
- 🔒 Security scanning (OWASP, SpotBugs)
- 📦 Podman container builds
- ✋ Manual approval gates for production
- 💬 Slack notifications for build status

**Triggers**: Push/PR to `demo-java8-baseline`, `demo-modernized`, `main`

### 2. 📦 Release Management
**File**: `.github/workflows/release.yml`

- 🎉 Automated release creation
- 📝 Auto-generated release notes from commits
- 🐳 Container image publishing to GHCR
- 📊 SBOM (Software Bill of Materials) generation
- ✅ Checksum generation (SHA256, MD5)
- 💬 Slack notifications for releases

**Triggers**: Git tags (`v*.*.*`) or manual workflow dispatch

### 3. 🤖 Dependency Management
**File**: `.github/dependabot.yml`

- 🔍 Weekly dependency scans (Mondays at 9 AM)
- 📦 Automated PR creation for updates
- 🏷️ Grouped updates by category
- 🔒 Security-first prioritization
- ✅ Auto-labeling and assignment

**Scans**: Maven dependencies, GitHub Actions, Docker images

### 4. 💬 Slack Integration
**Setup Guide**: `.github/workflows/SLACK-SETUP.md`

- ✅ Build success notifications
- ❌ Build failure alerts
- 🚀 Deployment approval notifications
- 🎉 Release announcements
- 📊 Rich formatted messages with links

## Quick Start Guide

### Initial Setup (One-time)

1. **Configure Slack Notifications**
   ```bash
   # Follow the guide in .github/workflows/SLACK-SETUP.md
   # Add SLACK_WEBHOOK_URL to GitHub Secrets
   ```

2. **Enable Dependabot**
   ```bash
   # Already configured - just verify in Settings → Security
   # Dependabot will start creating PRs automatically
   ```

3. **Set Up Environments**
   ```bash
   # Go to Settings → Environments
   # Create: production-approval
   # Add required reviewers
   ```

### Daily Workflow

**For Developers:**
```bash
# 1. Create feature branch
git checkout -b feature/new-feature

# 2. Make changes and commit
git add .
git commit -m "feat: add new feature"

# 3. Push and create PR
git push origin feature/new-feature
# Create PR in GitHub

# 4. CI runs automatically
# - Tests run
# - Coverage checked
# - Security scanned
# - Slack notification sent

# 5. After approval, merge
# CI runs again on main branch
```

**For Releases:**
```bash
# 1. Ensure main is ready
git checkout main
git pull origin main

# 2. Create version tag
git tag -a v1.0.0 -m "Release v1.0.0"

# 3. Push tag
git push origin v1.0.0

# 4. Release workflow runs automatically
# - Creates GitHub Release
# - Builds and uploads WAR
# - Publishes container image
# - Generates SBOM
# - Sends Slack notifications
```

## Workflow Diagrams

### CI/CD Pipeline Flow

```
┌─────────────────┐
│   Push/PR       │
│   to Branch     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Setup Config   │
│  (Java version) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Build & Test    │
│ - Compile       │
│ - Unit Tests    │
│ - Coverage      │
└────────┬────────┘
         │
         ├──────────────┬──────────────┐
         ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Code Quality │ │  Container   │ │  Coverage    │
│ - SpotBugs   │ │  Build       │ │  Gate        │
│ - OWASP      │ │  - Podman    │ │  - 30% min   │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │
       └────────────────┴────────────────┘
                        │
                        ▼
              ┌─────────────────┐
              │  Approval Gate  │
              │  (if modernized)│
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Slack Notify    │
              │ - Success/Fail  │
              └─────────────────┘
```

### Release Pipeline Flow

```
┌─────────────────┐
│  Git Tag Push   │
│  (v*.*.*)       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Create Release  │
│ - Gen changelog │
│ - Create GH rel │
│ - Slack notify  │
└────────┬────────┘
         │
         ├──────────────┬──────────────┐
         ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Build WAR    │ │ Build Image  │ │ Generate     │
│ - Package    │ │ - Podman     │ │ SBOM         │
│ - Checksums  │ │ - Push GHCR  │ │ - SPDX       │
│ - Upload     │ │ - Tag latest │ │ - CycloneDX  │
└──────────────┘ └──────────────┘ └──────────────┘
         │              │              │
         └──────────────┴──────────────┘
                        │
                        ▼
              ┌─────────────────┐
              │ Slack Notify    │
              │ Release Complete│
              └─────────────────┘
```

### Dependabot Flow

```
┌─────────────────┐
│  Monday 9 AM    │
│  Weekly Scan    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Check Updates   │
│ - Maven deps    │
│ - GH Actions    │
│ - Docker imgs   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Group Updates   │
│ - Struts        │
│ - Testing       │
│ - Security      │
│ - Build tools   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Create PRs      │
│ - Auto-label    │
│ - Assign team   │
│ - Run CI        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Team Review     │
│ & Merge         │
└─────────────────┘
```

## Notification Examples

### Build Success
```
✅ Build & Test Passed

Repository: org/simple-pharmacy
Branch: demo-modernized
Java Version: 17
Triggered by: developer

[View Workflow] [View Commit]
```

### Build Failure
```
❌ Build & Test Failed

Repository: org/simple-pharmacy
Branch: main
Java Version: 17
Triggered by: developer

⚠️ Action Required: Check logs

[View Logs]
```

### Release Created
```
🎉 New Release: v1.0.0

Repository: org/simple-pharmacy
Version: v1.0.0
Released by: maintainer
Type: Production

[View Release] [View Changelog]
```

### Release Complete
```
✅ Release Complete: v1.0.0

All artifacts built successfully!

WAR File: ✅ Uploaded
Container Image: ✅ Published
SBOM: ✅ Generated
Checksums: ✅ Available

Container: podman pull ghcr.io/org/simple-pharmacy:v1.0.0

[View Release]
```

## Configuration Files

| File | Purpose | Documentation |
|------|---------|---------------|
| `.github/workflows/ci-cd.yml` | Main CI/CD pipeline | [CI-CD-SETUP.md](../CI-CD-SETUP.md) |
| `.github/workflows/release.yml` | Release automation | [RELEASE-GUIDE.md](RELEASE-GUIDE.md) |
| `.github/dependabot.yml` | Dependency updates | [RELEASE-GUIDE.md](RELEASE-GUIDE.md) |
| `.github/workflows/SLACK-SETUP.md` | Slack integration | [SLACK-SETUP.md](workflows/SLACK-SETUP.md) |

## Secrets Required

| Secret Name | Purpose | Required For |
|-------------|---------|--------------|
| `SLACK_WEBHOOK_URL` | Slack notifications | All workflows (optional) |
| `GITHUB_TOKEN` | GitHub API access | Auto-provided by GitHub |

## Permissions Required

Workflows need these permissions:
- `contents: write` - Create releases, push tags
- `packages: write` - Publish container images
- `checks: write` - Publish test results
- `pull-requests: write` - Comment on PRs

## Monitoring & Metrics

### Key Metrics Tracked

1. **Build Metrics**
   - Build success rate
   - Average build time
   - Test pass rate
   - Code coverage percentage

2. **Security Metrics**
   - Vulnerabilities found
   - Dependencies with CVEs
   - Security scan results

3. **Release Metrics**
   - Release frequency
   - Time to release
   - Artifacts published

4. **Dependency Metrics**
   - Outdated dependencies
   - Security updates pending
   - Update PR merge time

### Viewing Metrics

- **GitHub Actions**: Actions tab → Workflow runs
- **Insights**: Insights tab → Dependency graph
- **Security**: Security tab → Dependabot alerts
- **Releases**: Releases section

## Troubleshooting

### Common Issues

**CI Pipeline Fails**
```bash
# Check logs in Actions tab
# Common causes:
# - Test failures
# - Coverage below threshold
# - Security vulnerabilities
# - Build errors
```

**Dependabot PRs Not Appearing**
```bash
# Verify configuration
cat .github/dependabot.yml

# Check Settings → Security → Dependabot
# Ensure Dependabot is enabled
```

**Slack Notifications Not Working**
```bash
# Verify secret exists
# Settings → Secrets → SLACK_WEBHOOK_URL

# Test webhook manually
curl -X POST -H 'Content-type: application/json' \
  --data '{"text":"Test"}' \
  $SLACK_WEBHOOK_URL
```

**Release Workflow Fails**
```bash
# Check tag format (must be v*.*.*)
git tag -l

# Verify permissions
# Settings → Actions → General → Workflow permissions
```

## Best Practices

### For Developers

1. ✅ **Run tests locally** before pushing
2. ✅ **Keep PRs small** and focused
3. ✅ **Write meaningful commit messages**
4. ✅ **Review Dependabot PRs** promptly
5. ✅ **Monitor CI failures** and fix quickly

### For Maintainers

1. ✅ **Review security alerts** weekly
2. ✅ **Merge Dependabot PRs** regularly
3. ✅ **Create releases** on schedule
4. ✅ **Monitor build metrics** for trends
5. ✅ **Update documentation** as needed

### For Operations

1. ✅ **Monitor Slack notifications**
2. ✅ **Verify deployments** after releases
3. ✅ **Keep secrets rotated** regularly
4. ✅ **Review workflow logs** for issues
5. ✅ **Maintain environment configs**

## Additional Resources

- **GitHub Actions Docs**: https://docs.github.com/en/actions
- **Dependabot Docs**: https://docs.github.com/en/code-security/dependabot
- **Slack API Docs**: https://api.slack.com/messaging/webhooks
- **Semantic Versioning**: https://semver.org/
- **SBOM Guide**: https://www.cisa.gov/sbom

## Support

For issues or questions:
1. Check workflow logs in Actions tab
2. Review documentation in `.github/` directory
3. Contact DevOps team via Slack
4. Create issue in repository

---

**Last Updated**: 2026-06-10  
**Maintained By**: DevOps Team  
**Version**: 1.0.0

<!-- Made with Bob -->
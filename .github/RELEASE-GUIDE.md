# Release Management Guide

## Overview

This repository uses automated release management with:
- 🤖 **Dependabot** - Automated dependency updates
- 📦 **GitHub Releases** - Automated release creation with changelogs
- 🐳 **Container Publishing** - Automatic image builds and publishing
- 📊 **SBOM Generation** - Software Bill of Materials for compliance
- 💬 **Slack Notifications** - Release announcements

## Creating a Release

### Method 1: Using Git Tags (Recommended)

1. **Ensure your code is ready**
   ```bash
   # Make sure all tests pass
   mvn clean verify
   
   # Ensure you're on the main branch
   git checkout main
   git pull origin main
   ```

2. **Create and push a version tag**
   ```bash
   # Create a tag (use semantic versioning)
   git tag -a v1.0.0 -m "Release version 1.0.0"
   
   # Push the tag to trigger the release workflow
   git push origin v1.0.0
   ```

3. **Monitor the release**
   - Go to Actions tab in GitHub
   - Watch the "Release Management" workflow
   - Check Slack for notifications

### Method 2: Manual Workflow Dispatch

1. **Go to GitHub Actions**
   - Navigate to Actions → Release Management
   - Click "Run workflow"

2. **Fill in the details**
   - **Branch**: Select the branch (usually `main`)
   - **Version**: Enter version (e.g., `v1.0.0`)
   - **Pre-release**: Check if this is a pre-release

3. **Run and monitor**
   - Click "Run workflow"
   - Monitor progress in Actions tab

## Semantic Versioning

Follow [Semantic Versioning](https://semver.org/) (MAJOR.MINOR.PATCH):

- **MAJOR** (v2.0.0): Breaking changes
  - API changes that break backward compatibility
  - Major architecture changes
  - Removal of deprecated features

- **MINOR** (v1.1.0): New features (backward compatible)
  - New functionality added
  - New endpoints or features
  - Performance improvements

- **PATCH** (v1.0.1): Bug fixes (backward compatible)
  - Bug fixes
  - Security patches
  - Documentation updates

### Examples:
```bash
# Major release (breaking changes)
git tag -a v2.0.0 -m "Release v2.0.0 - Migrated to Java 17"

# Minor release (new features)
git tag -a v1.1.0 -m "Release v1.1.0 - Added prescription management"

# Patch release (bug fixes)
git tag -a v1.0.1 -m "Release v1.0.1 - Fixed order validation bug"

# Pre-release versions
git tag -a v1.1.0-beta.1 -m "Release v1.1.0-beta.1"
git tag -a v2.0.0-rc.1 -m "Release v2.0.0-rc.1"
```

## What Gets Released

### Artifacts Included:

1. **WAR File** (`simple-pharmacy-{version}.war`)
   - Ready-to-deploy application
   - Includes all dependencies
   - SHA256 and MD5 checksums provided

2. **Container Image** (`ghcr.io/{org}/simple-pharmacy:{version}`)
   - Published to GitHub Container Registry
   - Tagged with version and `latest`
   - Ready for Podman/Docker deployment

3. **SBOM** (Software Bill of Materials)
   - SPDX format JSON
   - CycloneDX format JSON
   - Complete dependency list for compliance

4. **Source Code**
   - Automatic zip and tar.gz archives
   - Includes all source files

5. **Release Notes**
   - Auto-generated changelog
   - Commit history since last release
   - Installation instructions
   - Links to documentation

## Release Workflow Steps

The automated release process:

1. ✅ **Create Release**
   - Generate release notes from commits
   - Create GitHub release
   - Send Slack notification

2. 🔨 **Build Artifacts**
   - Compile and package WAR file
   - Generate checksums
   - Upload to release

3. 🐳 **Build Container**
   - Build container image
   - Push to GitHub Container Registry
   - Generate SBOM
   - Send completion notification

## Using Released Artifacts

### Deploy WAR File

```bash
# Download from GitHub Releases
wget https://github.com/{org}/{repo}/releases/download/v1.0.0/simple-pharmacy-v1.0.0.war

# Verify checksum
sha256sum -c simple-pharmacy-v1.0.0.war.sha256

# Deploy to Liberty
cp simple-pharmacy-v1.0.0.war /opt/ibm/wlp/usr/servers/defaultServer/dropins/
```

### Use Container Image

```bash
# Pull the image
podman pull ghcr.io/{org}/simple-pharmacy:v1.0.0

# Run the container
podman run -d -p 9080:9080 \
  --name simple-pharmacy \
  ghcr.io/{org}/simple-pharmacy:v1.0.0

# Or use latest
podman pull ghcr.io/{org}/simple-pharmacy:latest
```

### Docker Compose

```yaml
version: '3.8'
services:
  pharmacy:
    image: ghcr.io/{org}/simple-pharmacy:v1.0.0
    ports:
      - "9080:9080"
    environment:
      - JAVA_OPTS=-Xmx512m
```

## Dependabot Configuration

### What Dependabot Does

- 🔍 **Scans dependencies** weekly (every Monday at 9 AM)
- 📦 **Creates PRs** for updates automatically
- 🏷️ **Groups updates** by category (Struts, Testing, Security, etc.)
- 🔒 **Security updates** are prioritized
- ✅ **Auto-labels** PRs for easy filtering

### Dependency Categories

1. **Struts Framework** - All Struts-related updates
2. **Testing** - JUnit, AssertJ, Mockito
3. **Security** - OWASP, SpotBugs
4. **Build Tools** - Maven plugins, JaCoCo
5. **GitHub Actions** - Workflow action updates
6. **Docker** - Base image updates

### Managing Dependabot PRs

**Review a Dependabot PR:**
```bash
# Check the PR in GitHub
# Review the changelog and breaking changes
# Ensure CI passes

# If approved, merge via GitHub UI or:
gh pr merge {pr-number} --squash
```

**Auto-merge minor updates:**
```bash
# Enable auto-merge for patch updates
gh pr merge {pr-number} --auto --squash
```

**Close unwanted updates:**
```bash
# Comment on the PR:
@dependabot ignore this major version
# or
@dependabot ignore this dependency
```

### Dependabot Commands

Comment on Dependabot PRs with:
- `@dependabot rebase` - Rebase the PR
- `@dependabot recreate` - Recreate the PR
- `@dependabot merge` - Merge the PR (if CI passes)
- `@dependabot cancel merge` - Cancel auto-merge
- `@dependabot close` - Close the PR
- `@dependabot ignore this dependency` - Ignore future updates
- `@dependabot ignore this major version` - Ignore major version updates

## Release Checklist

Before creating a release:

- [ ] All tests pass locally (`mvn clean verify`)
- [ ] Code reviewed and approved
- [ ] Documentation updated
- [ ] CHANGELOG.md updated (if maintained separately)
- [ ] Version number follows semantic versioning
- [ ] No known critical bugs
- [ ] Security scans pass
- [ ] Performance tested (if applicable)

After release:

- [ ] Verify release appears in GitHub Releases
- [ ] Check Slack notification received
- [ ] Verify container image published
- [ ] Test deployment with released artifacts
- [ ] Update deployment documentation if needed
- [ ] Announce release to stakeholders

## Rollback Procedure

If a release has issues:

1. **Immediate rollback**
   ```bash
   # Deploy previous version
   podman pull ghcr.io/{org}/simple-pharmacy:v1.0.0
   podman run -d -p 9080:9080 ghcr.io/{org}/simple-pharmacy:v1.0.0
   ```

2. **Mark release as pre-release**
   - Go to GitHub Releases
   - Edit the problematic release
   - Check "This is a pre-release"
   - Add warning to release notes

3. **Create hotfix release**
   ```bash
   # Fix the issue
   git checkout -b hotfix/v1.0.2
   # Make fixes
   git commit -m "fix: critical bug in order processing"
   git checkout main
   git merge hotfix/v1.0.2
   git tag -a v1.0.2 -m "Hotfix release v1.0.2"
   git push origin main v1.0.2
   ```

## Troubleshooting

### Release workflow fails

1. Check Actions logs for errors
2. Common issues:
   - Missing secrets (GITHUB_TOKEN, SLACK_WEBHOOK_URL)
   - Build failures (check Maven logs)
   - Container registry authentication issues

### Container push fails

```bash
# Verify authentication
echo "$GITHUB_TOKEN" | podman login ghcr.io -u USERNAME --password-stdin

# Check permissions
# Ensure workflow has packages: write permission
```

### Dependabot PRs not appearing

1. Check `.github/dependabot.yml` syntax
2. Verify repository settings allow Dependabot
3. Check Insights → Dependency graph → Dependabot

### SBOM generation fails

```bash
# Install syft locally to test
curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh -s -- -b /usr/local/bin

# Generate SBOM locally
syft dir:. -o spdx-json
```

## Best Practices

1. **Release Frequency**
   - Regular releases (weekly/bi-weekly for active development)
   - Hotfixes as needed for critical bugs
   - Major releases for breaking changes

2. **Version Tags**
   - Always use annotated tags (`git tag -a`)
   - Include meaningful messages
   - Never delete or modify published tags

3. **Release Notes**
   - Auto-generated but review before publishing
   - Add manual notes for breaking changes
   - Include migration guides for major versions

4. **Testing**
   - Test in staging before production release
   - Run full test suite before tagging
   - Verify container image works before release

5. **Communication**
   - Use Slack notifications for team awareness
   - Document breaking changes clearly
   - Provide upgrade instructions

## Additional Resources

- [Semantic Versioning](https://semver.org/)
- [GitHub Releases Documentation](https://docs.github.com/en/repositories/releasing-projects-on-github)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [SBOM Guide](https://www.cisa.gov/sbom)
- [Container Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Last Updated**: 2026-06-10  
**Maintained By**: DevOps Team

<!-- Made with Bob -->
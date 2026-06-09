# CI/CD Pipeline Setup Guide

## Overview

This document describes the automated CI/CD pipeline for the Java Modernization Demo application. The pipeline uses GitHub Actions with comprehensive testing, code coverage, security scanning, and Podman container builds.

## Pipeline Architecture

### Workflow Triggers
- **Push** to `demo-java8-baseline`, `demo-modernized`, or `main` branches
- **Pull Requests** to these branches
- **Manual trigger** via GitHub Actions UI

### Pipeline Stages

```
┌─────────────────────────────────────────────────────────────┐
│                    Setup Configuration                       │
│              (Determine Java version & runtime)              │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
┌───────────────┐          ┌────────────────┐
│ Build & Test  │          │ Code Quality   │
│  - Compile    │          │  - SpotBugs    │
│  - Unit Tests │          │  - OWASP Check │
│  - Coverage   │          └────────────────┘
└───────┬───────┘
        │
        ▼
┌────────────────────┐
│ Container Build    │
│  - Podman build    │
│  - Trivy scan      │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Coverage Gate     │
│  (50% minimum)     │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Approval Gate     │
│  (Modernized only) │
└────────────────────┘
```

## Features

### 🧪 Automated Testing
- **Unit Tests**: JUnit 5 with Mockito
- **Test Reports**: Published to GitHub Actions
- **Coverage Reports**: JaCoCo with 50% minimum threshold
- **Coverage Tracking**: Codecov integration

### 🔒 Security Scanning
- **OWASP Dependency Check**: Scans for vulnerable dependencies
- **Trivy Container Scanning**: Scans container images for vulnerabilities
- **SpotBugs**: Static code analysis for Java
- **SARIF Upload**: Security findings uploaded to GitHub Security tab

### 📦 Container Build (Podman)
- **Podman Native**: Uses Podman instead of Docker
- **Multi-architecture**: Supports x86_64 and ARM64
- **Image Artifacts**: Saved for deployment
- **Security Scanning**: Integrated Trivy scanning

### 📊 Code Quality
- **Coverage Enforcement**: Minimum 50% line coverage
- **PR Comments**: Coverage reports on pull requests
- **Quality Gates**: Automated checks before approval

### ✅ Approval Workflow
- **Environment Protection**: `production-approval` environment
- **Manual Approval**: Required for modernized branch deployments
- **Audit Trail**: All approvals logged

## Setup Instructions

### 1. GitHub Repository Setup

#### Enable GitHub Actions
1. Go to repository **Settings** → **Actions** → **General**
2. Enable "Allow all actions and reusable workflows"
3. Save changes

#### Create Environment for Approvals
1. Go to **Settings** → **Environments**
2. Click **New environment**
3. Name it: `production-approval`
4. Configure protection rules:
   - ✅ Required reviewers (add team members)
   - ✅ Wait timer: 0 minutes (or set delay)
   - ✅ Deployment branches: `demo-modernized` only
5. Save protection rules

### 2. Branch Protection Rules

Set up branch protection for `demo-modernized`:

1. Go to **Settings** → **Branches**
2. Add rule for `demo-modernized`
3. Enable:
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - Select required checks:
     - `Build & Test`
     - `Code Quality & Security Analysis`
     - `Container Build`
     - `Coverage Gate`
   - ✅ Require pull request reviews before merging
   - ✅ Dismiss stale pull request approvals when new commits are pushed

### 3. Secrets Configuration (Optional)

If using external services, add these secrets in **Settings** → **Secrets and variables** → **Actions**:

- `CODECOV_TOKEN` - For Codecov integration (optional)
- `SONAR_TOKEN` - For SonarCloud integration (optional)
- `REGISTRY_USERNAME` - For container registry (if pushing images)
- `REGISTRY_PASSWORD` - For container registry (if pushing images)

### 4. Local Testing

Test the pipeline locally before pushing:

```bash
# Run tests locally
cd simple-pharmacy-twas-j8-struts-V2
mvn clean test

# Generate coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run security checks
mvn spotbugs:check
mvn org.owasp:dependency-check-maven:check

# Build with Podman
podman build -t simple-pharmacy:test .
```

## Pipeline Workflow Details

### Job: Setup Configuration
**Purpose**: Determines Java version and runtime based on branch

- `demo-java8-baseline` → Java 8 + WebSphere Traditional
- `demo-modernized` → Java 17 + Liberty
- Other branches → Java 17 + Liberty

### Job: Build & Test
**Purpose**: Compile, test, and generate coverage reports

**Steps**:
1. Checkout code
2. Setup JDK (version from setup job)
3. Cache Maven dependencies
4. Compile application
5. Run unit tests
6. Generate JaCoCo coverage report
7. Upload coverage to Codecov
8. Publish test results
9. Package WAR file
10. Upload artifacts

**Artifacts**:
- `simple-pharmacy-java{version}` - WAR file
- `test-reports-java{version}` - Test and coverage reports

### Job: Code Quality & Security
**Purpose**: Static analysis and security scanning

**Steps**:
1. Run SpotBugs for code quality
2. Run OWASP Dependency Check
3. Upload security reports

**Artifacts**:
- `security-reports` - SpotBugs and OWASP reports

### Job: Container Build (Podman)
**Purpose**: Build and scan container images

**Steps**:
1. Download WAR artifact
2. Install Podman
3. Build container image
4. Scan with Trivy
5. Upload SARIF to GitHub Security
6. Save image as artifact

**Artifacts**:
- `container-image-java{version}` - Container image tar file

### Job: Coverage Gate
**Purpose**: Enforce minimum code coverage

**Requirements**:
- Minimum 50% line coverage
- Comments coverage on PRs
- Fails build if threshold not met

### Job: Approval Gate
**Purpose**: Manual approval for production deployments

**Conditions**:
- Only runs for `demo-modernized` branch
- Only on push events (not PRs)
- Requires all previous jobs to pass

**Environment**: `production-approval`

### Job: Pipeline Summary
**Purpose**: Generate comprehensive summary

**Output**: GitHub Actions summary with:
- Configuration details
- Job statuses
- Artifact links
- Coverage metrics

## Using the Pipeline

### For Java 8 Baseline Development

```bash
# Switch to baseline branch
git checkout demo-java8-baseline

# Make changes
# ... edit files ...

# Commit and push
git add .
git commit -m "Update baseline application"
git push origin demo-java8-baseline
```

**Pipeline runs automatically**:
- ✅ Builds with Java 8
- ✅ Runs tests
- ✅ Generates coverage
- ✅ Builds WebSphere Traditional container
- ✅ No approval required

### For Modernized Development

```bash
# Switch to modernized branch
git checkout demo-modernized

# Make modernization changes
# ... update to Java 17, Liberty, etc. ...

# Commit and push
git add .
git commit -m "Modernize to Java 17 and Liberty"
git push origin demo-modernized
```

**Pipeline runs automatically**:
- ✅ Builds with Java 17
- ✅ Runs tests
- ✅ Generates coverage
- ✅ Security scanning
- ✅ Builds Liberty container
- ⏸️ **Waits for approval**

**To approve deployment**:
1. Go to Actions tab
2. Click on the workflow run
3. Click "Review deployments"
4. Select `production-approval`
5. Click "Approve and deploy"

### Pull Request Workflow

```bash
# Create feature branch
git checkout -b feature/my-improvement demo-modernized

# Make changes
# ... edit files ...

# Push and create PR
git push origin feature/my-improvement
```

**On GitHub**:
1. Create Pull Request to `demo-modernized`
2. Pipeline runs automatically
3. Coverage report commented on PR
4. All checks must pass before merge
5. Requires reviewer approval

## Monitoring and Reports

### GitHub Actions UI
- **Actions Tab**: View all workflow runs
- **Summary**: Detailed pipeline summary
- **Artifacts**: Download build artifacts
- **Logs**: View detailed execution logs

### Security Tab
- **Code Scanning**: Trivy and SARIF results
- **Dependabot**: Dependency vulnerability alerts
- **Secret Scanning**: Exposed secrets detection

### Coverage Reports
- **Codecov**: https://codecov.io/gh/{org}/{repo}
- **PR Comments**: Inline coverage reports
- **Artifacts**: Download full HTML reports

## Troubleshooting

### Pipeline Fails on Tests

```bash
# Run tests locally
cd simple-pharmacy-twas-j8-struts-V2
mvn clean test

# Check specific test
mvn test -Dtest=MedicineTest

# View detailed output
mvn test -X
```

### Coverage Below Threshold

```bash
# Generate coverage report
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html

# Add more tests to increase coverage
```

### Container Build Fails

```bash
# Test Podman build locally
cd simple-pharmacy-twas-j8-struts-V2
podman build -t simple-pharmacy:test .

# Check Dockerfile syntax
podman build --no-cache -t simple-pharmacy:test .

# View build logs
podman build -t simple-pharmacy:test . 2>&1 | tee build.log
```

### Security Scan Failures

```bash
# Run OWASP check locally
mvn org.owasp:dependency-check-maven:check

# View report
open target/dependency-check-report.html

# Update suppressions if needed
# Edit owasp-suppressions.xml
```

### Approval Not Showing

**Check**:
1. Environment `production-approval` exists
2. Required reviewers are configured
3. Branch is `demo-modernized`
4. Event is `push` (not PR)
5. All previous jobs passed

## Maintenance

### Update Dependencies

```bash
# Check for updates
mvn versions:display-dependency-updates

# Update specific dependency
mvn versions:use-latest-versions -Dincludes=org.junit.jupiter:*

# Update plugins
mvn versions:display-plugin-updates
```

### Update Security Suppressions

Edit `owasp-suppressions.xml` to suppress false positives:

```xml
<suppress>
    <notes><![CDATA[
    Reason for suppression
    ]]></notes>
    <packageUrl regex="true">^pkg:maven/group/artifact@.*$</packageUrl>
    <cve>CVE-XXXX-XXXXX</cve>
</suppress>
```

### Adjust Coverage Threshold

Edit `pom.xml`:

```xml
<configuration>
    <rules>
        <rule>
            <element>PACKAGE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.60</minimum> <!-- Change from 0.50 -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

## Best Practices

### 1. Write Tests First
- Add tests for new features
- Maintain coverage above threshold
- Use meaningful test names

### 2. Review Security Reports
- Check OWASP reports regularly
- Update dependencies promptly
- Review Trivy scan results

### 3. Use Feature Branches
- Create branches from `demo-modernized`
- Use descriptive branch names
- Keep PRs focused and small

### 4. Monitor Pipeline Performance
- Review execution times
- Optimize slow tests
- Cache dependencies effectively

### 5. Document Changes
- Update CHANGELOG
- Add comments to suppressions
- Document breaking changes

## Support

### Resources
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Podman Documentation](https://docs.podman.io/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)

### Getting Help
1. Check pipeline logs in Actions tab
2. Review this documentation
3. Check DEMO-GUIDE.md for demo workflow
4. Contact DevOps team

---

**Pipeline Version**: 1.0  
**Last Updated**: 2026-06-09  
**Maintained By**: DevOps Team

<!-- Made with Bob -->
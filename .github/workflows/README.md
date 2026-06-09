# GitHub Actions Workflows

## Available Workflows

### ci-cd.yml - Main CI/CD Pipeline

**Automated Java Modernization Pipeline with Testing, Coverage, and Podman Support**

#### Triggers
- Push to `demo-java8-baseline`, `demo-modernized`, `main`
- Pull requests to these branches
- Manual workflow dispatch

#### Features
- ✅ Automated testing with JUnit 5
- 📊 Code coverage with JaCoCo (50% minimum)
- 🔒 Security scanning (OWASP, Trivy, SpotBugs)
- 📦 Podman container builds
- ✋ Manual approval gates for production
- 📈 Comprehensive reporting

#### Branch-Specific Behavior

| Branch | Java Version | Runtime | Approval Required |
|--------|-------------|---------|-------------------|
| `demo-java8-baseline` | Java 8 | WebSphere Traditional | No |
| `demo-modernized` | Java 17 | Liberty | Yes |
| `main` | Java 17 | Liberty | No |

#### Jobs Overview

1. **Setup** - Determines configuration based on branch
2. **Build & Test** - Compiles, tests, generates coverage
3. **Code Quality** - SpotBugs and OWASP checks
4. **Container Build** - Podman build with Trivy scan
5. **Coverage Gate** - Enforces 50% minimum coverage
6. **Approval Gate** - Manual approval for modernized branch
7. **Pipeline Summary** - Generates comprehensive report

#### Artifacts Generated

- `simple-pharmacy-java{version}` - WAR file
- `test-reports-java{version}` - Test and coverage reports
- `security-reports` - Security scan results
- `container-image-java{version}` - Container image

#### Required Secrets (Optional)

- `CODECOV_TOKEN` - For Codecov integration
- `SONAR_TOKEN` - For SonarCloud integration

#### Required Environments

- `production-approval` - For deployment approvals
  - Configure in Settings → Environments
  - Add required reviewers
  - Set deployment branch rules

## Quick Start

### 1. Enable GitHub Actions
Settings → Actions → General → Allow all actions

### 2. Create Environment
Settings → Environments → New environment → `production-approval`

### 3. Push Code
```bash
git push origin demo-java8-baseline
# or
git push origin demo-modernized
```

### 4. Monitor Pipeline
Actions tab → View workflow run

### 5. Approve Deployment (if needed)
Actions → Workflow run → Review deployments → Approve

## Local Testing

Test before pushing:

```bash
cd simple-pharmacy-twas-j8-struts-V2

# Run tests
mvn clean test

# Check coverage
mvn jacoco:report
open target/site/jacoco/index.html

# Security checks
mvn spotbugs:check
mvn org.owasp:dependency-check-maven:check

# Build container
podman build -t simple-pharmacy:test .
```

## Troubleshooting

### Pipeline Fails
1. Check Actions tab for logs
2. Review error messages
3. Test locally with Maven
4. Check CI-CD-SETUP.md for details

### Coverage Below Threshold
1. Add more unit tests
2. Review coverage report
3. Aim for 50%+ line coverage

### Approval Not Showing
1. Verify environment exists
2. Check branch is `demo-modernized`
3. Ensure all jobs passed
4. Verify push event (not PR)

## Documentation

- **Full Setup Guide**: [CI-CD-SETUP.md](../../CI-CD-SETUP.md)
- **Demo Guide**: [DEMO-GUIDE.md](../../DEMO-GUIDE.md)
- **GitHub Actions Docs**: https://docs.github.com/en/actions

---

**Maintained By**: DevOps Team  
**Last Updated**: 2026-06-09

<!-- Made with Bob -->
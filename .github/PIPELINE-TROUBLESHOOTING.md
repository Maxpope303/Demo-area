# 🔧 Pipeline Troubleshooting Guide

Comprehensive troubleshooting guide for CI/CD pipeline issues, performance problems, and operational concerns.

## Table of Contents

1. [Performance Issues](#performance-issues)
2. [Intermittent Failures](#intermittent-failures)
3. [Resource Constraints](#resource-constraints)
4. [Network Issues](#network-issues)
5. [Configuration Problems](#configuration-problems)
6. [Integration Issues](#integration-issues)
7. [Monitoring & Alerts](#monitoring--alerts)

---

## Performance Issues

### Slow Build Times

**Symptoms:**
- Pipeline takes >15 minutes to complete
- Maven dependency resolution is slow
- Container builds timeout

**Diagnosis:**
```bash
# Check build time breakdown
# Review GitHub Actions logs for timing

# Test locally with timing
time mvn clean install

# Check cache effectiveness
mvn dependency:resolve -X | grep "Downloaded"
```

**Solutions:**

#### 1. Optimize Maven Caching
```yaml
# .github/workflows/ci-cd.yml
- name: Cache Maven packages
  uses: actions/cache@v3
  with:
    path: |
      ~/.m2/repository
      ~/.m2/wrapper
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

#### 2. Parallel Execution
```yaml
# Run jobs in parallel
jobs:
  build:
    # ...
  
  code-quality:
    needs: setup  # Not build
    # Runs in parallel with build
  
  container-build:
    needs: setup  # Not build
    # Runs in parallel with build
```

#### 3. Skip Unnecessary Steps
```yaml
# Skip tests in package phase (already run in test phase)
- name: Package Application
  run: mvn package -DskipTests -B
```

#### 4. Use Incremental Builds
```bash
# Only rebuild changed modules
mvn clean install -pl :changed-module -am
```

**Expected Improvements:**
- Build time: 8-10 minutes (from 15+)
- Cache hit rate: >80%
- Parallel execution: 3-4 jobs concurrent

---

### Slow Test Execution

**Symptoms:**
- Test phase takes >5 minutes
- Individual tests are slow
- Timeout errors

**Diagnosis:**
```bash
# Run tests with timing
mvn test -Dsurefire.printSummary=true

# Identify slow tests
mvn test -X | grep "Time elapsed"

# Profile tests
mvn test -Dmaven.surefire.debug
```

**Solutions:**

#### 1. Parallel Test Execution
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

#### 2. Test Categorization
```java
// Separate fast and slow tests
@Tag("fast")
public class FastTest { }

@Tag("slow")
public class SlowTest { }
```

```bash
# Run only fast tests in CI
mvn test -Dgroups="fast"
```

#### 3. Mock External Dependencies
```java
// Use mocks instead of real services
@Mock
private ExternalService externalService;
```

---

## Intermittent Failures

### Flaky Tests

**Symptoms:**
- Tests pass/fail randomly
- "Works on my machine"
- Timing-dependent failures

**Common Causes:**
1. Race conditions
2. Shared state between tests
3. External service dependencies
4. Time-based assertions
5. Random data generation

**Solutions:**

#### 1. Identify Flaky Tests
```bash
# Run tests multiple times
for i in {1..10}; do
    mvn test -Dtest=SuspiciousTest || echo "Failed on run $i"
done
```

#### 2. Fix Race Conditions
```java
// ❌ Bad: Race condition
@Test
public void testAsync() {
    service.asyncOperation();
    assertEquals(expected, service.getResult());
}

// ✅ Good: Wait for completion
@Test
public void testAsync() {
    CompletableFuture<Result> future = service.asyncOperation();
    Result result = future.get(5, TimeUnit.SECONDS);
    assertEquals(expected, result);
}
```

#### 3. Isolate Tests
```java
// ❌ Bad: Shared state
private static List<String> data = new ArrayList<>();

// ✅ Good: Fresh state per test
@BeforeEach
public void setUp() {
    data = new ArrayList<>();
}
```

#### 4. Use Deterministic Data
```java
// ❌ Bad: Random data
String id = UUID.randomUUID().toString();

// ✅ Good: Deterministic data
String id = "test-id-123";
```

---

### Network Timeouts

**Symptoms:**
```
[ERROR] Connection timeout
[ERROR] Failed to download artifact
[ERROR] Unable to reach repository
```

**Solutions:**

#### 1. Increase Timeouts
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <forkedProcessTimeoutInSeconds>300</forkedProcessTimeoutInSeconds>
    </configuration>
</plugin>
```

#### 2. Configure Retries
```yaml
# .github/workflows/ci-cd.yml
- name: Build with Maven
  uses: nick-invision/retry@v2
  with:
    timeout_minutes: 10
    max_attempts: 3
    command: mvn clean install -B
```

#### 3. Use Mirror Repositories
```xml
<!-- settings.xml -->
<mirrors>
    <mirror>
        <id>central-mirror</id>
        <url>https://repo1.maven.org/maven2</url>
        <mirrorOf>central</mirrorOf>
    </mirror>
</mirrors>
```

---

## Resource Constraints

### Out of Memory Errors

**Symptoms:**
```
[ERROR] Java heap space
[ERROR] GC overhead limit exceeded
[ERROR] OutOfMemoryError
```

**Solutions:**

#### 1. Increase Heap Size
```yaml
# .github/workflows/ci-cd.yml
env:
  MAVEN_OPTS: -Xmx2048m -XX:MaxPermSize=512m
```

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-Xmx1024m</argLine>
    </configuration>
</plugin>
```

#### 2. Fork Tests
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>false</reuseForks>
    </configuration>
</plugin>
```

#### 3. Optimize Container Builds
```dockerfile
# Use multi-stage builds
FROM maven:3.8-jdk-8 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM ibmcom/websphere-traditional:latest
COPY --from=builder /app/target/*.war /work/config/
```

---

### Disk Space Issues

**Symptoms:**
```
[ERROR] No space left on device
[ERROR] Failed to write artifact
```

**Solutions:**

#### 1. Clean Up Artifacts
```yaml
- name: Clean up
  if: always()
  run: |
    rm -rf target/
    docker system prune -af
```

#### 2. Reduce Artifact Retention
```yaml
- name: Upload artifact
  uses: actions/upload-artifact@v4
  with:
    name: my-artifact
    path: target/*.war
    retention-days: 7  # Reduced from 30
```

#### 3. Use Sparse Checkouts
```yaml
- name: Checkout code
  uses: actions/checkout@v4
  with:
    sparse-checkout: |
      src/
      pom.xml
```

---

## Configuration Problems

### Environment Variable Issues

**Symptoms:**
- Variables not available in workflow
- Incorrect values
- Secrets not working

**Diagnosis:**
```yaml
- name: Debug Environment
  run: |
    echo "JAVA_HOME: $JAVA_HOME"
    echo "MAVEN_OPTS: $MAVEN_OPTS"
    env | sort
```

**Solutions:**

#### 1. Proper Secret Configuration
```yaml
# Set in repository settings
# Settings → Secrets and variables → Actions

# Use in workflow
env:
  JIRA_TOKEN: ${{ secrets.JIRA_API_TOKEN }}
```

#### 2. Environment Inheritance
```yaml
# Global environment
env:
  MAVEN_OPTS: -Xmx1024m

jobs:
  build:
    # Job-specific environment
    env:
      BUILD_ENV: production
    steps:
      # Step-specific environment
      - name: Build
        env:
          CUSTOM_VAR: value
        run: mvn clean install
```

---

### Workflow Syntax Issues

**Symptoms:**
- Workflow doesn't trigger
- Jobs don't run
- Steps are skipped

**Validation:**
```bash
# Install actionlint
brew install actionlint

# Validate workflows
actionlint .github/workflows/*.yml

# Check YAML syntax
yamllint .github/workflows/ci-cd.yml
```

**Common Mistakes:**

#### 1. Incorrect Indentation
```yaml
# ❌ Wrong
jobs:
build:
  runs-on: ubuntu-latest

# ✅ Correct
jobs:
  build:
    runs-on: ubuntu-latest
```

#### 2. Missing Quotes
```yaml
# ❌ Wrong
if: github.ref == refs/heads/main

# ✅ Correct
if: github.ref == 'refs/heads/main'
```

#### 3. Incorrect Conditionals
```yaml
# ❌ Wrong
if: ${{ success() }}

# ✅ Correct
if: success()
```

---

## Integration Issues

### Codecov Upload Failures

**Symptoms:**
```
[ERROR] Failed to upload coverage report
[ERROR] Codecov token invalid
```

**Solutions:**

#### 1. Verify Token
```yaml
- name: Upload Coverage
  uses: codecov/codecov-action@v3
  with:
    token: ${{ secrets.CODECOV_TOKEN }}
    files: ./target/site/jacoco/jacoco.xml
    fail_ci_if_error: false  # Don't fail build
```

#### 2. Check File Paths
```bash
# Verify coverage file exists
ls -la target/site/jacoco/jacoco.xml

# Check file content
head target/site/jacoco/jacoco.xml
```

---

### Jira Integration Issues

**Symptoms:**
- Comments not appearing in Jira
- Transitions not working
- Authentication failures

**Diagnosis:**
```bash
# Test Jira API
curl -u "$JIRA_USER_EMAIL:$JIRA_API_TOKEN" \
  "$JIRA_BASE_URL/rest/api/3/myself"

# Test issue access
curl -u "$JIRA_USER_EMAIL:$JIRA_API_TOKEN" \
  "$JIRA_BASE_URL/rest/api/3/issue/PROJ-123"
```

**Solutions:**

#### 1. Verify Credentials
```yaml
env:
  JIRA_BASE_URL: ${{ secrets.JIRA_BASE_URL }}
  JIRA_USER_EMAIL: ${{ secrets.JIRA_USER_EMAIL }}
  JIRA_API_TOKEN: ${{ secrets.JIRA_API_TOKEN }}
```

#### 2. Check Issue Key Format
```bash
# Valid formats
PROJ-123
MYPROJECT-456

# Invalid formats
proj-123  # lowercase
PROJ123   # missing hyphen
```

#### 3. Verify Permissions
- User must have permission to comment
- User must have permission to transition
- Issue must be in correct status for transition

---

## Monitoring & Alerts

### Setting Up Notifications

#### Slack Notifications
```yaml
- name: Notify Slack on Failure
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "Pipeline failed: ${{ github.workflow }}",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "*Pipeline Failed*\nWorkflow: ${{ github.workflow }}\nBranch: ${{ github.ref_name }}\n<${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}|View Run>"
            }
          }
        ]
      }
```

#### Email Notifications
```yaml
- name: Send Email on Failure
  if: failure()
  uses: dawidd6/action-send-mail@v3
  with:
    server_address: smtp.gmail.com
    server_port: 465
    username: ${{ secrets.EMAIL_USERNAME }}
    password: ${{ secrets.EMAIL_PASSWORD }}
    subject: Pipeline Failed - ${{ github.workflow }}
    body: |
      Pipeline failed for ${{ github.repository }}
      Branch: ${{ github.ref_name }}
      Commit: ${{ github.sha }}
      View: ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}
    to: team@example.com
```

---

### Health Checks

#### Pipeline Health Dashboard
```bash
# Check recent pipeline runs
gh run list --limit 10

# Check failure rate
gh run list --limit 100 --json conclusion | \
  jq '[.[] | select(.conclusion=="failure")] | length'

# Average build time
gh run list --limit 20 --json duration | \
  jq '[.[].duration] | add / length'
```

#### Automated Health Checks
```yaml
# .github/workflows/health-check.yml
name: Pipeline Health Check
on:
  schedule:
    - cron: '0 9 * * 1'  # Weekly on Monday

jobs:
  health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check Pipeline Success Rate
        run: |
          SUCCESS_RATE=$(gh run list --limit 100 --json conclusion | \
            jq '[.[] | select(.conclusion=="success")] | length')
          
          if [ $SUCCESS_RATE -lt 90 ]; then
            echo "⚠️ Pipeline success rate below 90%: $SUCCESS_RATE%"
            exit 1
          fi
```

---

## Quick Reference

### Diagnostic Commands

```bash
# Maven diagnostics
mvn -version
mvn help:effective-pom
mvn dependency:tree
mvn validate

# GitHub Actions diagnostics
gh run list
gh run view <run-id>
gh workflow list

# Container diagnostics
podman version
podman images
podman ps -a

# Network diagnostics
curl -I https://repo.maven.apache.org/maven2/
ping github.com
```

### Log Locations

| Component | Log Location |
|-----------|-------------|
| Maven | `target/surefire-reports/` |
| JaCoCo | `target/site/jacoco/` |
| SpotBugs | `target/spotbugsXml.xml` |
| OWASP | `target/dependency-check-report.html` |
| GitHub Actions | Actions tab → Workflow run |
| Container | `podman logs <container>` |

### Common Exit Codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | General error |
| 2 | Misuse of shell command |
| 126 | Command cannot execute |
| 127 | Command not found |
| 130 | Terminated by Ctrl+C |
| 137 | Killed (OOM) |
| 143 | Terminated by SIGTERM |

---

## Escalation Path

1. **Check this guide** - Most issues are documented here
2. **Test locally** - Use `test-pipeline-locally-enhanced.sh`
3. **Review logs** - Check GitHub Actions logs
4. **Search issues** - Check repository issues
5. **Ask team** - Post in team chat
6. **Create issue** - Document the problem
7. **Contact DevOps** - For infrastructure issues

---

## Related Documentation

- [Pipeline Architecture](PIPELINE-ARCHITECTURE.md)
- [Debug Guide](PIPELINE-DEBUG-GUIDE.md)
- [CI/CD Setup](../CI-CD-SETUP.md)
- [Release Guide](RELEASE-GUIDE.md)

---

**Last Updated**: 2026-06-11  
**Maintained By**: DevOps Team  
**Feedback**: Open an issue or PR to improve this guide
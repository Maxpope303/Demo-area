# 📊 Pipeline Metrics & Analytics

Comprehensive guide to tracking, analyzing, and improving CI/CD pipeline metrics.

## Table of Contents

1. [Key Metrics](#key-metrics)
2. [Tracking Methods](#tracking-methods)
3. [Analysis & Reporting](#analysis--reporting)
4. [Performance Benchmarks](#performance-benchmarks)
5. [Improvement Strategies](#improvement-strategies)
6. [Dashboards & Visualization](#dashboards--visualization)

---

## Key Metrics

### DORA Metrics (DevOps Research and Assessment)

#### 1. Deployment Frequency
**Definition**: How often code is deployed to production

**Target**: Multiple times per day (Elite), Once per day (High)

**Tracking**:
```bash
# Count deployments in last 30 days
gh run list --workflow=release.yml --limit 1000 --json createdAt | \
  jq '[.[] | select(.createdAt > (now - 2592000))] | length'
```

**Current Status**: Track in your environment

---

#### 2. Lead Time for Changes
**Definition**: Time from code commit to production deployment

**Target**: Less than 1 day (Elite), 1-7 days (High)

**Tracking**:
```bash
# Calculate average time from commit to deployment
gh run list --workflow=ci-cd.yml --limit 100 --json createdAt,updatedAt | \
  jq '[.[] | (.updatedAt - .createdAt)] | add / length / 60'
```

**Components**:
- Code review time
- Build time
- Test time
- Deployment time

---

#### 3. Change Failure Rate
**Definition**: Percentage of deployments causing failures

**Target**: 0-15% (Elite), 16-30% (High)

**Tracking**:
```bash
# Calculate failure rate
TOTAL=$(gh run list --limit 100 --json conclusion | jq 'length')
FAILED=$(gh run list --limit 100 --json conclusion | \
  jq '[.[] | select(.conclusion=="failure")] | length')
echo "scale=2; $FAILED * 100 / $TOTAL" | bc
```

---

#### 4. Mean Time to Recovery (MTTR)
**Definition**: Average time to recover from a failure

**Target**: Less than 1 hour (Elite), Less than 1 day (High)

**Tracking**: Monitor time between failure detection and fix deployment

---

### Pipeline-Specific Metrics

#### Build Metrics

| Metric | Description | Target | Current |
|--------|-------------|--------|---------|
| Build Duration | Total pipeline execution time | < 10 min | ~8 min |
| Build Success Rate | % of successful builds | > 95% | Track |
| Cache Hit Rate | % of cache hits vs misses | > 80% | Track |
| Artifact Size | Size of build artifacts | < 50 MB | Track |

#### Test Metrics

| Metric | Description | Target | Current |
|--------|-------------|--------|---------|
| Test Duration | Time to run all tests | < 3 min | Track |
| Test Count | Total number of tests | Growing | Track |
| Test Success Rate | % of passing tests | 100% | Track |
| Code Coverage | % of code covered by tests | > 30% | 35% |
| Flaky Test Rate | % of intermittent failures | < 1% | Track |

#### Quality Metrics

| Metric | Description | Target | Current |
|--------|-------------|--------|---------|
| Security Vulnerabilities | Critical/High CVEs | 0 | Track |
| Code Smells | SpotBugs issues | < 10 | Track |
| Technical Debt | Estimated remediation time | Decreasing | Track |
| Dependency Age | Days since last update | < 90 | Track |

---

## Tracking Methods

### 1. GitHub Actions Insights

**Access**: Repository → Insights → Actions

**Available Data**:
- Workflow run history
- Job execution times
- Success/failure rates
- Billing usage

**Export Data**:
```bash
# Export last 100 runs
gh run list --limit 100 --json \
  databaseId,name,createdAt,updatedAt,conclusion,status \
  > pipeline-runs.json

# Analyze with jq
cat pipeline-runs.json | jq '[.[] | {
  name: .name,
  duration: (.updatedAt - .createdAt) / 60,
  conclusion: .conclusion
}]'
```

---

### 2. Custom Metrics Collection

#### Add Timing to Workflow

```yaml
# .github/workflows/ci-cd.yml
jobs:
  build-and-test:
    steps:
      - name: Record Start Time
        id: start
        run: echo "time=$(date +%s)" >> $GITHUB_OUTPUT
      
      - name: Build
        run: mvn clean install
      
      - name: Record End Time and Calculate Duration
        run: |
          START_TIME=${{ steps.start.outputs.time }}
          END_TIME=$(date +%s)
          DURATION=$((END_TIME - START_TIME))
          echo "Build duration: ${DURATION}s"
          echo "build_duration=${DURATION}" >> $GITHUB_STEP_SUMMARY
```

#### Track Artifact Sizes

```yaml
- name: Track Artifact Size
  run: |
    SIZE=$(du -h target/simple-pharmacy.war | cut -f1)
    echo "WAR size: $SIZE" >> $GITHUB_STEP_SUMMARY
    
    # Store in file for trending
    echo "$(date +%Y-%m-%d),$SIZE" >> metrics/artifact-sizes.csv
```

---

### 3. Test Metrics Collection

#### JaCoCo Coverage Tracking

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>report</id>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.30</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Extract Coverage Percentage

```bash
# Extract coverage from JaCoCo XML
COVERAGE=$(xmllint --xpath \
  "string(//report/counter[@type='LINE']/@covered div //report/counter[@type='LINE']/@missed + //report/counter[@type='LINE']/@covered)" \
  target/site/jacoco/jacoco.xml)

echo "Coverage: $(echo "$COVERAGE * 100" | bc)%"
```

---

### 4. Security Metrics

#### Track Vulnerabilities Over Time

```bash
# Extract vulnerability count from OWASP report
CRITICAL=$(grep -c "severity=\"CRITICAL\"" target/dependency-check-report.xml || echo 0)
HIGH=$(grep -c "severity=\"HIGH\"" target/dependency-check-report.xml || echo 0)

echo "$(date +%Y-%m-%d),$CRITICAL,$HIGH" >> metrics/vulnerabilities.csv
```

---

## Analysis & Reporting

### Weekly Metrics Report

Create a scheduled workflow to generate reports:

```yaml
# .github/workflows/metrics-report.yml
name: Weekly Metrics Report

on:
  schedule:
    - cron: '0 9 * * 1'  # Every Monday at 9 AM
  workflow_dispatch:

jobs:
  generate-report:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      
      - name: Generate Metrics Report
        run: |
          cat > metrics-report.md << 'EOF'
          # Pipeline Metrics Report
          **Week of**: $(date +%Y-%m-%d)
          
          ## Build Performance
          - Average build time: $(gh run list --limit 50 --json duration | jq '[.[].duration] | add / length / 60') minutes
          - Success rate: $(gh run list --limit 100 --json conclusion | jq '[.[] | select(.conclusion=="success")] | length')%
          
          ## Test Coverage
          - Current coverage: Check latest run
          - Tests passing: Check latest run
          
          ## Security
          - Critical vulnerabilities: Check OWASP report
          - High vulnerabilities: Check OWASP report
          
          ## Recommendations
          - Review failed builds
          - Update dependencies
          - Improve test coverage
          EOF
          
          cat metrics-report.md >> $GITHUB_STEP_SUMMARY
      
      - name: Create Issue with Report
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const report = fs.readFileSync('metrics-report.md', 'utf8');
            
            github.rest.issues.create({
              owner: context.repo.owner,
              repo: context.repo.repo,
              title: `Pipeline Metrics Report - ${new Date().toISOString().split('T')[0]}`,
              body: report,
              labels: ['metrics', 'automated']
            });
```

---

### Trend Analysis

#### Build Time Trends

```bash
# Analyze build time trends over last 100 runs
gh run list --limit 100 --json createdAt,duration | \
  jq -r '.[] | "\(.createdAt | split("T")[0]),\(.duration / 60)"' | \
  sort > build-times.csv

# Calculate moving average
awk -F',' '{sum+=$2; count++; if(count>=10) {print $1, sum/10; sum-=prev[count%10]} prev[count%10]=$2}' \
  build-times.csv
```

#### Coverage Trends

```bash
# Track coverage over time (requires storing coverage data)
# Create a script to extract and store coverage after each run

#!/bin/bash
# scripts/track-coverage.sh
COVERAGE=$(xmllint --xpath "string(//report/counter[@type='LINE']/@covered div (//report/counter[@type='LINE']/@missed + //report/counter[@type='LINE']/@covered))" target/site/jacoco/jacoco.xml)
PERCENTAGE=$(echo "$COVERAGE * 100" | bc)
echo "$(date +%Y-%m-%d),$PERCENTAGE" >> metrics/coverage-history.csv
```

---

## Performance Benchmarks

### Baseline Metrics (Current State)

```
Pipeline Stage          | Duration | Target  | Status
------------------------|----------|---------|--------
Setup                   | 10s      | < 15s   | ✅
Build & Test            | 3-5 min  | < 5 min | ✅
Code Quality            | 2-3 min  | < 3 min | ✅
Container Build         | 4-6 min  | < 5 min | ⚠️
Coverage Gate           | 2-3 min  | < 3 min | ✅
Total Pipeline          | 8-10 min | < 10min | ✅
```

### Optimization Targets

| Metric | Current | Target | Improvement |
|--------|---------|--------|-------------|
| Build Time | 8 min | 6 min | 25% faster |
| Cache Hit Rate | 70% | 90% | 20% increase |
| Test Duration | 3 min | 2 min | 33% faster |
| Container Build | 5 min | 3 min | 40% faster |

---

## Improvement Strategies

### 1. Reduce Build Time

**Current Bottlenecks**:
- Dependency resolution: 1-2 min
- Compilation: 1-2 min
- Testing: 2-3 min
- Container build: 4-6 min

**Optimization Actions**:
```yaml
# Parallel job execution
jobs:
  build:
    # ...
  
  quality:
    needs: setup  # Not build
    # Runs parallel to build
  
  container:
    needs: setup  # Not build
    # Runs parallel to build
```

**Expected Impact**: 30% reduction in total time

---

### 2. Improve Cache Efficiency

**Current State**: ~70% hit rate

**Actions**:
```yaml
- name: Enhanced Maven Cache
  uses: actions/cache@v3
  with:
    path: |
      ~/.m2/repository
      ~/.m2/wrapper
      target/
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml', '**/src/**') }}
    restore-keys: |
      ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
      ${{ runner.os }}-maven-
```

**Expected Impact**: 90% hit rate, 2 min saved per build

---

### 3. Optimize Test Execution

**Current State**: Sequential execution

**Actions**:
```xml
<!-- Enable parallel test execution -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>4</threadCount>
        <forkCount>2</forkCount>
    </configuration>
</plugin>
```

**Expected Impact**: 40% faster test execution

---

## Dashboards & Visualization

### GitHub Actions Dashboard

**Built-in Metrics**:
- Workflow runs over time
- Success/failure rates
- Job durations
- Billing usage

**Access**: Repository → Insights → Actions

---

### Custom Dashboard with GitHub API

```python
#!/usr/bin/env python3
# scripts/generate-dashboard.py

import requests
import json
from datetime import datetime, timedelta

def get_workflow_runs(repo, token, days=30):
    """Fetch workflow runs from GitHub API"""
    url = f"https://api.github.com/repos/{repo}/actions/runs"
    headers = {"Authorization": f"token {token}"}
    params = {
        "per_page": 100,
        "created": f">{(datetime.now() - timedelta(days=days)).isoformat()}"
    }
    
    response = requests.get(url, headers=headers, params=params)
    return response.json()

def calculate_metrics(runs):
    """Calculate key metrics from runs"""
    total = len(runs['workflow_runs'])
    successful = sum(1 for r in runs['workflow_runs'] if r['conclusion'] == 'success')
    failed = sum(1 for r in runs['workflow_runs'] if r['conclusion'] == 'failure')
    
    durations = [
        (datetime.fromisoformat(r['updated_at'].replace('Z', '+00:00')) - 
         datetime.fromisoformat(r['created_at'].replace('Z', '+00:00'))).total_seconds() / 60
        for r in runs['workflow_runs']
    ]
    
    return {
        'total_runs': total,
        'success_rate': (successful / total * 100) if total > 0 else 0,
        'failure_rate': (failed / total * 100) if total > 0 else 0,
        'avg_duration': sum(durations) / len(durations) if durations else 0
    }

# Usage
# python scripts/generate-dashboard.py
```

---

### Grafana Dashboard (Advanced)

For teams using Grafana:

```yaml
# Export metrics to Prometheus format
- name: Export Metrics
  run: |
    cat > metrics.prom << EOF
    # HELP pipeline_duration_seconds Pipeline execution duration
    # TYPE pipeline_duration_seconds gauge
    pipeline_duration_seconds{job="ci-cd"} ${{ steps.duration.outputs.seconds }}
    
    # HELP pipeline_success Pipeline success indicator
    # TYPE pipeline_success gauge
    pipeline_success{job="ci-cd"} 1
    
    # HELP test_coverage_percent Test coverage percentage
    # TYPE test_coverage_percent gauge
    test_coverage_percent{job="ci-cd"} ${{ steps.coverage.outputs.percent }}
    EOF
```

---

## Metrics Checklist

### Daily
- [ ] Check pipeline success rate
- [ ] Review failed builds
- [ ] Monitor build duration

### Weekly
- [ ] Generate metrics report
- [ ] Review coverage trends
- [ ] Check security vulnerabilities
- [ ] Analyze slow tests

### Monthly
- [ ] Calculate DORA metrics
- [ ] Review optimization opportunities
- [ ] Update performance benchmarks
- [ ] Plan improvements

---

## Tools & Resources

### Recommended Tools

1. **GitHub CLI** - Command-line metrics extraction
2. **jq** - JSON processing for analysis
3. **act** - Local workflow testing
4. **Grafana** - Advanced visualization (optional)
5. **Codecov** - Coverage tracking and trends

### Useful Scripts

```bash
# scripts/metrics-summary.sh
#!/bin/bash
echo "=== Pipeline Metrics Summary ==="
echo "Last 30 days:"
echo "Total runs: $(gh run list --limit 1000 --json createdAt | jq '[.[] | select(.createdAt > (now - 2592000))] | length')"
echo "Success rate: $(gh run list --limit 100 --json conclusion | jq '[.[] | select(.conclusion=="success")] | length')%"
echo "Avg duration: $(gh run list --limit 50 --json duration | jq '[.[].duration] | add / length / 60') min"
```

---

## Related Documentation

- [Pipeline Architecture](PIPELINE-ARCHITECTURE.md)
- [Troubleshooting Guide](PIPELINE-TROUBLESHOOTING.md)
- [Debug Guide](PIPELINE-DEBUG-GUIDE.md)

---

**Last Updated**: 2026-06-11  
**Maintained By**: DevOps Team  
**Next Review**: 2026-07-11
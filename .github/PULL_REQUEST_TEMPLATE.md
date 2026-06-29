# Pull Request

## Description

<!-- Provide a brief description of the changes in this PR -->

## Type of Change

<!-- Mark the relevant option with an 'x' -->

- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📝 Documentation update
- [ ] 🔧 Configuration change
- [ ] ♻️ Code refactoring (no functional changes)
- [ ] ⚡ Performance improvement
- [ ] 🧪 Test update

## Related Issues

<!-- Link to related issues using #issue_number or Jira ticket keys (e.g., PROJ-123) -->

Fixes #
Relates to #

## Changes Made

<!-- List the main changes made in this PR -->

- 
- 
- 

## Testing Performed

<!-- Describe the testing you've done -->

- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed
- [ ] Tested on multiple Java versions (if applicable)

### Test Coverage

- Current coverage: __%
- Coverage change: +/- __%

## Pipeline Checklist

<!-- Ensure all pipeline checks pass before requesting review -->

### Build & Test
- [ ] ✅ Build completes successfully
- [ ] ✅ All unit tests pass
- [ ] ✅ Code coverage meets minimum threshold (30%)
- [ ] ✅ No test failures or flaky tests

### Code Quality
- [ ] ✅ SpotBugs analysis passes (no critical issues)
- [ ] ✅ OWASP dependency check passes (no high/critical vulnerabilities)
- [ ] ✅ Code follows project style guidelines
- [ ] ✅ No new compiler warnings introduced

### Container Build
- [ ] ✅ Container image builds successfully
- [ ] ✅ Container starts without errors
- [ ] ✅ Application accessible in container

### Documentation
- [ ] ✅ Code changes are documented
- [ ] ✅ README updated (if needed)
- [ ] ✅ API documentation updated (if applicable)
- [ ] ✅ Changelog updated (for releases)

### Security
- [ ] ✅ No hardcoded secrets or credentials
- [ ] ✅ No sensitive data in logs
- [ ] ✅ Dependencies are up to date
- [ ] ✅ Security best practices followed

## Deployment Considerations

<!-- Any special considerations for deployment? -->

- [ ] Database migrations required
- [ ] Configuration changes required
- [ ] Environment variables need updating
- [ ] Backward compatibility maintained
- [ ] Rollback plan documented

## Screenshots/Logs

<!-- If applicable, add screenshots or relevant log outputs -->

<details>
<summary>Click to expand</summary>

```
Paste logs or add screenshots here
```

</details>

## Reviewer Notes

<!-- Any specific areas you'd like reviewers to focus on? -->

- 
- 

## Pre-Merge Checklist

<!-- Final checks before merging -->

- [ ] All pipeline checks are green ✅
- [ ] Code has been reviewed and approved
- [ ] Conflicts resolved (if any)
- [ ] Commit messages follow convention
- [ ] Branch is up to date with target branch
- [ ] Jira ticket updated (if applicable)

## Post-Merge Actions

<!-- Actions to take after merging -->

- [ ] Monitor deployment
- [ ] Update Jira ticket status
- [ ] Notify stakeholders
- [ ] Delete feature branch

---

## Pipeline Status

<!-- This section will be auto-populated by GitHub Actions -->

The following checks will run automatically:

1. **Setup** - Determine Java version and runtime
2. **Build & Test** - Compile, test, and generate coverage
3. **Code Quality** - SpotBugs and OWASP security scans
4. **Container Build** - Build and validate container image
5. **Coverage Gate** - Ensure minimum 30% coverage
6. **Pipeline Summary** - Generate comprehensive report

### Quick Links

- [Pipeline Architecture](PIPELINE-ARCHITECTURE.md)
- [CI/CD Setup Guide](../CI-CD-SETUP.md)
- [Troubleshooting Guide](PIPELINE-TROUBLESHOOTING.md)
- [Contributing Guidelines](../CONTRIBUTING.md)

---

**Reminder**: Ensure your commit messages include the Jira ticket key (e.g., `PROJ-123: Add new feature`) for automatic Jira integration.
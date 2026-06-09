# Java Modernization Demo Guide

This guide explains how to use the demo reset functionality to repeatedly demonstrate the Java modernization workflow.

## Overview

The demo reset system uses Git branches to maintain two separate states:
- **Java 8 Baseline** (`demo-java8-baseline`) - Original application state
- **Modernized** (`demo-modernized`) - Upgraded application state

This allows you to:
1. Show the original Java 8 application
2. Perform the modernization
3. Show the modernized application
4. Reset back to Java 8 for the next demo

## Prerequisites

- Git repository initialized
- Docker or Podman installed
- Maven installed
- Java 8 and Java 17/21 installed

## Quick Start

### 1. Initial Setup (One-Time)

Initialize the demo branches:

```bash
cd simple-pharmacy-twas-j8-struts-V2
./demo-reset.sh init
```

This creates two branches:
- `demo-java8-baseline` - Preserves the current Java 8 state
- `demo-modernized` - For the upgraded version

### 2. Demo Workflow

#### Step 1: Start with Java 8 Baseline

```bash
# Switch to Java 8 baseline
./demo-reset.sh baseline

# Verify you're on the baseline
./demo-reset.sh status

# Build and run the Java 8 application
./build-and-run.sh
```

Access the application at: http://localhost:9080/pharmacy/

#### Step 2: Perform Modernization

Switch to the modernized branch:

```bash
./demo-reset.sh modernized
```

Now perform your modernization steps (or if already done, just show the changes):

**Option A: Using IBM Application Modernization Accelerator (AMA)**
```bash
# Run AMA analysis and apply recommendations
# (Follow AMA workflow)
```

**Option B: Manual Modernization**
- Update `pom.xml` to Java 17/21
- Change from WebSphere Traditional to Liberty
- Update dependencies
- Modify code for newer Java APIs

After making changes, save them:

```bash
./demo-reset.sh save
```

#### Step 3: Build and Run Modernized Version

```bash
# Build the modernized application
./build-and-run.sh
```

Access the modernized application at the same URL.

#### Step 4: Reset for Next Demo

When you're done with the demo and want to reset:

```bash
# Clean up containers and switch back to baseline
./demo-reset.sh baseline
```

You're now ready to run the demo again!

## Demo Reset Commands

### `./demo-reset.sh init`
Initialize demo branches (run once during setup)
- Creates `demo-java8-baseline` branch
- Creates `demo-modernized` branch
- Commits current state as baseline

### `./demo-reset.sh status`
Show current demo state
- Displays current branch
- Shows Java version and runtime
- Lists uncommitted changes

### `./demo-reset.sh baseline`
Switch to Java 8 baseline state
- Cleans Docker/Podman containers
- Switches to `demo-java8-baseline` branch
- Cleans working directory
- Ready for demo start

### `./demo-reset.sh modernized`
Switch to modernized state
- Cleans Docker/Podman containers
- Switches to `demo-modernized` branch
- Shows upgraded code

### `./demo-reset.sh save`
Save current changes to active branch
- Commits all changes
- Prompts for commit message
- Preserves your work

### `./demo-reset.sh clean`
Clean up Docker/Podman resources
- Stops and removes containers
- Removes images
- Cleans Maven target directory

### `./demo-reset.sh help`
Display help information

## Demo Script Example

Here's a complete demo script you can follow:

```bash
# ============================================
# Java Modernization Demo Script
# ============================================

# 1. Start with Java 8 baseline
echo "=== Starting with Java 8 Application ==="
./demo-reset.sh baseline
./demo-reset.sh status

# 2. Show the current configuration
echo "=== Current Configuration ==="
cat pom.xml | grep -A 2 "maven.compiler"
cat Dockerfile | grep FROM

# 3. Build and run Java 8 version
echo "=== Building Java 8 Application ==="
./build-and-run.sh

# 4. Access application (manual step)
echo "=== Access: http://localhost:9080/pharmacy/ ==="
read -p "Press Enter when ready to modernize..."

# 5. Stop the application
docker-compose down

# 6. Switch to modernized version
echo "=== Switching to Modernized Version ==="
./demo-reset.sh modernized

# 7. Show the modernized configuration
echo "=== Modernized Configuration ==="
cat pom.xml | grep -A 2 "maven.compiler"
cat Dockerfile | grep FROM

# 8. Build and run modernized version
echo "=== Building Modernized Application ==="
./build-and-run.sh

# 9. Access modernized application (manual step)
echo "=== Access: http://localhost:9080/pharmacy/ ==="
read -p "Press Enter when demo is complete..."

# 10. Clean up and reset
echo "=== Cleaning Up ==="
docker-compose down
./demo-reset.sh baseline

echo "=== Demo Complete - Ready for Next Run ==="
```

## Troubleshooting

### Problem: "Not a git repository"
**Solution:** Initialize git first:
```bash
git init
git add .
git commit -m "Initial commit"
./demo-reset.sh init
```

### Problem: "You have uncommitted changes"
**Solution:** Save or discard changes:
```bash
# Save changes
./demo-reset.sh save

# Or discard changes (careful!)
git reset --hard HEAD
git clean -fd
```

### Problem: Containers still running
**Solution:** Clean up containers:
```bash
./demo-reset.sh clean
```

### Problem: Port already in use
**Solution:** Stop existing containers:
```bash
docker-compose down
# or
docker stop $(docker ps -q)
```

## Best Practices

1. **Always run `init` first** - Set up branches before starting demos
2. **Use `status` frequently** - Know which state you're in
3. **Save your work** - Use `save` after making changes to modernized branch
4. **Clean between switches** - The script does this automatically
5. **Test both states** - Verify both Java 8 and modernized versions work

## File Changes During Modernization

The following files typically change during modernization:

### `pom.xml`
```xml
<!-- Java 8 Baseline -->
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>

<!-- Modernized -->
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

### `Dockerfile`
```dockerfile
# Java 8 Baseline
FROM ibmcom/websphere-traditional:latest

# Modernized
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi
```

### Source Code
- Update deprecated APIs
- Use modern Java features (streams, lambdas, etc.)
- Update dependencies to compatible versions

## Advanced Usage

### Creating Multiple Demo Scenarios

You can create additional branches for different scenarios:

```bash
# Create a branch for partial modernization
git checkout demo-java8-baseline
git checkout -b demo-partial-modernization
# Make partial changes
git commit -am "Partial modernization"

# Switch between scenarios
git checkout demo-java8-baseline
git checkout demo-partial-modernization
git checkout demo-modernized
```

### Comparing States

```bash
# Compare baseline vs modernized
git diff demo-java8-baseline..demo-modernized

# Show specific file differences
git diff demo-java8-baseline..demo-modernized -- pom.xml
```

### Backing Up Demo State

```bash
# Create a backup tag
git tag -a demo-backup-$(date +%Y%m%d) -m "Demo backup"

# List backups
git tag -l "demo-backup-*"

# Restore from backup
git checkout demo-backup-20260609
```

## Integration with IBM AMA

When using IBM Application Modernization Accelerator:

1. Start with baseline: `./demo-reset.sh baseline`
2. Run AMA analysis on the Java 8 code
3. Switch to modernized: `./demo-reset.sh modernized`
4. Apply AMA recommendations
5. Save changes: `./demo-reset.sh save`
6. Test the modernized application
7. Reset for next demo: `./demo-reset.sh baseline`

## Support

For issues or questions:
- Check the troubleshooting section above
- Run `./demo-reset.sh help` for command reference
- Review Git status: `git status`
- Check container status: `docker ps -a` or `podman ps -a`

---

**Happy Demoing!** 🚀
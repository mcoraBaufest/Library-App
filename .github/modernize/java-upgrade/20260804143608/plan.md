# Upgrade Plan: library-app (20260804143608)

- **Generated**: 2026-08-04 14:36:08
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

> **Note**: This project is not a git repository. Changes will not be version-controlled during this upgrade.

---

## Available Tools

**JDKs**
- JDK 21.0.11: `C:\Program Files\Java\jdk-21.0.11\bin` (current project JDK, used for baseline in Step 2)
- JDK 25.0.4: `C:\Users\mcora\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.7-hotspot\bin` (target, already installed ✓)

**Build Tools**
- Maven Wrapper: 3.9.16 (via `.mvn/wrapper/maven-wrapper.properties`) ✓ — compatible with Java 25

---

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

---

## Options

- Working branch: N/A (not a git repository)
- Run tests before and after the upgrade: true

---

## Upgrade Goals

- **Java**: 21 → 25 (latest LTS)

---

## Technology Stack

| Technology/Dependency  | Current  | Min Compatible | Why Incompatible                          |
|------------------------|----------|----------------|-------------------------------------------|
| Java                   | 21       | 25             | User requested upgrade to latest LTS      |
| Spring Boot            | 4.1.0    | 4.1.0          | Compatible with Java 25 (no change needed)|
| Maven Wrapper          | 3.9.16   | 3.9.0          | Already compatible ✓                      |
| Lombok                 | managed by SB 4.1.0 | —    | No change needed                          |

---

## Derived Upgrades

- **Java 25** → Maven 3.9+ recommended: Maven Wrapper 3.9.16 already satisfies this ✓
- **Java 25** → No Kotlin in this project (no Kotlin version upgrade needed)
- **Java 25** → No Gradle (Maven project; no Gradle version constraint applies)

---

## Impact Analysis

### Dependency Changes

| File    | Dependency    | Current | Action  | Target | Reason           |
|---------|---------------|---------|---------|--------|------------------|
| pom.xml | java.version  | 21      | upgrade | 25     | User requested   |

### Source Code Changes

No source code changes required. The project contains only a `@SpringBootApplication` main class and a context-loads test with no JDK-internal API usage, reflection into `java.base` internals, removed APIs, or `javax.*` namespace dependencies.

### Configuration Changes

No configuration changes required.

### CI/CD Changes

No Dockerfile or CI/CD pipeline files detected in the project.

### Risks & Warnings

- **Spring Boot 4.1.0 + Java 25 compatibility**: Spring Boot 4.x officially targets Java 17+; Java 25 LTS was released September 2025. Spring Boot 4.1.0 should support Java 25 given its release timeline. **Mitigation**: Baseline compilation and test run with JDK 21 confirms pass rate; final validation with JDK 25 confirms compatibility.
- **No version control**: This project is not a git repository. Changes cannot be rolled back via git. **Mitigation**: Note pre-change state in progress.md; changes are minimal (single property in pom.xml).

---

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Verify JDK 25 is available. No installation needed — JDK 25.0.4 is already present on the system.
  - **Changes to Make**: None (environment already ready). Verify with `#appmod-list-jdks`.
  - **Verification**: Confirm JDK 25.0.4 found at `C:\Users\mcora\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.7-hotspot\bin`

- Step 2: Setup Baseline
  - **Rationale**: Establish a baseline compilation and test pass rate using JDK 21 before any changes.
  - **Changes to Make**: None — read-only verification step.
  - **Verification**: `.\mvnw.cmd clean test -q`, JDK: `C:\Program Files\Java\jdk-21.0.11\bin`; document pass rate.

- Step 3: Upgrade Java Version from 21 to 25
  - **Rationale**: Update the `java.version` property in pom.xml to target Java 25.
  - **Changes to Make**: Dependency Changes — update `<java.version>21</java.version>` → `<java.version>25</java.version>` in `pom.xml`.
  - **Verification**: `.\mvnw.cmd clean test-compile -q`, JDK: `C:\Users\mcora\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.7-hotspot\bin`; Expected: Compilation SUCCESS.

- Step 4: CVE Validation & Fix
  - **Rationale**: Scan all direct dependencies for known CVEs after the upgrade and remediate any found.
  - **Changes to Make**: Upgrade any CVE-affected dependencies to patched versions.
  - **Verification**: Re-scan with `#appmod-validate-cves-for-java`; Expected: No CVEs or all CVEs addressed.

- Step 5: Final Validation
  - **Rationale**: Confirm all upgrade goals are met, resolve any TODOs, and achieve 100% test pass rate with JDK 25.
  - **Changes to Make**: Fix any test failures found; verify all Upgrade Success Criteria.
  - **Verification**: `.\mvnw.cmd clean test`, JDK: `C:\Users\mcora\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.4.7-hotspot\bin`; Expected: All tests pass (≥ baseline).

# Upgrade Plan: bank (20260927130623)

- **Generated**: 2026-09-27 13:06:23
- **HEAD Branch**: appmod/java-upgrade-20260927130623
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 21.0.11: /opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home/bin (installed, current compatibility toolchain)
- JDK 22.0.1: /Users/raniaburaia/Library/Java/JavaVirtualMachines/openjdk-22.0.1/Contents/Home/bin (installed, not the target runtime)
- JDK 25: /Users/raniaburaia/.jdk/jdk-25.0.2/jdk-25.0.2+10/Contents/Home/bin (installed for final validation)

**Build Tools**
- Maven Wrapper: 3.9.7 (project wrapper; distributionUrl in .mvn/wrapper/maven-wrapper.properties)
- System Maven: not detected; the project relies on the Maven wrapper for build/test execution

## Guidelines

- Upgrade the Java runtime to the latest LTS release: Java 25.
- Keep the project buildable and testable throughout the migration.
- Prefer minimal, compatibility-first dependency updates that preserve application behavior.
- Use the Maven wrapper to ensure a consistent build environment across the upgrade.

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260927130623
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java 25

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| ---------------------- | ------- | ---------------------- | --------------- |
| Java | 17 | 25 | User requested Java 25 as the target LTS runtime |
| Spring Boot | 3.3.1 | 3.5.0 | Align the supported Spring Boot release line with the Java 25 target and current compatibility expectations |
| Maven Wrapper | 3.9.7 | 3.9.7+ | Maven 3.9.x is the recommended baseline for modern JDK support |
| jakarta.servlet-api | 5.0.0 | 6.0.0 (managed by Spring Boot 3.x) | Explicit pin is stale for Spring Boot 3.x and should be removed from direct overrides |
| JAXB modules | 2.3.1 | 2.3.1+ | Java 25 removes JAXB from the JDK; keeping explicit Java EE/Jakarta JAXB artifacts is required for compatibility |

## Derived Upgrades

- Spring Boot 3.3.1 -> 3.5.0: align with the Java 25 runtime support window and keep the framework on a supported release line.
- Java property update: `java.version` 17 -> 25.
- Dependency hygiene: remove stale explicit servlet API version override so Spring Boot manages the compatible Jakarta Servlet version.
- Maven wrapper remains compatible with Java 25 because the project already uses Maven 3.9.7.

## Impact Analysis

### Subsection: Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
| ---- | ---------- | ------- | ------ | ------ | ------ |
| pom.xml | org.springframework.boot:spring-boot-starter-parent | 3.3.1 | upgrade | 3.5.0 | Java 25 support and supported Spring Boot release line |
| pom.xml | java.version | 17 | upgrade | 25 | User requested target runtime |
| pom.xml | jakarta.servlet-api | 5.0.0 | remove | (managed by Spring Boot) | Stale explicit override prevents Boot 3.x managed compatibility |
| pom.xml | spring-boot-starter-test | duplicate entry | remove | (single managed test dependency) | Duplicate dependency declaration adds noise and can cause resolution ambiguity |
| pom.xml | lombok | duplicate entry | remove | (single managed dependency) | Duplicate declaration should be cleaned while updating the project |

### Subsection: Source Code Changes

| File | Location | Current | Required Change | Reason |
| ---- | -------- | ------- | --------------- | ------ |
| src/main/java/myBankApplication/BankApplication.java | imports | uses `jakarta.annotation.PostConstruct` and `javax.security.auth.login.AccountNotFoundException` | Keep JDK-standard `javax.security` use but verify no invalid JDK-module usage under Java 25 | Guard against runtime compatibility issues introduced by stricter JDK module access |
| src/main/java/myBankApplication/**/*.java | business/service code | many `AccountNotFoundException` imports from `javax.security.auth.login` | Verify the exception type remains valid on Java 25 and no reflection-based access is used | Avoid runtime failures due to removed or restricted APIs |
| src/main/java/myBankApplication/config/SecurityConfig.java | security configuration | Spring Security DSL | Verify security config still compiles on Spring Boot 3.5 | Spring Boot 3.x uses updated Security APIs and stricter defaults |

### Subsection: Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
| ---- | --------------- | ------- | --------------- | ------ |
| pom.xml | `<java.version>` | 17 | 25 | User target runtime |
| .mvn/wrapper/maven-wrapper.properties | `distributionUrl` | Maven 3.9.7 | keep/verify compatible | Maven 3.9.x is acceptable for Java 25 |

### Subsection: CI/CD Changes

| File | Location | Current | Required Change |
| ---- | -------- | ------- | ---------------- |
| none detected | project currently uses Maven wrapper only; no explicit GitHub Actions / Jenkins / Docker JDK pins were found in the repo root | - | No CI change required unless a hidden workflow file is discovered during validation |

### Subsection: Risks & Warnings

- **Spring Boot / Java compatibility**: upgrading the runtime to Java 25 can surface JDK 17+ compatibility issues around reflection, security defaults, and removed JDK internals. Mitigation: run the full Maven test suite under the target JDK before closing the upgrade.
- **Dependency override drift**: the project contains stale explicit dependency pins (servlet API, duplicate test/lombok declarations). Mitigation: clean duplicates and let Spring Boot manage compatible versions during the upgrade step.
- **JDK 25 module access**: any code using internal JDK packages or reflection may fail at runtime even if compilation succeeds. Mitigation: search for `sun.*`, `jdk.internal.*`, and `setAccessible(true)` patterns and fix or document any residual risk.

## Upgrade Steps

- Step 1: Install Java 25 and validate toolchain
  - **Rationale**: The target runtime is Java 25; the project cannot compile/test against the final JDK until it is installed.
  - **Changes to Make**: Install the required JDK 25 and confirm the project can launch with the wrapper under that runtime.
  - **Verification**: `./mvnw -version` using JDK 25; expected result: Maven reports Java 25 as the active runtime.

- Step 2: Setup Baseline on the current JDK
  - **Rationale**: Capture the baseline state before the Java 25 upgrade so any later failure can be attributed correctly.
  - **Changes to Make**: Run current compilation and test baseline using the project’s effective Java 17 toolchain.
  - **Verification**: `./mvnw clean compile test-compile -q && ./mvnw clean test -q`; expected result: baseline passes or any fail is documented before the upgrade.

- Step 3: Upgrade runtime and compatible dependency line
  - **Rationale**: This is the core Java 25 migration step: update the Java target, align Spring Boot to the supported release line, and remove stale dependency overrides.
  - **Changes to Make**: Apply all Dependency Changes and the required configuration updates from the Impact Analysis.
  - **Verification**: `./mvnw clean test-compile -q` with JDK 25; expected result: production and test code compile successfully.

- Step 4: Fix build and test regressions under Java 25
  - **Rationale**: Java 25 and Spring Boot 3.5 may expose compatibility problems in the app configuration, annotations, or security settings.
  - **Changes to Make**: Resolve any compile/test issues surfaced by the final runtime and iterate until green.
  - **Verification**: `./mvnw clean test -q`; expected result: 100% of tests pass under Java 25.

- Step 5: Final Validation and summary
  - **Rationale**: Preserve evidence that the upgrade met the project’s success criteria and left the repo in a clean, verified state.
  - **Changes to Make**: Final sanity check, confirm no unresolved risks remain, and record the summary.
  - **Verification**: `./mvnw clean verify -q`; expected result: all checks pass and the project is ready on Java 25.

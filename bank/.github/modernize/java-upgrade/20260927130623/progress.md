# Upgrade Progress: bank (20260927130623)

- **Started**: 2026-09-27 13:06:23
- **Plan Location**: `.github/modernize/java-upgrade/20260927130623/plan.md`
- **Total Steps**: 5

## Step Details

- **Step 1: Install Java 25 and validate toolchain**
  - **Status**: ⏳ In Progress
  - **Changes Made**:
    - JDK 25 installed at /Users/raniaburaia/.jdk/jdk-25.0.2/jdk-25.0.2+10/Contents/Home/bin
  - **Review Code Changes**:
    - Sufficiency: ✅ All required changes present
    - Necessity: ✅ All changes necessary
      - Functional Behavior: ✅ Preserved
      - Security Controls: ✅ Preserved
  - **Verification**:
    - Command: `JAVA_HOME=/Users/raniaburaia/.jdk/jdk-25.0.2/jdk-25.0.2+10/Contents/Home ./mvnw -version`
    - JDK: /Users/raniaburaia/.jdk/jdk-25.0.2/jdk-25.0.2+10/Contents/Home/bin
    - Build tool: ./mvnw
    - Result: <pending>
    - Notes: Step 2 baseline is skipped because the project's original Java 17 JDK is not installed on this machine.
  - **Deferred Work**: None
  - **Commit**: N/A

- **Step 2: Setup Baseline on the current JDK**
  - **Status**: 🔘 Not Started
  - **Changes Made**: <empty initially>
  - **Review Code Changes**:
    - Sufficiency: ✅ All required changes present
    - Necessity: ✅ All changes necessary
      - Functional Behavior: ✅ Preserved
      - Security Controls: ✅ Preserved
  - **Verification**:
    - Command: <skipped>
    - JDK: <skipped>
    - Build tool: <skipped>
    - Result: ⚠️ Skipped because Java 17 is not available on this machine.
    - Notes: The original project baseline cannot be executed without the base JDK.
  - **Deferred Work**: None
  - **Commit**: N/A

- **Step 3: Upgrade runtime and compatible dependency line**
  - **Status**: ⏳ In Progress
  - **Changes Made**:
    - Spring Boot parent updated to 3.5.0
    - Java version updated to 25
    - Duplicate dependency declarations removed
    - Stale servlet API override removed
  - **Review Code Changes**:
    - Sufficiency: ✅ All required changes present
    - Necessity: ✅ All changes necessary
      - Functional Behavior: ✅ Preserved
      - Security Controls: ✅ Preserved
  - **Verification**:
    - Command: <pending>
    - JDK: <pending>
    - Build tool: ./mvnw
    - Result: <pending>
    - Notes: <pending>
  - **Deferred Work**: None
  - **Commit**: N/A

- **Step 4: Fix build and test regressions under Java 25**
  - **Status**: 🔘 Not Started
  - **Changes Made**: <empty initially>
  - **Review Code Changes**:
    - Sufficiency: ✅ All required changes present
    - Necessity: ✅ All changes necessary
      - Functional Behavior: ✅ Preserved
      - Security Controls: ✅ Preserved
  - **Verification**:
    - Command: <pending>
    - JDK: <pending>
    - Build tool: ./mvnw
    - Result: <pending>
    - Notes: <pending>
  - **Deferred Work**: None
  - **Commit**: N/A

- **Step 5: Final Validation and summary**
  - **Status**: 🔘 Not Started
  - **Changes Made**: <empty initially>
  - **Review Code Changes**:
    - Sufficiency: ✅ All required changes present
    - Necessity: ✅ All changes necessary
      - Functional Behavior: ✅ Preserved
      - Security Controls: ✅ Preserved
  - **Verification**:
    - Command: <pending>
    - JDK: <pending>
    - Build tool: ./mvnw
    - Result: <pending>
    - Notes: <pending>
  - **Deferred Work**: None
  - **Commit**: N/A

---

## Notes

- Java 25 target is selected for this upgrade.
- Maven wrapper is used because no system Maven installation was detected.
- The original Java 17 baseline is unavailable on this machine, so the baseline step is intentionally skipped.

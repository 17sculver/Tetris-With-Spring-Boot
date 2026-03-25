# Spring Boot 3.2.0 → 3.3.x Upgrade Plan

## Overview

Upgrade the Tetris Multiplayer project from **Spring Boot 3.2.0** (end-of-support) to **Spring Boot 3.3.12** (LTS - supported until Nov 2026).

**Support Timeline:**
- Spring Boot 3.2.0: OSS support ended Dec 31, 2024 ❌
- Spring Boot 3.3.x: OSS support until Nov 2026 ✅

---

## Phase 1: Pre-Upgrade Assessment

### 1.1 Compatibility Check ✓

| Component | Current | Target | Status |
|-----------|---------|--------|--------|
| Java | 17 | 17 | ✓ Compatible |
| Spring Boot | 3.2.0 | 3.3.12 | ✓ Patch upgrade |
| Spring Security | 6.2.x | 6.3.x | ✓ Compatible |
| Spring Data JPA | 3.2.x | 3.3.x | ✓ Compatible |
| Spring WebSocket | 6.2.x | 6.3.x | ✓ Compatible |
| MySQL Driver | 8.2.0 | 8.2.0 | ✓ No change needed |
| JJWT | 0.11.5 | 0.11.5 | ✓ No change needed |
| Lombok | Latest | Latest | ✓ No change needed |

**Assessment Result:** Low-risk upgrade - no breaking changes expected.

### 1.2 Codebase Review

Current implementation uses:
- Standard Spring Boot patterns ✓
- Modern lambda-based Security configuration ✓
- Standard JPA entity mappings ✓
- STOMP/WebSocket configuration ✓

**No deprecated API usage detected** - Code should work with 3.3.x without changes.

---

## Phase 2: Dependency Updates

### 2.1 Update Parent Version

**File:** `pom.xml`

```xml
<!-- Current -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>

<!-- Target -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.12</version>
    <relativePath/>
</parent>
```

**Impact:** All Spring Boot starters will automatically update to compatible versions.

### 2.2 Verify Dependency Versions

The following are automatically managed by Spring Boot parent and will update:

| Dependency | 3.2.0 | 3.3.12 | Change |
|------------|-------|--------|--------|
| spring-boot-starter-web | 3.2.0 | 3.3.12 | Minor update |
| spring-security-core | 6.2.x | 6.3.x | Patch update |
| spring-data-jpa | 3.2.x | 3.3.x | Patch update |
| spring-websocket | 6.2.x | 6.3.x | Patch update |
| jackson-databind | 2.14.2 | 2.15.x | Minor update |
| lombok | 1.18.x | 1.18.x | No change |

**Manual Dependencies (No Changes Required):**
- `mysql-connector-j:8.2.0` - Already at latest stable
- `jjwt:0.11.5` - Stable, tested version

### 2.3 Update Build Plugins

Spring Boot Maven Plugin will automatically update to 3.3.12.

---

## Phase 3: Code Changes

**Assessment:** ✓ **No code changes required**

The current implementation uses:
- Modern Spring Security lambda syntax ✓
- Standard JPA annotations ✓
- Standard WebSocket/STOMP configuration ✓
- Standard REST controller patterns ✓

All code is compatible with Spring Boot 3.3.x.

---

## Phase 4: Configuration Updates

### 4.1 application.properties

**No changes needed.** Current properties are forward-compatible:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:h2:mem:tetrisdb
spring.h2.console.enabled=true
app.jwt.secret=...
app.jwt.expiration=86400000
```

### 4.2 application-test.properties

**No changes needed.**

---

## Phase 5: Build & Test

### 5.1 Clean Build

```bash
mvn clean install
```

**Expected outcome:**
- ✓ 22 Java files compile without errors
- ✓ All tests pass (if present)
- ✓ JAR packages successfully
- ✓ Minor warnings only (expected in Spring Boot 3.3.x)

### 5.2 Runtime Verification

```bash
mvn spring-boot:run
```

**Verify:**
- ✓ Application starts on port 8080
- ✓ H2 console accessible at `/h2-console`
- ✓ No startup errors in logs
- ✓ Spring Security configuration loads correctly

### 5.3 Functional Testing

1. **Authentication**
   - Register new user
   - Login with credentials
   - Verify JWT token generation
   - Verify Security filter chain works

2. **WebSocket Communication**
   - Create game session
   - Connect via WebSocket
   - Publish/subscribe to STOMP topics
   - Send game move messages

3. **Database Operations**
   - Create users
   - Create game sessions
   - Update game state
   - Query game history

---

## Phase 6: Execution Steps

### Step 1: Update pom.xml
- Change Spring Boot parent version: 3.2.0 → 3.3.12

### Step 2: Refresh Dependencies
```bash
mvn clean install -U
```
This forces Maven to check for new versions.

### Step 3: Verify Build
```bash
mvn clean install
```
Ensure no compilation errors.

### Step 4: Test Application
```bash
mvn spring-boot:run
```
Verify application starts without errors.

### Step 5: Manual Testing
- Test authentication flow
- Test game creation/join
- Test WebSocket connectivity

### Step 6: Commit Changes
```bash
git add pom.xml
git commit -m "Upgrade Spring Boot 3.2.0 to 3.3.12 (LTS)"
```

---

## Phase 7: Rollback Plan (If Needed)

If issues arise:

1. **Immediate Rollback**
   ```bash
   git revert HEAD
   mvn clean install
   ```

2. **Check Logs for Issues**
   - Review Spring Boot startup logs
   - Check for dependency conflicts
   - Verify database migrations

3. **Compatibility Issues**
   - Check Spring Boot release notes for 3.3.x
   - Review dependency changelogs
   - Post to Spring Boot forums if issues persist

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|-----------|
| Compilation errors | Very Low | Medium | Code is compatible with 3.3.x |
| Runtime errors | Very Low | Medium | Modern code patterns used |
| Database migration issues | Very Low | Low | Using same JPA configuration |
| Dependency conflicts | Very Low | Low | Updated by Spring Boot parent |
| Security regression | Very Low | Low | Security config uses modern syntax |

**Overall Risk Level: LOW** ✓

---

## Expected Benefits

After upgrade to Spring Boot 3.3.x:

✅ **Long-term support** - Covered until Nov 2026 (OSS) / Nov 2027 (commercial)
✅ **Security patches** - Latest security updates included
✅ **Performance improvements** - Bug fixes and optimizations
✅ **New features** - Access to Spring Boot 3.3.x enhancements
✅ **Java 21 ready** - Future compatibility with newer Java versions

---

## Timeline

| Phase | Duration | Status |
|-------|----------|--------|
| Phase 1: Assessment | < 5 min | Ready |
| Phase 2: Dependency Updates | < 5 min | Ready |
| Phase 3: Code Changes | N/A | No changes needed |
| Phase 4: Config Updates | N/A | No changes needed |
| Phase 5: Build & Test | 5-10 min | Ready to execute |
| Phase 6: Execution | 10-15 min | Ready to execute |
| **Total** | **~30 minutes** | |

---

## Approval Checklist

- [x] Compatibility assessment complete
- [x] No breaking changes identified
- [x] Rollback plan documented
- [x] Risk assessment completed
- [x] Ready to proceed with upgrade

**Recommendation:** PROCEED with upgrade to Spring Boot 3.3.12

---

## References

- [Spring Boot 3.3.x Release Notes](https://spring.io/projects/spring-boot)
- [Spring Boot 3.2.x to 3.3.x Migration Guide](https://spring.io/projects/spring-boot)
- [Spring Security 6.3.x Release Notes](https://spring.io/projects/spring-security)

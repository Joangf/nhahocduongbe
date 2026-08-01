<div align="center">
  <img src="path/to/logo.png" alt="Logo" width="120" />

  # Hệ Thống Quản Lý Nha Học Đường — Backend Service (`nhahocduongbe`)
  ### School Dental Health Management System — Enterprise REST API Server

  **BVRHM Development Team** &nbsp;|&nbsp; *Client: Bệnh Viện Răng Hàm Mặt Trung Ương TP. Hồ Chí Minh*

  <br />

  <!-- shields.io badges -->
  <a href="https://openjdk.org/projects/jdk/17/">
    <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  </a>
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring_Boot-3.1.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3.1.0" />
  </a>
  <a href="https://www.postgresql.org/">
    <img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL 17" />
  </a>
  <a href="https://redis.io/">
    <img src="https://img.shields.io/badge/Redis-6%2B-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/Architecture-Spring_Modulith-6DB33F?style=for-the-badge" alt="Architecture: Spring Modulith" />
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/License-Proprietary-0052CC?style=for-the-badge" alt="License: Proprietary" />
  </a>
</div>

---

## 1. Executive Summary & Elevator Pitch

**Hệ Thống Quản Lý Nha Học Đường (Backend Service)** is an enterprise-grade, stateless **Java 17 / Spring Boot 3.1.0** medical API server engineered for **Bệnh Viện Răng Hàm Mặt Trung Ương TP. Hồ Chí Minh**. It digitizes and standardizes school-based dental health examination programs across Southern Vietnam, enabling seamless collaboration among central hospital administrators, field dentists, school officials, and parents.

By automating clinical record-keeping, multi-district campaign scheduling, and population-level epidemiological reporting, the platform replaces fragmented paper workflows with an auditable, real-time digital ecosystem. The backend guarantees ACID compliance, high-concurrency odontogram updates, and stringent pediatric data security at scale.

---

## 2. Key Features & Technical Achievements

- **Spring Modulith Bounded Contexts:** Engineered on a strict **Spring Modulith** architecture (`api.auth`, `api.user`, `api.nhahocduong`, `api.common`, `api.system`) to enforce domain isolation, clean dependency boundaries, and event-driven decoupled communication inside a highly maintainable monolith.
- **Odontogram JSONB Clinical Storage:** Longitudinally stores WHO / FDI 32-tooth dental condition states (decayed, filled, missing, sealant, tartar/plaque indices) inside PostgreSQL indexed **JSONB** columns, allowing rich diagnostic schema flexibility without relational table explosion.
- **Robust Security & OTP Verification Flows:** End-to-end stateless authentication featuring **JSON Web Tokens (JWT)**, multi-layered role-based access control (RBAC), organization-scoped data filtering, and server-validated **OTP verification flows** with rate-limiting for user registration and password recovery.
- **Atomic Academic Year Transitions & Rollbacks:** Complex multi-entity state machine capable of promoting and graduating thousands of student records across school classes in a single atomic transaction, backed by complete `system_log` snapshot rollbacks.
- **Enterprise Three-Tier Architecture:** Robust separation of stateless REST API controllers, domain transaction services, and relational/caching persistence layers.

---

## 3. Technology Stack Summary

| Domain / Layer | Technology | Key Capabilities & Role in Backend |
| :--- | :--- | :--- |
| **Runtime & Language** | **Java 17** | LTS release leveraging modern Java features (records, pattern matching, enhanced streams). |
| **Core Framework** | **Spring Boot 3.1.0** | Enterprise application foundation providing DI, REST API controllers, and scheduling. |
| **Architecture** | **Spring Modulith** | Strict domain boundary verification and event-driven module isolation. |
| **Security & Auth** | **Spring Security + JWT** | Stateless Bearer token authentication, RBAC, and method-level security guards. |
| **Data Persistence** | **Hibernate 6 / Spring Data JPA** | ORM abstraction over relational models, JSONB column mappings, and custom projections. |
| **Relational Database** | **PostgreSQL 17** | Primary ACID database utilizing advanced CHECK constraints, triggers, and JSONB schemas. |
| **Caching & In-Memory** | **Redis** | High-performance caching for JWT blacklists, OTP rate-limiting, and dashboard statistics. |
| **Testing Framework** | **JUnit 5 + Mockito + JaCoCo** | Pure isolated unit testing of Service-Layer business logic with line/branch coverage reporting. |

---

## 4. Architecture & System Design

### High-Level Architecture
The backend API server operates as the **Application Tier** within a decoupled Three-Tier Architecture:
1. **Presentation Tier:** Client SPA (`nhahocduongfe/`) communicating exclusively via HTTP/JSON.
2. **Application Tier (This Repository):** Stateless Spring Boot modular monolith handling authentication, clinical workflows, and data orchestration.
3. **Data Tier:** PostgreSQL 17 relational database and Redis in-memory cache.

```
+---------------------------------------------------------------------------+
|               APPLICATION TIER (nhahocduongbe Spring Modulith)            |
|   +-------------------------------------------------------------------+   |
|   |                 Security Filter Chain (JWT / RBAC)                |   |
|   +-------------------------------------------------------------------+   |
|   |  api.auth   |  api.user   |  api.nhahocduong  | api.common | system |   |
|   | (Auth Ctx)  | (User Ctx)  | (Core Dental Ctx) | (Shared)   | (Audit)|   |
+---------------------------------------------------------------------------+
                                      |
                     +----------------+----------------+
                     |                                 |
                     v                                 v
         +-----------------------+         +-----------------------+
         |     PostgreSQL 17     |         |         Redis         |
         |  (nhahocduong Schema) |         |  (Caching / OTP Rate) |
         +-----------------------+         +-----------------------+
```

> [!IMPORTANT]
> **Stateless RESTful API:** The backend API is strictly stateless. **No HTTP sessions** are maintained on the server. Every authenticated request must carry a valid Bearer JSON Web Token (**JWT**) in the `Authorization` header. Security filters validate token signatures, expiration, and role permissions independently on each request.

### Repository Map (`src/main/java/`)
```text
nhahocduongbe/
├── src/main/java/vn/viettel/bvrhm/nhahocduong/
│   └── api/                           # Spring Modulith Bounded Contexts
│       ├── auth/                      # Authentication, JWT tokens, OTP verification
│       ├── user/                      # User administration, accounts, role mappings
│       ├── nhahocduong/               # Core dental domain (Exams, Odontogram, Campaigns)
│       ├── common/                    # Shared utilities, Excel parsers, email services
│       └── system/                    # Audit logs, academic year state machines
└── src/test/java/vn/viettel/bvrhm/nhahocduong/
    └── api/                           # Isolated JUnit 5 / Mockito unit test suites
```

### Core Backend Design Patterns
- **Service-Layer Pattern (`Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository` $\rightarrow$ `Entity`):**
  - **`Controller` Layer:** Responsible strictly for HTTP protocol handling, route mapping, request payload deserialization, input validation (`@Valid`), and HTTP response serialization. **No business logic or database transactions may reside in controllers.**
  - **`Service` Layer:** The transactional core of the application (`@Service`, `@Transactional`). All domain workflows, eligibility checks, campaign orchestration, notification broadcasting, and cross-module interactions occur here. Read-only queries must use `@Transactional(readOnly = true)`.
  - **`Repository` Layer:** Interfaces extending **Spring Data JPA** (`JpaRepository`, `JpaSpecificationExecutor`) or custom **Hibernate** repositories. Handles raw SQL/JPQL queries, pagination, projections, and database locks.
- **Entity $\rightarrow$ DTO Bridging:** JPA entities (`@Entity`) map strictly to database tables and must never be exposed directly to controllers or external API consumers. All data transfer across bounded contexts or REST boundaries must use explicit **DTOs (Records or Lombok Data classes)** mapped via **MapStruct** or dedicated mappers.
- **Distributed API Routing Strategy:** The backend does not use a central route registration table. Every REST controller must declare its own base path using **`@RequestMapping`** annotations on the controller class. New endpoints must strictly follow the established **nested sub-resource URL pattern** to express domain ownership and parent-child relationships (e.g., `/api/patients/{id}/exams` or `/api/exams/{id}/odontogram`).

---

## 5. Core Database Domains & Data Safeguards

### PostgreSQL 17 Schema (`nhahocduong`)
The database comprises **37 tables** organized into three primary domain clusters:
1. **Identity & Access Domain:** Governs security credentials and authorization. Key tables include **Users**, **Roles**, **Permissions**, and **OTP Tokens** (with rate-limiting and expiration timestamps).
2. **Organization Structure Domain:** Maps hierarchical regional and educational institutions. Key tables include **Organizations** (schools, clinics), **School Classes**, **Student Class Affiliations**, and **Academic Years** (supporting atomic multi-class promotion and graduation state transitions).
3. **Clinical Records Domain:** Houses longitudinal patient dental data. Key tables include **Patients**, **Dental Exams**, **Exam Campaigns**, **Exam Schedules**, and structured **Odontogram Records**. Odontogram tooth states are stored in indexed **JSONB** columns.

### Must-Know Backend Data Safeguards
- **Soft-Delete Pattern:** Physical `DELETE` SQL queries are rarely executed against core entities (`Patients`, `Exams`, `Organizations`, `School Classes`). Instead, entities implement a soft-delete mechanism via a boolean **`status`** column (`true` = active, `false` = deleted/inactive). Hibernate entities use `@Where(clause = "status = true")` (or explicit JPA repository queries) to automatically filter out inactive records from application reads.
- **Stateless Authentication:** The Spring Boot backend is completely stateless—**zero HTTP sessions are stored on the server or database**. User identity, role assignments, and authorization expiration are encapsulated entirely within the **JWT Bearer token**. Never attempt to store user state in `HttpSession` or server-side memory.
- **Database Triggers & Constraints:** Business-critical rules are enforced directly at the **PostgreSQL 17** database engine level. For example, username uniqueness, account lock states, and admin-only registration request approvals are safeguarded by custom PostgreSQL triggers such as **`validate_registration_request()`** and table-level `CHECK` constraints. Never bypass database migrations or attempt to circumvent database triggers in application logic.

---

## 6. Developer Onboarding & Local Setup

### Prerequisites
Before starting local development, ensure the following tools are installed globally:
- **Java Development Kit (JDK) 17:** Long-Term Support (LTS) release required for Spring Boot 3.1.0 (`java -version`).
- **Maven 3.x:** Build and dependency management tool (`mvn -version`).
- **Docker & Docker Compose:** Required to run containerized PostgreSQL 17 and Redis instances locally (`docker --version && docker compose version`).

### Step 1: Configure Environment Variables (`.env`)
1. Navigate to the backend root directory:
   ```bash
   cd nhahocduongbe/
   ```
2. Copy the sample environment template to create your local `.env` file:
   ```bash
   cp .env.example .env
   ```
3. Open `.env` and configure the following required properties:
   ```ini
   # Relational Database (PostgreSQL 17)
   DB_URL=jdbc:postgresql://localhost:5432/nhahocduong_db
   DB_USERNAME=postgres
   DB_PASSWORD=secretpassword

   # In-Memory Cache & Rate Limiting (Redis)
   REDIS_HOST=localhost
   REDIS_PORT=6379

   # Security & Authentication (256-bit Base64 Secret)
   JWT_SIGNING_KEY=your_base64_encoded_256bit_jwt_secret_key_here
   JWT_EXPIRATION_MS=86400000

   # SMTP Notification Service (JavaMailSender)
   SMTP_HOST=smtp.gmail.com
   SMTP_PORT=587
   SMTP_USERNAME=your-service-email@gmail.com
   SMTP_PASSWORD=your-app-password
   ```

### Step 2: Boot Local Infrastructure (Docker Compose)
Start the containerized **PostgreSQL 17** database and **Redis** cache server in detached mode:
```bash
docker-compose up -d
```

> [!IMPORTANT]
> **Database Schema Initialization:** On your initial boot, the database schema and seed data must be imported from the repository dump file. Connect to your local PostgreSQL instance (`localhost:5432`) and execute the **`backup02_nhahocduong.sql`** script located in the database backup directory to initialize all **37 tables** in the `nhahocduong` schema:
> ```bash
> # Example import command using psql
> psql -h localhost -U postgres -d nhahocduong_db -f backup02_nhahocduong.sql
> ```

### Step 3: Start the Backend Server (Spring Boot 3.1.0)
With PostgreSQL and Redis running, build the backend artifact and start the Spring Boot runtime using the `local` profile:
```bash
mvn clean install -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
- **Verification:** Watch the terminal logs for `Started NhahocduongApplication in X seconds`.
- **API Endpoint:** The REST API server will be accessible at **`http://localhost:8081`**.
- **API Documentation:** OpenAPI / Swagger UI is available at **`http://localhost:8081/swagger-ui/index.html`** (or `/v3/api-docs`).

---

## 7. Operations, Testing & Debugging

### Testing & Code Coverage
The backend test strategy strictly targets the **Service-Layer** (`*ServiceImpl` classes and shared domain utilities) using high-speed, isolated **JUnit 5** and **Mockito** unit tests.

> [!IMPORTANT]
> **No Spring Context Loading:** To prevent test execution bottlenecks and maintain pure domain isolation, **do not use `@SpringBootTest`** for unit testing. All repository dependencies, mappers, and external services must be mocked using `@Mock` and injected via `@InjectMocks`.

#### Executing the Test Suite & Generating JaCoCo Reports
To execute all isolated unit tests and generate the HTML line-and-branch coverage report via **JaCoCo**, run the following command from inside the `nhahocduongbe/` root directory:

```bash
mvn clean test jacoco:report
```
- **Report Location:** Once the Maven build completes, open the generated interactive HTML report in your browser at:
  ```text
  nhahocduongbe/target/site/jacoco/index.html
  ```
- **Coverage Quality Target:** Ensure all modifications to core services maintain or exceed the established **≥ 80% line and branch coverage** thresholds.

### Common Gotchas & Debugging Guide

| Symptom | Likely Cause | Resolution |
| :--- | :--- | :--- |
| **"Missing" database records in the API** | The record was soft-deleted (**`status = false`**), causing Hibernate's default `@Where(clause = "status = true")` filter or repository query to exclude it. | Check the PostgreSQL table directly using SQL (`SELECT * FROM table_name;` without status filters). To restore the record, set `status = true` in the database or via an administrative unlock endpoint. |
| **Silent database insert or update failures** | A PostgreSQL **`CHECK` constraint** or database trigger (such as **`validate_registration_request()`**) rejected the transaction at the database engine level. | Inspect the Spring Boot console or log files for a **`DataIntegrityViolationException`** or **`PSQLException`**. Verify that payload fields satisfy PostgreSQL constraints (e.g., valid foreign keys, non-null mandatory dates, or enum checks). |
| **HTTP 403 Forbidden on authenticated endpoints** | The JWT token is expired, has an invalid signature, or the user's account status was locked (`status = false`) by an administrator. | Inspect security audit logs. Verify token claims and ensure the user's role has the required authority for the endpoint. |

---

## 8. Governance, Coding Standards & Handover

### Coding Standards
- **Service-Layer Business Logic:** Controllers are strictly HTTP traffic directors. Never write domain calculations, eligibility checks, or `@Transactional` operations inside a Controller. All business logic must reside within dedicated **`*ServiceImpl`** classes.
- **Distributed API Routing Strategy:** Use `@RequestMapping` prefixes on Controllers and adhere to nested sub-resource URL conventions (`/api/patients/{id}/exams`).
- **Entity $\rightarrow$ DTO Bridging:** Always map entities to DTOs via MapStruct before returning API responses.

### Contribution Guidelines
- **Conventional Commits:** All Git commit messages must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:
  - `feat:` — A new feature or user-facing capability.
  - `fix:` — A bug fix.
  - `refactor:` — Code changes that neither fix a bug nor add a feature.
  - `test:` — Adding or updating JUnit 5 unit tests.
  - `chore:` — Routine build tooling, dependency upgrades, or documentation maintenance.
- **Branching Strategy:** Work in feature branches created from `develop` or `main`. Name branches descriptively using the pattern **`feature/ticket-name`** or **`fix/ticket-name`**.
- **Pull Request & Code Review Workflow:** All merges require an explicit Pull Request (PR), successful CI/CD pipeline execution (zero unit test failures), and at least one peer code review approval.

### Extended Documentation (The Source of Truth)
Before designing new features, modifying database schemas, or refactoring bounded contexts, team members **MUST** read the core project documentation files located in the root repository:

1. **[1_SRS.md](file:///c:/Viettel%20Solution%20Intern/src_file_02/1_SRS.md):** **Software Requirements Specification** *(97 endpoints, 37 tables)* — Comprehensive functional specifications, user roles, security matrices, and complete REST API endpoint inventory.
2. **[2_SDD.md](file:///c:/Viettel%20Solution%20Intern/src_file_02/2_SDD.md):** **Software Design Document (Architecture & Data Flows)** — In-depth architectural blueprints, Spring Modulith module boundaries, and subsystem data flow sequences.
3. **[3_UML_Diagrams.md](file:///c:/Viettel%20Solution%20Intern/src_file_02/3_UML_Diagrams.md):** **ERD, Class, Sequence, and Activity Diagrams** — Visual system modeling including Entity-Relationship Diagrams, odontogram state transitions, and campaign workflows.
4. **[4_SVVP_STD.md](file:///c:/Viettel%20Solution%20Intern/src_file_02/4_SVVP_STD.md):** **Test Plans and 25 QA Test Cases** — Software Verification & Validation Plan, STD test catalog, and acceptance criteria across all core domains.
5. **[5_User_Manual.md](file:///c:/Viettel%20Solution%20Intern/src_file_02/5_User_Manual.md):** **End-User Workflows and Troubleshooting** — Illustrated user guides for central hospital administrators, field dentists, school staff, and parents.

---

### Contact & Support
- **Project Lead / Product Owner:** `[Insert Name / Contact Email]` &nbsp;*(Product roadmap, requirement clarifications, clinical domain alignment)*
- **DevOps / Infrastructure Access:** `[Insert Name / Contact Email]` &nbsp;*(PostgreSQL production access, Redis cache, CI/CD runners)*
- **Original Developers (Legacy Support):** `[Insert Team Name / Contact Email]` &nbsp;*(Core codebase handover, Spring Modulith bounded context history)*

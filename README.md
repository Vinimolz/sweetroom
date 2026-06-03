# 🏨 sweetRoom — Advanced Room Booking Engine

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL-blue.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)

A production-grade, highly optimized backend engine for booking rooms, designed specifically to explore advanced software patterns, database optimizations, and complex business logic that extend far beyond basic CRUD operations. 

This project serves as a showcase for mid-to-senior level design challenges, focusing on data consistency under heavy concurrent load, advanced scheduling algorithms, database representation efficiencies, and extensible architectural pipelines.

---

## 🚀 Key Architectural Patterns Implemented

### 1. Dynamic Validation Pipelines (Chain of Responsibility Pattern)
Instead of writing massive, rigid `if-else` blocks in the service layer, validation is structured as a pipeline of decoupled components.
* **How it works:** All validation rules implement the `ReservationValidator` interface.
* **Spring-Injected Autowiring:** The `ReservationService` receives a `List<ReservationValidator>` via constructor injection. Spring automatically detects all beans implementing this interface and compiles them into a list.
* **Deterministic Sequencing:** The execution order of validators is strictly controlled using Spring's `@Order` annotations (e.g., executing basic time range parsing before querying database overlap status).
* **Extensibility:** Adding a new validation rule (e.g., user limit verification) simply requires creating a new `@Component` class implementing the validator interface; **the core service logic remains untouched**.

```
[CreateReservationDTO] 
        │
        ▼
┌──────────────────────────────┐
│  ReservationService          │
│  (Orchestrates Pipeline)     │
└──────────────┬───────────────┘
               │
               ├─► [Order 1] TimeRangeValidator (Validates start < end)
               ├─► [Order 2] OperatingHoursValidator (Checks room operating limits)
               └─► [Order 3] RoomAvailabilityValidator (Verifies database overlap)
```

### 2. Space-Efficient Weekdays Bitmasking
Storing a list of active operational weekdays for rooms (e.g., Mondays, Wednesdays, Fridays) typically requires a separate database table mapping room IDs to weekdays (a costly join table) or storing it as a text array. Instead, this system utilizes a bitmask stored in a single integer column (`available_days_mask`).
* **Conversion Layer:** A custom JPA `AttributeConverter` (`DayOfWeekBitmaskConverter`) handles serialization.
* **Bitwise Operations:**
  * **To DB:** Iterates over the list of `DayOfWeek` values and turns on the corresponding bit: `mask |= (1 << (day.getValue() - 1))`
  * **From DB:** Evaluates the integer value against active bits to reconstruct the `List<DayOfWeek>`: `(dbData & bit) != 0`
* **Performance Benefit:** Zero junction tables, minimal storage footprint, and highly indexing-friendly.

### 3. Constant-Time Reservation Overlap Algorithm
Checking if a requested booking slot $(S_{new}, E_{new})$ overlaps with any existing booked slot $(S_{existing}, E_{existing})$ on a given day requires mathematically sound logic.
Instead of loading all bookings into memory or using complicated nested conditions, the repository uses a single, highly performant JPQL query:

$$\text{Overlap} \iff S_{new} < E_{existing} \quad\text{and}\quad E_{new} > S_{existing}$$

In Spring Data JPA (`ReservationRepository`):
```java
@Query("SELECT COUNT(r) > 0 FROM Reservation r " +
        "WHERE r.room.id = :roomId " +
        "AND r.reservationDate = :date " +
        "AND r.reservationStatus <> com.vinicius.sweetRoom.model.enums.ReservationStatus.CANCELLED " +
        "AND (r.reservationStart < :endTime AND r.reservationEnd > :startTime)")
boolean hasOverlappingReservation(...);
```

### 4. DTO Pattern & Clean Boundaries
To keep database entities isolated from the API transport layer:
* **Java Records:** Standardized DTOs (e.g., `CreateReservationDTO`, `ResponseReservationDTO`) represent request/response bodies.
* **Jakarta Bean Validation:** Dynamic validation constraints like `@NotNull` and `@FutureOrPresent` are handled at the controller threshold, failing fast before invoking any service layer execution.
* **Unified Error Mapping:** A `GlobalExceptionHandler` intercepts exceptions and translates them into uniform, clean JSON structures for the client.

---

## 🌟 Upcoming Challenges & Roadmap

These milestones target complex, real-world problems typical of high-traffic enterprise architectures.

### 🌟 Challenge 1: Concurrency Control (Preventing Double-Booking)
* **The Problem:** The current check-then-write mechanism (`SELECT COUNT(...)` followed by `INSERT`) is vulnerable to race conditions under heavy load. If two concurrent requests hit validation for the same slot at the exact same millisecond, both will read 0 reservations and write to the database, resulting in a double-booking.
* **The Solution:** Introduce **Pessimistic Locking** on the database layer. In `RoomRepository`, define a locked retrieval query:
  ```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Room r WHERE r.id = :id")
  Optional<Room> findByIdForUpdate(@Param("id") Long id);
  ```
  This forces transaction blockades, ensuring that only one thread can validate and book a room at a time, completely preventing race conditions.

### 🌟 Challenge 2: Advanced REST Queries (Pagination & Filtering)
* **The Problem:** Endpoints returning unbounded lists of records expose the database to performance decay.
* **The Solution:**
  * Implement pagination and sorting for queries using Spring's `Pageable` and returning `Page<ResponseReservationDTO>`.
  * Create dynamic REST endpoints filtering reservations by attributes (e.g., status, date ranges, user ID) dynamically using JPA Specifications or Criteria API.

### 🌟 Challenge 3: Complex Interval Logic (Room Availability Schedule API)
* **The Goal:** Build an endpoint: `GET /rooms/{id}/schedule?date=YYYY-MM-DD`
* **Output Format:**
  ```json
  [
    { "status": "OPEN", "start": "09:00", "end": "11:00" },
    { "status": "OCCUPIED", "start": "11:00", "end": "12:30", "byUser": "Vinicius" },
    { "status": "OPEN", "start": "12:30", "end": "17:00" }
  ]
  ```
* **Algorithm Profile:** This requires splitting a room's operating hours interval by subtracting active reservation slots and stitching together the resultant availability timeline. This exercises data structures and computational interval math in pure Java.

### 🌟 Challenge 4: Multi-Tiered Testing Strategy
To build complete confidence in the codebase:
* **Isolated Unit Tests:** Mock-based tests to isolate individual validators in the Chain of Responsibility.
* **Integration Tests:** `@SpringBootTest` suites testing the automatic discovery, ordering, and execution of validators when context changes.

---

## 🛠️ Tech Stack
* **Java 17** (LTS)
* **Spring Boot 3.x**
* **Spring Data JPA** (Hibernate)
* **PostgreSQL**
* **Docker & Docker Compose** (Containerized database)
* **Maven** (Dependency management)

---

## ⚙️ Setting Up and Running

### Prerequisites
* **Java 17 JDK** or higher installed.
* **Docker Desktop** installed and running.

### 1. Configure the Environment
Create a `.env` file in the root directory (based on `.env` or typical configs):
```env
POSTGRES_DB=sweetroom
POSTGRES_USER=postgres
POSTGRES_PASSWORD=changeme
DB_URL=jdbc:postgresql://localhost:5432/sweetroom
DB_USERNAME=postgres
DB_PASSWORD=changeme
```

### 2. Start PostgreSQL Container
Spin up the database using Docker Compose:
```bash
docker compose up -d
```

### 3. Run the Application
Start the Spring Boot dev server:
```bash
mvnw spring-boot:run
```

### 4. Run the Test Suite
Execute the unit and integration tests:
```bash
mvnw test
```

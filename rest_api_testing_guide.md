# REST API Testing Strategy Guide: Spring Boot

This guide organizes the main testing strategies, target layers, annotations, and best practices for testing REST APIs in a Spring Boot ecosystem.

---

## The Testing Pyramid for REST APIs

```text
       ▲
      / \       End-to-End (E2E)  [Postman, REST Assured]
     /   \      Integration Tests  [@SpringBootTest]
    /     \     Slice Tests (Web/DB)  [@WebMvcTest, @DataJpaTest]
   /_______\    Unit Tests (Business Logic)  [JUnit + Mockito]
```

---

## Overview of Test Types

| Test Type | Target Layer | Goal | Dependencies | Key Spring Annotations |
| :--- | :--- | :--- | :--- | :--- |
| **Unit Test** | Service, Domain Models, Validators | Test pure business logic, calculations, and rules in isolation. | All dependencies are mocked. | Standard JUnit 5, Mockito (`@ExtendWith`) |
| **Data Slice Test** | Repository (`@Repository`), Database | Test database constraints, custom queries, JPQL, and locking. | Uses an embedded DB (H2) or Docker containers. | `@DataJpaTest` |
| **Web Slice Test** | Controller (`@RestController`) | Test serialization (JSON), input validation (`@Valid`), status codes, and routing. | Mocks services. | `@WebMvcTest`, `MockMvc` |
| **Integration Test** | Full Context | Test the collaboration of multiple layers (Controller -> Service -> DB). | Runs the full context. | `@SpringBootTest` |
| **Security Test** | Authentication, Filter Chain, AuthZ | Test if public routes are accessible, and if private routes reject/accept users based on roles. | Simulated mock users. | `@WebMvcTest` or `@SpringBootTest` + `@WithMockUser` |
| **Concurrency Test** | Service / Database Lock | Test race conditions, pessimistic/optimistic locking, and data integrity under load. | Multithreaded test runners. | Custom executors inside JUnit |

---

## Deep Dive & Best Practices

### 1. Unit Tests (Service Layer)
* **What you test**: Method returns, exception boundaries, calculations.
* **Why it's fast**: It doesn't load the Spring Context or start a database. Mockito intercepts calls to repositories.
* **Best Practice**: Use Mockito's `@InjectMocks` on the service, and `@Mock` on its dependencies.
* **Example**:
  ```java
  @ExtendWith(MockitoExtension.class)
  class UserServiceTest {
      @Mock private UserRepository repo;
      @InjectMocks private UserService service;
      
      @Test
      void shouldCreateUser() { ... }
  }
  ```

### 2. Database Slice Tests (`@DataJpaTest`)
* **What you test**: Custom `@Query` statements, database constraints (like duplicate emails throwing exceptions), and locking behavior.
* **Why it's clean**: Spring automatically boots up an in-memory database (like H2), runs schema migrations, executes the test, and rolls back the database transaction after each test.
* **Example**:
  ```java
  @DataJpaTest
  class UserRepositoryTest {
      @Autowired private UserRepository repo;
  }
  ```

### 3. Controller Web Slice Tests (`@WebMvcTest`)
* **What you test**:
  * Did `/rooms` return a `200 OK` or `400 Bad Request` when inputs are invalid?
  * Is the Java object converted to JSON correctly?
  * Do request mappings (`@GetMapping`, `@PostMapping`) match the URL rules?
* **Why it's clean**: It only loads components related to the web layer (Controllers, Filters, Advice). It does not load repositories or database connections. You mock the service layer using `@MockBean`.
* **Example**:
  ```java
  @WebMvcTest(RoomController.class)
  class RoomControllerTest {
      @Autowired private MockMvc mockMvc;
      @MockBean private RoomService roomService;
  }
  ```

### 4. Integration Tests (`@SpringBootTest`)
* **What you test**: The flow of data through the entire system.
* **Best Practice**: Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`. This starts a real, local Tomcat server running on a random port, ensuring your request flows through the entire system (Filters -> Servlet -> Controller -> Service -> DB).

### 5. Security & Authorization Tests
* **What you test**: Does a user with role `STUDENT` get a `403 Forbidden` if they try to create a room?
* **Best Practice**: Use `spring-security-test` dependencies. The annotation `@WithMockUser` lets you mock security contexts dynamically:
  ```java
  @Test
  @WithMockUser(roles = "STUDENT")
  void shouldRejectStudentFromCreatingRoom() throws Exception {
      mockMvc.perform(post("/rooms")...)
             .andExpect(status().isForbidden());
  }
  ```

### 6. Concurrency Tests
* **What you test**: Database race conditions (e.g. two users booking the same room at the exact same millisecond).
* **Best Practice**: Use Java's `CountDownLatch` or `ExecutorService` inside a standard JUnit test to launch multiple threads executing concurrent tasks simultaneously, then assert that only one operation succeeded and the database state remains consistent.

### 7. End-to-End (E2E) Testing
* **What you test**: Testing the fully compiled and deployed API from the outside.
* **Why it matters**: It is the closest simulation to how your frontend or mobile client will consume the API.
* **Tools**:
  * **Postman**: Great for writing manual/automated test suites against a running server.
  * **REST Assured**: A Java library that lets you write readable HTTP assertions directly from your IDE:
    ```java
    RestAssured.given()
        .header("Authorization", "Bearer token...")
        .when().get("/rooms")
        .then().statusCode(200);
    ```

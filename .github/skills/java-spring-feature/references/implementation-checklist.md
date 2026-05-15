# Implementation Checklist

Use this checklist to validate that your Spring Boot feature is complete and production-ready.

## Phase 1: Entity & Repository ✓

- [ ] **Entity Class Created**
  - [ ] `@Entity` and `@Table` annotations applied
  - [ ] `@Id` with `@GeneratedValue` strategy
  - [ ] All required fields with appropriate `@Column` annotations
  - [ ] Relationships defined (ManyToOne, OneToMany, ManyToMany)
  - [ ] Cascade strategy set appropriately
  - [ ] Timestamps (createdAt, updatedAt) included
  - [ ] Using Lombok annotations (@Data, @AllArgsConstructor, @NoArgsConstructor)

- [ ] **Repository Interface Created**
  - [ ] Extends `JpaRepository<Entity, Long>`
  - [ ] Has `@Repository` annotation
  - [ ] Custom query methods added for business requirements
  - [ ] `@Query` annotations used for complex queries if needed

## Phase 2: DTOs ✓

- [ ] **Request DTO Created**
  - [ ] Located in `/dto/` folder
  - [ ] Has validation annotations (@NotBlank, @NotNull, etc.)
  - [ ] Custom validators applied if needed (@ValidPrice, @ValidCardNumber, etc.)
  - [ ] Clear error messages on all validators
  - [ ] Only includes fields that clients submit
  - [ ] No ID field (system-generated)
  - [ ] No timestamp fields (system-generated)

- [ ] **Response DTO Created**
  - [ ] Located in `/dto/` folder
  - [ ] Includes ID field
  - [ ] Includes timestamp fields (createdAt, updatedAt)
  - [ ] Includes all fields clients need to receive
  - [ ] Nested DTOs for related entities (if applicable)
  - [ ] No validation annotations

## Phase 3: Service ✓

- [ ] **Service Interface Created**
  - [ ] Located in `/service/` folder
  - [ ] Defines all CRUD operations (create, getById, getAll, update, delete)
  - [ ] Method signatures with appropriate DTOs
  - [ ] Clear method documentation

- [ ] **Service Implementation Created**
  - [ ] Located in `/service/impl/` folder
  - [ ] Implements the service interface
  - [ ] Has `@Service` annotation
  - [ ] Uses `@RequiredArgsConstructor` for dependency injection
  - [ ] Injects Repository and Mapper dependencies
  - [ ] **Create Method**
    - [ ] Validates business rules before creating
    - [ ] Throws `BadRequestException` for validation errors
    - [ ] Converts RequestDTO to Entity using mapper
    - [ ] Saves to database
    - [ ] Returns ResponseDTO
  - [ ] **Get by ID Method**
    - [ ] Throws `ResourceNotFoundException` if not found
    - [ ] Converts Entity to ResponseDTO
  - [ ] **Get All Method**
    - [ ] Returns list of ResponseDTOs
    - [ ] Handles empty results gracefully
  - [ ] **Update Method**
    - [ ] Throws `ResourceNotFoundException` if not found
    - [ ] Validates business rules
    - [ ] Updates only provided fields
    - [ ] Saves and returns updated ResponseDTO
  - [ ] **Delete Method**
    - [ ] Throws `ResourceNotFoundException` if not found
    - [ ] Handles cascading deletes properly
  - [ ] Exception handling is comprehensive

- [ ] **Mapper Created**
  - [ ] Located in `/mapping/` folder
  - [ ] Has `@Component` annotation
  - [ ] `toEntity()` method converts RequestDTO to Entity
  - [ ] `toResponseDTO()` method converts Entity to ResponseDTO
  - [ ] Handles null values gracefully
  - [ ] Complex relationships properly mapped

## Phase 4: Controller ✓

- [ ] **Controller Created**
  - [ ] Located in `/controller/` folder
  - [ ] Has `@RestController` annotation
  - [ ] `@RequestMapping` defines base path
  - [ ] Has `@RequiredArgsConstructor` and injects Service
  - [ ] Uses `@Tag` for Swagger documentation

- [ ] **POST Endpoint (Create)**
  - [ ] Maps to correct URL
  - [ ] `@PostMapping` annotation
  - [ ] Accepts `@RequestBody` with `@Valid` annotation
  - [ ] Returns `201 Created` status for successful creation
  - [ ] Returns `FeatureResponseDTO`
  - [ ] `@RateLimit` applied with appropriate limits
  - [ ] `@Operation` Swagger annotation added

- [ ] **GET Endpoint (Get by ID)**
  - [ ] Maps to `/{id}` path
  - [ ] `@GetMapping` annotation
  - [ ] Accepts path variable for ID
  - [ ] Returns `200 OK` status
  - [ ] Returns `FeatureResponseDTO`
  - [ ] `@Operation` Swagger annotation added

- [ ] **GET All Endpoint**
  - [ ] Maps to base path with `@GetMapping`
  - [ ] Returns `200 OK` status
  - [ ] Returns list of `FeatureResponseDTO`
  - [ ] `@Operation` Swagger annotation added

- [ ] **PUT Endpoint (Update)**
  - [ ] Maps to `/{id}` path
  - [ ] `@PutMapping` annotation
  - [ ] Accepts both path variable and request body with `@Valid`
  - [ ] Returns `200 OK` status
  - [ ] Returns updated `FeatureResponseDTO`
  - [ ] `@RateLimit` applied
  - [ ] `@Operation` Swagger annotation added

- [ ] **DELETE Endpoint**
  - [ ] Maps to `/{id}` path
  - [ ] `@DeleteMapping` annotation
  - [ ] Accepts path variable for ID
  - [ ] Returns `204 No Content` status on success
  - [ ] `@RateLimit` applied
  - [ ] `@Operation` Swagger annotation added

## Phase 5: Validation & Security ✓

- [ ] **DTO Validation**
  - [ ] All required fields have `@NotBlank` or `@NotNull`
  - [ ] Business rules validated with custom annotations
  - [ ] Error messages are clear and user-friendly
  - [ ] Validation happens at controller level with `@Valid`

- [ ] **Service Validation**
  - [ ] Business logic validated in service layer
  - [ ] Appropriate exceptions thrown
  - [ ] No invalid states allowed in database

- [ ] **Rate Limiting**
  - [ ] Sensitive endpoints (login, payment) rate-limited
  - [ ] Write operations rate-limited
  - [ ] Appropriate `maxAttempts` and `windowSizeSeconds` set
  - [ ] `@RateLimit` annotation on controller methods

- [ ] **Error Handling**
  - [ ] Service throws appropriate exceptions:
    - [ ] `BadRequestException` for validation errors
    - [ ] `ResourceNotFoundException` for missing resources
    - [ ] `OutOfStockException` for inventory issues
    - [ ] Custom exceptions for business logic errors
  - [ ] GlobalExceptionHandler catches all exceptions
  - [ ] Errors return appropriate HTTP status codes

## Phase 6: Documentation & Testing ✓

- [ ] **Swagger Documentation**
  - [ ] `@Tag` on controller
  - [ ] `@Operation` on each endpoint
  - [ ] `@ApiResponse` annotations for error cases (if using)
  - [ ] Request/Response examples visible in Swagger UI

- [ ] **Code Documentation**
  - [ ] Javadoc on public methods (optional but recommended)
  - [ ] Complex logic has inline comments
  - [ ] Entity relationships documented

- [ ] **Testing**
  - [ ] Unit tests for service layer (optional but recommended)
  - [ ] Integration tests for controller endpoints (optional but recommended)
  - [ ] Error scenarios tested

## Phase 7: Code Quality ✓

- [ ] **Naming Conventions**
  - [ ] Classes follow PascalCase
  - [ ] Methods/fields follow camelCase
  - [ ] Constants follow UPPER_CASE
  - [ ] Meaningful names (no `data1`, `temp`, etc.)

- [ ] **Project Structure**
  - [ ] Files in correct directories
  - [ ] Package names follow convention: `com.JaimeAmuedoJAH.backend.<layer>`

- [ ] **Code Style**
  - [ ] Consistent indentation (4 spaces)
  - [ ] No unused imports
  - [ ] No dead code
  - [ ] Follows project conventions

- [ ] **Performance**
  - [ ] N+1 query issues avoided
  - [ ] Eager/lazy loading configured properly
  - [ ] No unnecessary database calls

## Pre-Deployment Checklist ✓

- [ ] Application compiles without errors
- [ ] All tests pass (if applicable)
- [ ] No security vulnerabilities:
  - [ ] SQL injection prevented
  - [ ] XSS prevention in place
  - [ ] CSRF protection if needed
  - [ ] Sensitive data not logged
- [ ] Rate limiting prevents brute force
- [ ] Error messages don't expose system details
- [ ] Documentation is updated
- [ ] Code is committed to version control

## Quality Assessment

**Score your feature:**

- **✅ Gold:** All items checked → Production ready
- **⚠️ Silver:** 90%+ items checked → Minor improvements needed
- **❌ Bronze:** <90% items checked → Needs significant work

---

## Notes & Issues Found

```
Use this space to document any issues or decisions made during implementation:

Example:
- [ ] Issue: N+1 query problem with categories
  - Solution: Used @Query with LEFT JOIN FETCH
- [ ] Decision: Used manual mapper instead of MapStruct for simplicity
- [ ] Performance: Added index on email field for faster lookups
```

# Design Decisions for Spring Features

This reference explains common architectural decisions when implementing features.

## Entity Design

### When to Use Relationships

**ManyToOne** - Use when the entity belongs to another entity
```java
@ManyToOne
@JoinColumn(name = "usuario_id", nullable = false)
private UsuarioEntity usuario;
```

**OneToMany** - Use when the entity contains multiple child entities
```java
@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
private List<CarritoItemEntity> carritoItems;
```

**ManyToMany** - Use when entities have a many-to-many relationship
```java
@ManyToMany
@JoinTable(
    name = "producto_categoria",
    joinColumns = @JoinColumn(name = "producto_id"),
    inverseJoinColumns = @JoinColumn(name = "categoria_id")
)
private Set<CategoriaEntity> categorias;
```

### Cascade Strategy

- **PERSIST**: Save related entities when parent is saved
- **MERGE**: Update related entities when parent is updated
- **REMOVE**: Delete related entities when parent is deleted
- **ALL**: Apply all operations
- **orphanRemoval = true**: Delete child entities if removed from collection

### Timestamps

Always include audit timestamps:
```java
@Temporal(TemporalType.TIMESTAMP)
@CreationTimestamp
private LocalDateTime createdAt;

@Temporal(TemporalType.TIMESTAMP)
@UpdateTimestamp
private LocalDateTime updatedAt;
```

---

## DTO Design

### Request vs Response DTOs

**RequestDTO:**
- Contains only fields that clients **submit**
- Includes validation annotations
- Excludes timestamps (system-generated)
- Excludes ID (system-generated for new records)

**ResponseDTO:**
- Contains all fields clients **receive**
- Includes ID, timestamps, computed fields
- May include related entity information (nested DTOs)
- No validation annotations needed

### Nested DTOs

For related entities, create separate DTOs:

```java
// Main DTO
@Data
public class OrdenResponseDTO {
    private Long id;
    private UsuarioResponseDTO usuario;  // Nested DTO
    private List<OrdenItemResponseDTO> items;
}

// Related DTO
@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
}
```

---

## Service Design

### Validation Levels

**Layer 1: DTO Validation**
```java
@NotBlank
@ValidPrice
private Double precio;
```

**Layer 2: Service Business Logic**
```java
if (producto.getStock() < cantidad) {
    throw new OutOfStockException("Stock insuficiente");
}
```

**Layer 3: Database Constraints**
```java
@Column(nullable = false, unique = true)
private String email;
```

### Exception Hierarchy

```
BadRequestException          // Validation/business rule violation (400)
ResourceNotFoundException    // Resource not found (404)
OutOfStockException         // Stock insufficient (400)
UnauthorizedException       // Access denied (401)
ConflictException           // Resource conflict/duplicate (409)
```

---

## Controller Design

### HTTP Methods

- **POST** /resource → Create new resource
- **GET** /resource → Get all resources (with pagination/filters)
- **GET** /resource/{id} → Get specific resource
- **PUT** /resource/{id} → Update entire resource
- **PATCH** /resource/{id} → Partial update
- **DELETE** /resource/{id} → Delete resource

### Status Codes

- **200 OK** - GET success
- **201 Created** - POST success
- **204 No Content** - DELETE success, or operations with no response body
- **400 Bad Request** - Validation error, invalid input
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Authorization failure
- **404 Not Found** - Resource not found
- **409 Conflict** - Resource conflict (e.g., duplicate entry)
- **429 Too Many Requests** - Rate limit exceeded
- **500 Internal Server Error** - Server error (should not happen with proper validation)

### Rate Limiting Presets

```java
// Authentication endpoints
@RateLimit(maxAttempts = 10, windowSizeSeconds = 300)

// Registration endpoints
@RateLimit(maxAttempts = 5, windowSizeSeconds = 300)

// Payment operations
@RateLimit(maxAttempts = 20, windowSizeSeconds = 600)

// General CRUD operations
@RateLimit(maxAttempts = 100, windowSizeSeconds = 60)

// Read-only operations (can use higher limits)
@RateLimit(maxAttempts = 1000, windowSizeSeconds = 60)
```

---

## Custom Validators

Implement validators for complex business rules:

```java
// Annotation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MyValidatorImpl.class)
public @interface MyValidator {
    String message() default "Invalid value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Implementation
@Component
public class MyValidatorImpl implements ConstraintValidator<MyValidator, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validation logic
        return true;
    }
}

// Usage
@Data
public class MyDTO {
    @MyValidator(message = "Custom error message")
    private String field;
}
```

---

## Data Pagination (Future Enhancement)

When implementing pagination:

```java
// Controller
@GetMapping
public ResponseEntity<Page<FeatureResponseDTO>> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id") String sortBy) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<FeatureResponseDTO> response = service.getAll(pageable);
    return ResponseEntity.ok(response);
}

// Service
public Page<FeatureResponseDTO> getAll(Pageable pageable) {
    return repository.findAll(pageable)
        .map(mapper::toResponseDTO);
}
```

---

## Security Considerations

### Authentication

- All protected endpoints require JWT token
- Token passed in header: `Authorization: Bearer <token>`
- Implement in `SecurityConfig.java`

### Authorization

- Use `@Secured`, `@PreAuthorize` for role-based access
- Check resource ownership in service layer

### Sensitive Data

- Never log passwords, CVVs, tokens
- Use `@JsonIgnore` or separate DTOs to exclude sensitive fields
- Hash sensitive values (CVV using `CVVConverter`)

### SQL Injection Prevention

- Use parameterized queries (Spring Data JPA does this automatically)
- Never concatenate user input into queries

---

## Testing Considerations

### Unit Tests (Service Layer)

- Test business logic
- Mock repositories
- Test exception scenarios

### Integration Tests (Controller Layer)

- Test endpoints with MockMvc
- Test validation
- Test error responses

### Test Data

- Use `@BeforeEach` to set up test data
- Consider using an embedded H2 database
- Clean up after each test with `@AfterEach`

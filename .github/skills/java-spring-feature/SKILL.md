---
name: java-spring-feature
description: 'Implement complete Spring Boot features following layered architecture (Entity → Repository → DTO → Service → Controller). Use when: adding new modules/endpoints, creating business entities, implementing REST APIs with proper separation of concerns.'
argument-hint: 'Feature name (e.g., "Inventory Management" or "Review System")'
user-invocable: true
---

# Java Spring Feature Implementation

## Overview

This skill guides you through implementing a complete backend feature in Spring Boot following the project's layered architecture pattern:

```
Controller (REST API) 
    ↓ 
Service (Business Logic)
    ↓ 
Repository (Data Access)
    ↓ 
Entity (Database Model)
```

Each layer includes DTOs, validation, mapping, and exception handling.

---

## When to Use

- ✅ Adding new entities and endpoints (Usuarios, Productos, Órdenes, etc.)
- ✅ Extending existing modules with new functionality
- ✅ Creating REST APIs with validation and security
- ✅ Implementing CRUD operations with business logic

---

## Architecture Layers Quick Reference

| Layer | Responsibility | Key Patterns |
|-------|-----------------|--------------|
| **Entity** | JPA database model | `@Entity`, `@Table`, relationships, constraints |
| **Repository** | Data access (CRUD) | Spring Data JPA interface, custom queries |
| **DTO** | Data transfer objects | Request (input), Response (output), validation annotations |
| **Service** | Business logic | Validation, calculations, orchestration, exception handling |
| **Controller** | REST endpoints | `@RestController`, `@RequestMapping`, HTTP methods, security |
| **Mapping** | DTO ↔ Entity conversion | MapStruct or manual mapper classes |

---

## Step-by-Step Implementation Process

### Phase 1: Database Layer (Entity & Repository)

#### Step 1: Create Entity Class

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/entity/<Feature>Entity.java`

**Pattern:**
```java
@Entity
@Table(name = "features")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    // Relationships if needed
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    
    // Timestamps for audit
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**Decisions:**
- Determine all fields needed for the feature
- Add relationships (ManyToOne, OneToMany, ManyToMany) if needed
- Use appropriate JPA annotations for constraints
- Include audit fields (createdAt, updatedAt) for tracking

#### Step 2: Create Repository Interface

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/repository/<Feature>Repository.java`

**Pattern:**
```java
@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {
    
    // Custom query methods if needed
    List<FeatureEntity> findByName(String name);
    
    @Query("SELECT f FROM FeatureEntity f WHERE f.active = true ORDER BY f.createdAt DESC")
    List<FeatureEntity> findAllActive();
}
```

**Decisions:**
- Spring Data JPA provides basic CRUD automatically (save, findById, findAll, delete)
- Add custom query methods for business requirements
- Use `@Query` for complex queries

---

### Phase 2: Data Transfer Layer (DTOs)

#### Step 3: Create Request DTO

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/dto/<Feature>RequestDTO.java`

**Pattern:**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    // Add custom validators if needed
    @ValidPrice
    private Double price;
    
    @ValidStock
    private Integer stock;
}
```

**Decisions:**
- Validate all inputs using Jakarta Validation annotations
- Use custom validators for complex business rules
- Include helpful error messages
- Only include fields that clients need to send

#### Step 4: Create Response DTO

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/dto/<Feature>ResponseDTO.java`

**Pattern:**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Decisions:**
- Include all fields needed by the client
- Add computed/derived fields if needed
- Include relationships as nested DTOs (use separate DTO classes for related entities)

---

### Phase 3: Business Logic Layer (Service)

#### Step 5: Create Service Interface

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/service/<Feature>Service.java`

**Pattern:**
```java
public interface FeatureService {
    
    FeatureResponseDTO create(FeatureRequestDTO request);
    
    FeatureResponseDTO getById(Long id);
    
    List<FeatureResponseDTO> getAll();
    
    FeatureResponseDTO update(Long id, FeatureRequestDTO request);
    
    void delete(Long id);
}
```

#### Step 6: Create Service Implementation

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/service/impl/<Feature>ServiceImpl.java`

**Pattern:**
```java
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {
    
    private final FeatureRepository repository;
    private final FeatureMapper mapper;
    
    @Override
    public FeatureResponseDTO create(FeatureRequestDTO request) {
        // Validation logic
        if (repository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Feature with name already exists");
        }
        
        // Convert DTO to Entity
        FeatureEntity entity = mapper.toEntity(request);
        
        // Save to database
        FeatureEntity saved = repository.save(entity);
        
        // Convert back to Response DTO
        return mapper.toResponseDTO(saved);
    }
    
    @Override
    public FeatureResponseDTO getById(Long id) {
        FeatureEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }
    
    @Override
    public List<FeatureResponseDTO> getAll() {
        return repository.findAll()
            .stream()
            .map(mapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public FeatureResponseDTO update(Long id, FeatureRequestDTO request) {
        FeatureEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        
        // Update fields
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        
        FeatureEntity updated = repository.save(entity);
        return mapper.toResponseDTO(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Feature not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
```

**Decisions:**
- Validate business rules (uniqueness, constraints, calculations)
- Use custom exceptions (BadRequestException, ResourceNotFoundException, OutOfStockException)
- Handle all error scenarios gracefully
- Use mapper to convert between DTOs and entities
- Add security checks (authorization, data ownership) if needed

---

### Phase 4: API Layer (Controller)

#### Step 7: Create Controller

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/controller/<Feature>Controller.java`

**Pattern:**
```java
@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
@Tag(name = "Feature Management", description = "APIs for managing features")
public class FeatureController {
    
    private final FeatureService service;
    
    @PostMapping
    @Operation(summary = "Create a new feature")
    @RateLimit(maxAttempts = 20, windowSizeSeconds = 300)
    public ResponseEntity<FeatureResponseDTO> create(
        @Valid @RequestBody FeatureRequestDTO request) {
        FeatureResponseDTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get feature by ID")
    public ResponseEntity<FeatureResponseDTO> getById(@PathVariable Long id) {
        FeatureResponseDTO response = service.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all features")
    public ResponseEntity<List<FeatureResponseDTO>> getAll() {
        List<FeatureResponseDTO> response = service.getAll();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a feature")
    @RateLimit(maxAttempts = 20, windowSizeSeconds = 300)
    public ResponseEntity<FeatureResponseDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody FeatureRequestDTO request) {
        FeatureResponseDTO response = service.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a feature")
    @RateLimit(maxAttempts = 10, windowSizeSeconds = 300)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Decisions:**
- Add `@RateLimit` for sensitive operations
- Use `@Valid` to trigger DTO validation
- Include `@Operation` for Swagger documentation
- Use appropriate HTTP status codes
- Handle authentication/authorization if needed (using Spring Security)

---

### Phase 5: Mapping Layer (Optional but Recommended)

#### Step 8: Create Mapper

**Location:** `src/main/java/com/JaimeAmuedoJAH/backend/mapping/<Feature>Mapper.java`

**Pattern:**
```java
@Component
public class FeatureMapper {
    
    public FeatureEntity toEntity(FeatureRequestDTO dto) {
        if (dto == null) return null;
        
        FeatureEntity entity = new FeatureEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        
        return entity;
    }
    
    public FeatureResponseDTO toResponseDTO(FeatureEntity entity) {
        if (entity == null) return null;
        
        FeatureResponseDTO dto = new FeatureResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}
```

---

## Cross-Cutting Concerns

### Exception Handling

Exceptions are automatically handled by `GlobalExceptionHandler`. Throw appropriate exceptions from services:

```java
// Validation/business logic errors
throw new BadRequestException("Error message");

// Resource not found
throw new ResourceNotFoundException("Resource not found");

// Out of stock
throw new OutOfStockException("Insufficient stock");

// Unauthorized
throw new UnauthorizedException("Access denied");
```

### Validation

Use Jakarta Validation annotations in DTOs:

```java
@NotBlank
@NotNull
@Min(0)
@Max(100)
@ValidCardNumber
@ValidPrice
@ValidStock
@ValidExpirationDate
```

### Rate Limiting

Protect sensitive endpoints:

```java
@RateLimit(maxAttempts = 10, windowSizeSeconds = 300)  // Login (10 attempts per 5 min)
@RateLimit(maxAttempts = 5, windowSizeSeconds = 300)   // Register (5 attempts per 5 min)
@RateLimit(maxAttempts = 20, windowSizeSeconds = 600)  // Payments (20 attempts per 10 min)
```

---

## Workflow Summary

1. ✅ Create **Entity** (database model)
2. ✅ Create **Repository** (data access)
3. ✅ Create **Request DTO** (input validation)
4. ✅ Create **Response DTO** (output format)
5. ✅ Create **Service Interface** (contract)
6. ✅ Create **Service Implementation** (business logic)
7. ✅ Create **Mapper** (DTO ↔ Entity conversion)
8. ✅ Create **Controller** (REST endpoints)
9. ✅ Add **Validation** annotations to DTOs
10. ✅ Add **Rate Limiting** to sensitive endpoints
11. ✅ Test endpoints and error scenarios

---

## Quality Checklist

- [ ] Entity has all required fields and relationships
- [ ] Entity uses appropriate JPA annotations
- [ ] Repository includes custom queries for business needs
- [ ] DTOs have validation annotations with clear messages
- [ ] Service validates all business rules before operations
- [ ] Service throws appropriate exceptions
- [ ] Controller uses correct HTTP methods and status codes
- [ ] Controller has rate limiting on write operations
- [ ] Swagger documentation is added (@Operation, @Tag)
- [ ] Error scenarios are handled gracefully
- [ ] Mapper correctly converts between DTO and Entity
- [ ] Code follows project naming conventions

---

## Code Organization

```
src/main/java/com/JaimeAmuedoJAH/backend/
├── entity/
│   └── FeatureEntity.java
├── repository/
│   └── FeatureRepository.java
├── dto/
│   ├── FeatureRequestDTO.java
│   └── FeatureResponseDTO.java
├── service/
│   ├── FeatureService.java
│   └── impl/
│       └── FeatureServiceImpl.java
├── mapping/
│   └── FeatureMapper.java
├── controller/
│   └── FeatureController.java
├── exceptions/
│   ├── BadRequestException.java
│   └── ResourceNotFoundException.java
└── validation/
    └── [custom validators]
```

---

## Related Documentation

- **API_DOCUMENTATION.md** - Complete endpoint reference
- **EXCEPTION_GUIDE.md** - Exception types and scenarios
- **IMPROVEMENTS_GUIDE.md** - Advanced patterns (validators, rate limiting, auditing)
- **QUICK_REFERENCE.md** - Common code snippets

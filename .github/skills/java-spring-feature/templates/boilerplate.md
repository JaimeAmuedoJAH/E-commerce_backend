# Boilerplate Templates

Copy and customize these templates when implementing a new feature.

## Entity Template

```java
package com.JaimeAmuedoJAH.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

## Repository Template

```java
package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {
    
    List<FeatureEntity> findByName(String name);
}
```

## Request DTO Template

```java
package com.JaimeAmuedoJAH.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
}
```

## Response DTO Template

```java
package com.JaimeAmuedoJAH.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## Service Interface Template

```java
package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.dto.FeatureRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.FeatureResponseDTO;

import java.util.List;

public interface FeatureService {
    
    FeatureResponseDTO create(FeatureRequestDTO request);
    
    FeatureResponseDTO getById(Long id);
    
    List<FeatureResponseDTO> getAll();
    
    FeatureResponseDTO update(Long id, FeatureRequestDTO request);
    
    void delete(Long id);
}
```

## Service Implementation Template

```java
package com.JaimeAmuedoJAH.backend.service.impl;

import com.JaimeAmuedoJAH.backend.dto.FeatureRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.FeatureResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.FeatureEntity;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.mapping.FeatureMapper;
import com.JaimeAmuedoJAH.backend.repository.FeatureRepository;
import com.JaimeAmuedoJAH.backend.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {
    
    private final FeatureRepository repository;
    private final FeatureMapper mapper;
    
    @Override
    public FeatureResponseDTO create(FeatureRequestDTO request) {
        FeatureEntity entity = mapper.toEntity(request);
        FeatureEntity saved = repository.save(entity);
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

## Mapper Template

```java
package com.JaimeAmuedoJAH.backend.mapping;

import com.JaimeAmuedoJAH.backend.dto.FeatureRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.FeatureResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.FeatureEntity;
import org.springframework.stereotype.Component;

@Component
public class FeatureMapper {
    
    public FeatureEntity toEntity(FeatureRequestDTO dto) {
        if (dto == null) return null;
        
        FeatureEntity entity = new FeatureEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        
        return entity;
    }
    
    public FeatureResponseDTO toResponseDTO(FeatureEntity entity) {
        if (entity == null) return null;
        
        FeatureResponseDTO dto = new FeatureResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}
```

## Controller Template

```java
package com.JaimeAmuedoJAH.backend.controller;

import com.JaimeAmuedoJAH.backend.dto.FeatureRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.FeatureResponseDTO;
import com.JaimeAmuedoJAH.backend.ratelimit.RateLimit;
import com.JaimeAmuedoJAH.backend.service.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/features")
@RequiredArgsConstructor
@Tag(name = "Features", description = "Feature Management APIs")
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
    public ResponseEntity<FeatureResponseDTO> getById(
        @PathVariable Long id) {
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
    public ResponseEntity<Void> delete(
        @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

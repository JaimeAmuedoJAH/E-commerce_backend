# Documentación de Excepciones - E-commerce Backend

## Overview
Se ha implementado un sistema jerárquico de excepciones personalizado para manejo consistente de errores en la aplicación e-commerce. Todas las excepciones de negocio extienden de `ApiException` que encapsula el estado HTTP correspondiente.

## Jerarquía de Excepciones

### Clase Base: `ApiException`
```
ApiException (extends RuntimeException)
├── Encapsula HttpStatus
├── Proporciona getStatus() para obtener el código HTTP
└── Se usa en GlobalExceptionHandler
```

**Propiedades:**
- `status: HttpStatus` - Código HTTP asociado
- `message: String` - Mensaje de error

---

## Excepciones Específicas de Dominio

### 1. `BadRequestException` (400 - BAD REQUEST)
**Uso:** Errores de validación de entrada, datos inválidos del cliente.

**Escenarios comunes:**
- Precio negativo o igual a 0
- Stock negativo
- Carrito vacío
- Formato de tarjeta inválido
- Cantidad negativa

**Ejemplo:**
```java
if (producto.getPrecio() <= 0) {
    throw new BadRequestException("El precio debe ser mayor que 0");
}
```

---

### 2. `ResourceNotFoundException` (404 - NOT FOUND)
**Uso:** Recurso no encontrado en la base de datos.

**Escenarios comunes:**
- Usuario no existe
- Producto no existe
- Orden no existe
- Categoría no existe
- Tarjeta no existe

**Ejemplo:**
```java
UsuarioEntity usuario = usuarioRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
```

---

### 3. `OutOfStockException` (400 - BAD REQUEST)
**Uso:** Stock insuficiente para la cantidad solicitada.

**Escenarios comunes:**
- Cantidad solicitada excede el stock disponible
- Producto sin inventario

**Ejemplo:**
```java
if (producto.getStock() < item.getCantidad()) {
    throw new OutOfStockException("Stock insuficiente para " + producto.getNombre());
}
```

---

### 4. `AuthenticationException` (401 - UNAUTHORIZED)
**Uso:** Credenciales inválidas, usuario no autenticado.

**Escenarios comunes:**
- Email no encontrado en login
- Contraseña incorrecta
- Token JWT expirado o inválido

**Ejemplo:**
```java
if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
    throw new AuthenticationException("Credenciales inválidas");
}
```

---

### 5. `ConflictException` (409 - CONFLICT)
**Uso:** Violación de restricción de unicidad o estado incompatible.

**Escenarios comunes:**
- Email ya registrado
- Número de tarjeta duplicado
- Estado de orden no permite la operación

**Ejemplo:**
```java
if (usuarioRepository.existsByEmail(request.getEmail())) {
    throw new ConflictException("Ya existe un usuario con este email");
}
```

---

### 6. `ForbiddenException` (403 - FORBIDDEN)
**Uso:** Usuario autenticado pero sin permisos necesarios.

**Escenarios comunes:**
- Usuario intenta acceder a orden de otro usuario
- Rol insuficiente para operación
- Falta de permisos en recurso

**Disponible para futuras implementaciones de autorización.**

---

### 7. `PaymentException` (402 - PAYMENT REQUIRED)
**Uso:** Errores específicos de procesamiento de pagos.

**Escenarios comunes:**
- Número de tarjeta inválido
- CVV incorrecto
- Fecha de expiración inválida
- Saldo insuficiente

**Ejemplo:**
```java
if (numero.length() != 16) {
    throw new PaymentException("El número de tarjeta debe tener 16 dígitos");
}
```

---

## GlobalExceptionHandler - Manejo Centralizado

### Características
El `@RestControllerAdvice` centraliza el manejo de todas las excepciones:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)           // Maneja todas las excepciones de dominio
    @ExceptionHandler(MethodArgumentNotValidException.class)  // Validación de DTOs
    @ExceptionHandler(ConstraintViolationException.class)     // Validaciones de path/query
    @ExceptionHandler(HttpMessageNotReadableException.class)  // JSON malformado
    @ExceptionHandler(Exception.class)              // Captura genérica
}
```

### Formato de Respuesta de Error
```json
{
    "timestamp": "2026-05-13T10:30:45.123456",
    "status": 400,
    "error": "Bad Request",
    "message": "El precio debe ser mayor que 0",
    "details": [
        {
            "field": "precio",
            "message": "debe ser mayor que 0"
        }
    ]
}
```

---

## Validación en DTOs

Se utilizan anotaciones de Jakarta Validation en los DTOs:

```java
@NotBlank(message = "El email no puede estar vacío")
@Email(message = "El email debe ser válido")
private String email;

@NotNull(message = "El precio no puede ser nulo")
@DecimalMin(value = "0.1")
private BigDecimal precio;

@Size(min = 16, max = 16, message = "El número de tarjeta debe tener 16 dígitos")
private String numeroTarjeta;
```

La validación se activa con `@Valid` en los controladores:
```java
@PostMapping
public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
    @Valid @RequestBody UsuarioRequestDTO usuarioRequest) {
    ...
}
```

---

## Servicios Actualizados

### UsuarioService
- ✅ `AuthenticationException` - Para errores de autenticación
- ✅ `ConflictException` - Para email duplicado
- ✅ `ResourceNotFoundException` - Para usuario no encontrado

### PagoService
- ✅ `PaymentException` - Para errores de validación de tarjeta

### TarjetaService
- ✅ `ConflictException` - Para tarjeta duplicada
- ✅ `ResourceNotFoundException` - Para tarjeta no encontrada

### ProductoService, OrdenService, CarritoService
- ✅ `BadRequestException` - Para validaciones
- ✅ `OutOfStockException` - Para stock insuficiente
- ✅ `ResourceNotFoundException` - Para recursos no encontrados

---

## Códigos HTTP Utilizados

| Excepción | Código | Descripción |
|-----------|--------|-------------|
| BadRequestException | 400 | Solicitud inválida |
| AuthenticationException | 401 | No autenticado |
| ForbiddenException | 403 | Sin permisos |
| ResourceNotFoundException | 404 | Recurso no encontrado |
| ConflictException | 409 | Conflicto de datos |
| PaymentException | 402 | Error en pago |
| OutOfStockException | 400 | Sin inventario |
| Exception (genérica) | 500 | Error interno |

---

## Próximas Mejoras Sugeridas

1. **Auditoría de errores** - Logging en base de datos de errores críticos
2. **Códigos de error personalizados** - Añadir `errorCode` en respuestas
3. **Internacionalización** - Mensajes de error multiidioma
4. **Rate Limiting** - Proteción contra ataques de fuerza bruta
5. **Rastreo de transacciones** - `requestId` único en cada solicitud
6. **Documentación Swagger** - Anotaciones de excepciones en endpoints

---

## Testing de Excepciones

Para validar las excepciones, se puede crear tests:

```java
@Test
void testRegistrarUsuarioDuplicado() {
    UsuarioRequestDTO dto = new UsuarioRequestDTO(...);
    
    assertThrows(ConflictException.class, () -> {
        usuarioService.crearUsuario(dto);
    });
}
```

---

**Última actualización:** 13 de mayo de 2026  
**Estado:** Compilación exitosa ✅

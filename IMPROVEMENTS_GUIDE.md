# Mejoras de Seguridad y Validación - E-commerce Backend

Documentación de las tres mejoras principales implementadas en la aplicación.

---

## 1. Validaciones Personalizadas (@Custom Validators)

### Descripción
Sistema de validaciones personalizadas usando Jakarta Validation API para reglas complejas de negocio que van más allá de las anotaciones estándar.

### Validadores Implementados

#### `@ValidCardNumber`
**Ubicación:** `validation/ValidCardNumber.java`  
**Propósito:** Validar números de tarjeta de crédito

**Características:**
- Verifica que tenga exactamente 16 dígitos
- Implementa el Algoritmo de Luhn para validación criptográfica
- Acepta espacios y guiones que automáticamente se eliminan
- Evita números de tarjeta inválidos desde el nivel de DTO

**Uso:**
```java
@NotBlank
@ValidCardNumber
private String numeroTarjeta;
```

**Ejemplo de validación:**
- ✅ `4532 1234 5678 9010` - Válida (Luhn check pass)
- ❌ `4532 1234 5678 9011` - Inválida (Luhn check fail)
- ❌ `4532123456789` - Inválida (menos de 16 dígitos)

---

#### `@ValidPrice`
**Ubicación:** `validation/ValidPrice.java`  
**Propósito:** Validar precios de productos

**Características:**
- Verifica que el precio sea mayor que 0
- Usa BigDecimal para precisión en operaciones monetarias
- Compatible con tipos numéricos (Double, BigDecimal, Integer, etc.)

**Uso:**
```java
@NotNull
@ValidPrice
private Double precio;
```

**Ejemplo de validación:**
- ✅ `19.99` - Válida
- ❌ `0` - Inválida
- ❌ `-5.50` - Inválida

---

#### `@ValidStock`
**Ubicación:** `validation/ValidStock.java`  
**Propósito:** Validar niveles de inventario

**Características:**
- Verifica que el stock no sea negativo
- Solo acepta valores >= 0
- Ideal para validación en DTOs de actualización

**Uso:**
```java
@NotNull
@ValidStock
private Integer stock;
```

**Ejemplo de validación:**
- ✅ `0` - Válida
- ✅ `100` - Válida
- ❌ `-1` - Inválida

---

#### `@ValidExpirationDate`
**Ubicación:** `validation/ValidExpirationDate.java`  
**Propósito:** Validar fecha de expiración de tarjeta

**Características:**
- Formato obligatorio: `MM/AA` (ej: `12/25`)
- Verifica que la fecha sea futura o actual
- Usa `YearMonth` de Java 8+ para precisión

**Uso:**
```java
@NotBlank
@ValidExpirationDate
private String fechaExpiracion;
```

**Ejemplo de validación:**
- ✅ `12/25` - Válida (futura)
- ❌ `12/23` - Inválida (pasada)
- ❌ `13/25` - Inválida (mes inválido)
- ❌ `12-25` - Inválida (formato incorrecto)

---

### DTOs Actualizados

#### ProductoRequestDTO
```java
@ValidPrice              // En lugar de @Positive
@ValidStock              // En lugar de @PositiveOrZero
```

#### TarjetaRequestDTO
```java
@ValidCardNumber         // En lugar de @Size(16)
@ValidExpirationDate     // Validación nueva
```

---

## 2. Interceptor de Errores (Error Tracking & Auditoría)

### Descripción
Sistema centralizado de registro de errores en base de datos para auditoría, análisis y debugging.

### Componentes

#### `ErrorLogEntity`
**Ubicación:** `entity/ErrorLogEntity.java`  
**Tabla:** `error_logs`

**Campos Registrados:**
- `timestamp` - Fecha y hora exacta del error
- `httpStatus` - Código HTTP (400, 401, 500, etc.)
- `errorType` - Clase de la excepción (`ConflictException`, etc.)
- `message` - Mensaje de error
- `endpoint` - URI del endpoint afectado
- `httpMethod` - GET, POST, PUT, DELETE
- `userAgent` - Cliente HTTP (navegador, app, etc.)
- `ipAddress` - IP del cliente (soporta proxies)
- `requestId` - ID único para rastrear la solicitud
- `stackTrace` - Traza completa para debugging
- `userEmail` - Email del usuario autenticado (o "ANONYMOUS")
- `severity` - LOW (4xx), MEDIUM (conflictos), HIGH (5xx)

**Ejemplo de registro:**
```json
{
    "id": 123,
    "timestamp": "2026-05-13T15:30:45",
    "httpStatus": 401,
    "errorType": "AuthenticationException",
    "message": "Credenciales inválidas",
    "endpoint": "/usuarios/login",
    "httpMethod": "POST",
    "ipAddress": "192.168.1.100",
    "userEmail": "ANONYMOUS",
    "severity": "MEDIUM"
}
```

---

#### `ErrorTrackingService`
**Ubicación:** `service/ErrorTrackingService.java`

**Métodos Principales:**

```java
// Registrar un error
ErrorLogEntity trackError(
    HttpStatus status,
    String errorType,
    String message,
    HttpServletRequest request,
    String userEmail,
    Throwable throwable
)

// Obtener errores recientes
List<ErrorLogEntity> getRecentErrors(int limit)

// Obtener errores por usuario
List<ErrorLogEntity> getErrorsByUser(String userEmail)

// Obtener errores críticos (5xx)
List<ErrorLogEntity> getCriticalErrors()
```

**Características:**
- Determina automáticamente la severidad (LOW/MEDIUM/HIGH)
- Extrae IP real incluso detrás de proxies (X-Forwarded-For)
- Genera requestId único para correlacionar logs
- Convierte stack traces a string para almacenamiento

---

#### `GlobalExceptionHandler` Mejorado
**Cambios:**
- Integración automática con `ErrorTrackingService`
- Cada excepción registrada en BD para auditoría
- Obtiene usuario actual del `SecurityContext`
- Funciona con inyección opcional (no rompe si service no está disponible)

**Flujo:**
```
Excepción ocurre
    ↓
GlobalExceptionHandler.handleXXX()
    ↓
ErrorTrackingService.trackError() [Async, no bloquea respuesta]
    ↓
Respuesta de error enviada al cliente
    ↓
Registro guardado en BD para análisis posterior
```

---

#### Casos de Uso

**Debugging:** Revisar stack traces de errores 500
```sql
SELECT stackTrace FROM error_logs 
WHERE httpStatus = 500 
ORDER BY timestamp DESC LIMIT 1;
```

**Análisis de seguridad:** Intentos de acceso fallidos
```sql
SELECT ipAddress, COUNT(*) as intentos 
FROM error_logs 
WHERE errorType = 'AuthenticationException' 
GROUP BY ipAddress 
HAVING COUNT(*) > 5;
```

**Monitoreo:** Errores críticos recientes
```sql
SELECT * FROM error_logs 
WHERE severity = 'HIGH' 
ORDER BY timestamp DESC 
LIMIT 10;
```

---

## 3. Rate Limiting (Protección contra Fuerza Bruta)

### Descripción
Sistema de limitación de velocidad para proteger endpoints sensibles contra ataques de fuerza bruta.

### Componentes

#### `@RateLimit` Anotador
**Ubicación:** `ratelimit/RateLimit.java`

```java
@RateLimit(
    maxAttempts = 5,      // Máximo número de intentos
    windowSizeSeconds = 60 // Ventana de tiempo en segundos
)
```

**Uso en Controladores:**
```java
@PostMapping("/login")
@RateLimit(maxAttempts = 10, windowSizeSeconds = 300)
public ResponseEntity<UsuarioLoginResponseDTO> login(...) {
    ...
}
```

---

#### `RateLimitService`
**Ubicación:** `ratelimit/RateLimitService.java`

**Métodos:**
```java
// Verificar si la solicitud debe permitirse
boolean allowRequest(String key, int maxAttempts, int windowSizeSeconds)

// Obtener contador de intentos
int getAttemptCount(String key)

// Resetear contador
void resetCounter(String key)

// Limpiar entradas antiguas (prevenir memory leaks)
void cleanup()
```

**Algoritmo:**
- Usa `ConcurrentHashMap` para rastrear intentos por IP
- Ventana de tiempo deslizante
- Auto-limpia entradas antiguas cada hora
- Thread-safe para entornos multi-hilo

**Ejemplo:**
```
Cliente A (IP: 192.168.1.1)
├─ Intento 1: ✅ Permitido (1/10)
├─ Intento 2: ✅ Permitido (2/10)
├─ Intento 3: ✅ Permitido (3/10)
├─ Intento 4: ✅ Permitido (4/10)
├─ Intento 5: ✅ Permitido (5/10)
├─ Intento 6: ✅ Permitido (6/10)
├─ Intento 7: ✅ Permitido (7/10)
├─ Intento 8: ✅ Permitido (8/10)
├─ Intento 9: ✅ Permitido (9/10)
├─ Intento 10: ✅ Permitido (10/10)
└─ Intento 11: ❌ BLOQUEADO (429 - Too Many Requests)
    Esperar 5 minutos para intentar de nuevo
```

---

#### `RateLimitInterceptor`
**Ubicación:** `ratelimit/RateLimitInterceptor.java`

**Funcionalidad:**
- Interceptor que se ejecuta antes de cada request
- Verifica si el endpoint tiene `@RateLimit`
- Extrae IP del cliente (soporta proxies)
- Delega al `RateLimitService` para verificar límite
- Lanza `TooManyRequestsException` si se excede

**Flujo:**
```
Request HTTP
    ↓
RateLimitInterceptor.preHandle()
    ↓
¿Tiene @RateLimit?
├─ NO: Continuar normalmente
└─ SÍ: Llamar RateLimitService.allowRequest()
         ├─ Permitido: Continuar
         └─ Denegado: Lanzar TooManyRequestsException
```

---

#### `RateLimitConfig`
**Ubicación:** `ratelimit/RateLimitConfig.java`

**Configuración:**
- Registra `RateLimitInterceptor` en la cadena de interceptores
- Excluye endpoints públicos (Swagger)
- Tarea programada (@Scheduled) que ejecuta `cleanup()` cada hora

---

#### `TooManyRequestsException`
**Ubicación:** `exceptions/TooManyRequestsException.java`

**HTTP Status:** `429 Too Many Requests`

**Respuesta de error:**
```json
{
    "timestamp": "2026-05-13T15:30:45",
    "status": 429,
    "message": "Too many requests. Maximum 10 attempts per 300 seconds.",
    "error": "Too Many Requests"
}
```

---

### Endpoints Protegidos

#### `/usuarios/login`
- **Límite:** 10 intentos por 5 minutos (300 segundos)
- **Razón:** Proteger contra ataques de fuerza bruta en contraseñas
- **Excepción:** `TooManyRequestsException`

#### `/usuarios/register`
- **Límite:** 5 intentos por 5 minutos (300 segundos)
- **Razón:** Prevenir creación automática masiva de cuentas
- **Excepción:** `TooManyRequestsException`

#### `/pagos/procesar`
- **Límite:** 20 intentos por 10 minutos (600 segundos)
- **Razón:** Evitar múltiples transacciones fraudulentas
- **Excepción:** `TooManyRequestsException`

---

### Identificación de Cliente

**Orden de precedencia:**
1. Header `X-Forwarded-For` (si está detrás de proxy)
2. IP directa del cliente (`RemoteAddr`)

**Ejemplo con proxy:**
```
Request Headers:
X-Forwarded-For: 203.0.113.45, 198.51.100.20
Remote Address: 10.0.0.1

Identificador usado: 203.0.113.45 (IP real del cliente)
```

---

## Flujo Completo de Manejo de Errores

```
Usuario realiza solicitud HTTP
    ↓
RateLimitInterceptor
├─ ¿Rate limit excedido?
│  └─ SÍ: Lanzar TooManyRequestsException (429)
└─ NO: Continuar

Controlador -> Servicio -> Lógica de Negocio
    ↓
¿Ocurre Excepción?
├─ NO: Respuesta exitosa (200, 201, etc.)
└─ SÍ: GlobalExceptionHandler.handleXXX()
         ├─ ErrorTrackingService.trackError() [Async]
         │  └─ Guardar en BD para auditoría
         └─ Retornar respuesta de error JSON
              (400, 401, 404, 409, 500, etc.)
```

---

## Seguridad y Mejores Prácticas

### Validaciones
✅ Previenen datos inválidos en nivel de DTO  
✅ Mensajes de error claros para el cliente  
✅ Rechazo temprano (fail-fast)  
✅ Evitan procesamiento innecesario  

### Auditoría
✅ Rastreo completo de errores en BD  
✅ Identificación de usuarios (email)  
✅ Detección de patrones sospechosos  
✅ Fácil debugging con stack traces  

### Rate Limiting
✅ Protección contra fuerza bruta  
✅ Identificación por IP (soporta proxies)  
✅ Ventanas de tiempo configurables  
✅ Auto-limpieza para evitar memory leaks  
✅ No bloquea requests legítimos  

---

## Ejemplo: Ataque Bloqueado

```
Attacker: 200.10.20.30

Intento 1 (14:30:00): POST /usuarios/login → 200 OK
  └─ Usuario no existe, pero intenta

Intento 2 (14:30:05): POST /usuarios/login → 401 Unauthorized
  └─ Credenciales inválidas

Intento 3 (14:30:10): POST /usuarios/login → 401 Unauthorized
  └─ Credenciales inválidas

...

Intento 10 (14:35:00): POST /usuarios/login → 401 Unauthorized
  └─ Credenciales inválidas

Intento 11 (14:35:02): POST /usuarios/login → 429 Too Many Requests ✅ BLOQUEADO
  └─ "Too many requests. Maximum 10 attempts per 300 seconds."

[En BD: Error registrado como MEDIUM severity, IP bloqueada]
```

---

## Compilación y Testing

✅ **Compilación exitosa:** `mvnw -DskipTests compile`

**Próximos pasos sugeridos:**
- [ ] Crear tests unitarios para validadores
- [ ] Crear tests de integración para rate limiting
- [ ] Implementar métricas de monitoreo (Actuator)
- [ ] Considerar usar Redis para rate limiting distribuido
- [ ] Documentación en Swagger para excepciones

---

**Fecha:** 13 de mayo de 2026  
**Estado:** ✅ Implementado y compilado exitosamente  
**Próxima versión:** Considerar Redis para escalabilidad horizontal

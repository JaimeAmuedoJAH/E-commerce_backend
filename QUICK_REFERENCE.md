# Quick Reference Guide - Validaciones, Auditoría y Rate Limiting

## 🚀 Uso Rápido

### 1️⃣ Usar Validadores Personalizados

**Agregar a un DTO:**
```java
public class MiDTO {
    @ValidCardNumber
    private String numeroTarjeta;
    
    @ValidPrice
    private Double precio;
    
    @ValidStock
    private Integer stock;
    
    @ValidExpirationDate
    private String fechaExpiracion;
}
```

**Mensajes personalizados:**
```java
@ValidCardNumber(message = "Tarjeta inválida")
@ValidPrice(message = "Precio debe ser > 0")
```

---

### 2️⃣ Proteger Endpoints con Rate Limiting

**Agregar a un controlador:**
```java
@PostMapping("/endpoint-sensible")
@RateLimit(maxAttempts = 5, windowSizeSeconds = 300)
public ResponseEntity<?> endpointSensible(...) {
    // 5 intentos máximo cada 5 minutos
}
```

**Presets comunes:**
```java
// Para login
@RateLimit(maxAttempts = 10, windowSizeSeconds = 300)

// Para registro
@RateLimit(maxAttempts = 5, windowSizeSeconds = 300)

// Para pagos
@RateLimit(maxAttempts = 20, windowSizeSeconds = 600)

// Para operaciones normales
@RateLimit(maxAttempts = 100, windowSizeSeconds = 60)
```

---

### 3️⃣ Auditar Errores (Automático)

**No requiere código adicional.** Los errores se registran automáticamente en `error_logs` tabla.

**Consultar errores por usuario:**
```java
@Autowired
private ErrorTrackingService errorTracking;

// En un endpoint administrativo
List<ErrorLogEntity> usuarioErrors = 
    errorTracking.getErrorsByUser("usuario@email.com");
```

**Obtener errores críticos:**
```java
List<ErrorLogEntity> criticalErrors = 
    errorTracking.getCriticalErrors();
```

---

## 📋 Validadores Disponibles

| Anotador | Uso | Formato | Ejemplo |
|----------|-----|---------|---------|
| `@ValidCardNumber` | Tarjetas crédito | 16 dígitos (Luhn) | `4532123456789010` |
| `@ValidPrice` | Precios | Número > 0 | `19.99` |
| `@ValidStock` | Inventario | Número >= 0 | `100` |
| `@ValidExpirationDate` | Fecha tarjeta | MM/AA futuro | `12/25` |

---

## 🔥 Rate Limiting - Valores Recomendados

```
Login:              10 attempts / 5 minutos
Registro:           5 attempts / 5 minutos
Pago:               20 attempts / 10 minutos
Crear producto:     30 attempts / 1 minuto
Búsqueda:           100 attempts / 1 minuto
Operaciones normales: sin límite
```

---

## 📊 Códigos de Error

```
400 → Validación fallida
401 → Credenciales inválidas
402 → Error de pago
404 → No encontrado
409 → Conflicto/duplicado
429 → Límite de velocidad excedido ← NUEVO
500 → Error interno
```

---

## 🔍 Debugging

**Ver si rate limit está activo:**
```
Si recibes 429 → Rate limit excedido
Si recibes 400 → Validación fallida en DTO
```

**Consultar intentos fallidos:**
```sql
-- Top 10 IPs con más errores
SELECT ipAddress, COUNT(*) as count 
FROM error_logs 
GROUP BY ipAddress 
ORDER BY count DESC 
LIMIT 10;

-- Errores en última hora
SELECT * FROM error_logs 
WHERE timestamp > NOW() - INTERVAL 1 HOUR 
ORDER BY timestamp DESC;
```

---

## ✅ Checklist para Nuevo Endpoint

- [ ] ¿Necesita validación personalizada? → Agregar anotadores en DTO
- [ ] ¿Es endpoint sensible? → Agregar `@RateLimit`
- [ ] ¿Usa excepciones apropiadas? → BadRequest, Conflict, Payment, etc.
- [ ] ¿Compilar sin errores? → `mvnw -DskipTests compile`
- [ ] ¿Probar error flow? → Verificar respuesta JSON

---

## 🚨 Respuestas de Error

**Validación (400):**
```json
{
    "timestamp": "2026-05-13T15:30:45",
    "status": 400,
    "message": "Validation failed",
    "details": [
        {"field": "precio", "message": "El precio debe ser mayor que 0"}
    ]
}
```

**Rate Limit (429):**
```json
{
    "timestamp": "2026-05-13T15:30:45",
    "status": 429,
    "message": "Too many requests. Maximum 10 attempts per 300 seconds."
}
```

**Autenticación (401):**
```json
{
    "timestamp": "2026-05-13T15:30:45",
    "status": 401,
    "message": "Credenciales inválidas"
}
```

---

## 💡 Tips

1. **Para múltiples validaciones:** Combinar anotadores estándar + personalizados
```java
@NotBlank
@Size(min = 3)
@ValidCardNumber  // Personalizado
private String numero;
```

2. **Para endpoints críticos:** Usar múltiples capas
```java
@PostMapping("/critical")
@RateLimit(maxAttempts = 3, windowSizeSeconds = 60)
@PreAuthorize("hasRole('ADMIN')")  // Seguridad adicional
public ResponseEntity<?> criticalEndpoint(...) { }
```

3. **Monitorear rate limits:** Revisar `error_logs` frecuentemente
```sql
SELECT * FROM error_logs 
WHERE errorType = 'TooManyRequestsException' 
ORDER BY timestamp DESC;
```

---

## 📚 Enlaces a Documentación Completa

- `EXCEPTION_GUIDE.md` - Excepciones
- `IMPROVEMENTS_GUIDE.md` - Validaciones, Auditoría, Rate Limiting
- `IMPLEMENTATION_SUMMARY.md` - Resumen técnico

---

**¡Listo para usar! 🚀**

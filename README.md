# 🛒 E-commerce Backend

Backend completo para una plataforma de e-commerce desarrollado con **Spring Boot**, **Java 21** y autenticación **JWT** con refresh tokens.

---

## 🚀 Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Spring Security | - |
| Spring Data JPA | - |
| H2 Database (file mode) | - |
| JWT (io.jsonwebtoken) | - |
| Lombok | - |
| SpringDoc OpenAPI (Swagger) | - |

---

## 📐 Arquitectura

El proyecto sigue una arquitectura en capas estándar:

```
Controller → Service → Repository → Entity
```

Cada módulo incluye:
- **Entity** → Entidad JPA mapeada a la base de datos
- **Repository** → Interfaz de acceso a datos (Spring Data JPA)
- **Service** → Lógica de negocio
- **Controller** → Exposición de endpoints REST
- **DTOs** → Objetos de transferencia de datos (Request / Response)
- **Mapping** → Conversión entre entidades y DTOs

---

## 🔐 Seguridad

### Autenticación y autorización
- **JWT** con expiración de 15 minutos
- **Refresh token** para renovación automática de sesión (7 días)
- **Roles** — `ROLE_USER` y `ROLE_ADMIN` protegidos con `@PreAuthorize`
- **UsuarioPrincipal** personalizado con `publicId` accesible desde el contexto de seguridad

### Protección de datos
- **publicId (UUID)** en usuarios — nunca se expone el ID interno de la base de datos
- **Encriptación AES-256** para números de tarjeta
- **Hash SHA-256** para búsqueda eficiente de tarjetas
- **BCrypt** para contraseñas y CVV
- **Verificación de contraseña actual** antes de permitir cambio de contraseña
- **Rate limiting** en endpoints críticos (login, registro, pagos)

### Imágenes
- Imágenes de perfil y productos almacenadas como **Base64** en base de datos
- Columnas de tipo `TEXT/CLOB` para soportar el tamaño

---

## 📦 Módulos

### 👤 Usuarios
Registro, login, actualización de perfil (nombre, email, contraseña, imagen) y eliminación. Contraseña verificada antes de cambio.

### 🔑 Auth
Endpoints para refresh token y logout con invalidación del token en base de datos.

### 🏷️ Categorías
Listado ligero sin productos para carga rápida. Detalle con productos al acceder a una categoría concreta.

### 📦 Productos
Gestión de productos con imagen en Base64, paginación y filtrado por categoría.

### 🛒 Carrito
Gestión del carrito de compra con items y cantidades, asociado al `publicId` del cliente.

### 💳 Tarjetas
Gestión de tarjetas de pago con número encriptado y CVV hasheado. Una tarjeta puede estar asociada a múltiples usuarios.

### 💰 Pagos
Pasarela de pago ficticia con validación de saldo, CVV y fecha de expiración. Reembolso automático al cancelar una orden.

### 📋 Órdenes
Gestión de órdenes con estados (`PENDIENTE`, `CONFIRMADA`, `ENVIADA`, `ENTREGADA`, `CANCELADA`), cancelación con período de 15 días y reembolso automático.

---

## 🔧 Configuración

### application.properties

```properties
# Servidor
server.port=8110
server.servlet.context-path=/api

# JWT
jwt.secret=<clave-secreta-minimo-32-caracteres>
jwt.expiration-ms=900000
jwt.refresh-expiration-ms=604800000

# Encriptación
encryption.key=<clave-AES-256-en-base64>

# Base de datos H2 (file mode - persiste entre reinicios)
spring.datasource.url=jdbc:h2:file:./data/ecommerce;MODE=MySQL;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# Logging
logging.level.com.JaimeAmuedoJAH.backend=INFO
```

---

## 🏃 Ejecutar la aplicación

```bash
# Con Maven
mvn clean spring-boot:run

# O desde el IDE
# Click derecho → Run As → Spring Boot App
```

La API estará disponible en: `http://localhost:8110/api`

---

## 📚 Documentación interactiva

Swagger UI:
```
http://localhost:8110/swagger-ui.html
```

Consola H2:
```
http://localhost:8110/h2-console
```
- URL: `jdbc:h2:file:./data/ecommerce`
- Usuario: `sa`
- Contraseña: (vacía)

---

## 🔐 Autenticación

### Registro
```http
POST /api/usuarios/register
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "rol": "ROLE_USER"
}
```

### Login
```http
POST /api/usuarios/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "usuario": {
    "publicId": "a1b2c3d4-...",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "ROLE_USER",
    "imagenPerfil": null
  }
}
```

### Refresh token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Logout
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

### Usar el token
```
Authorization: Bearer <token>
```

---

## 📋 Endpoints

### Usuarios
| Método | Endpoint | Descripción | Auth | Rol |
|---|---|---|---|---|
| POST | `/usuarios/register` | Registrar usuario | ❌ | - |
| POST | `/usuarios/login` | Login | ❌ | - |
| GET | `/usuarios/all` | Listar usuarios | ✅ | ADMIN |
| GET | `/usuarios/{publicId}` | Obtener usuario | ✅ | ADMIN/propio |
| PUT | `/usuarios/update/{publicId}` | Actualizar usuario | ✅ | ADMIN/propio |
| DELETE | `/usuarios/delete/{publicId}` | Eliminar usuario | ✅ | ADMIN |

### Auth
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/refresh` | Renovar access token | ❌ |
| POST | `/auth/logout` | Cerrar sesión | ✅ |

### Categorías
| Método | Endpoint | Descripción | Auth | Rol |
|---|---|---|---|---|
| GET | `/categorias/all` | Listar categorías (sin productos) | ❌ | - |
| GET | `/categorias/{id}` | Categoría con productos | ❌ | - |
| POST | `/categorias/add` | Crear categoría | ✅ | ADMIN |
| PUT | `/categorias/update/{id}` | Actualizar categoría | ✅ | ADMIN |
| DELETE | `/categorias/delete/{id}` | Eliminar categoría | ✅ | ADMIN |

### Productos
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/productos/all` | Listar productos paginados | ❌ |
| GET | `/productos/{id}` | Obtener producto | ❌ |
| GET | `/productos/categoria/{categoriaId}` | Productos por categoría | ❌ |
| POST | `/productos/add` | Crear producto | ✅ ADMIN |
| PUT | `/productos/update/{id}` | Actualizar producto | ✅ ADMIN |
| DELETE | `/productos/delete/{id}` | Eliminar producto | ✅ ADMIN |

### Carrito
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/carritos/all` | Listar carritos | ✅ ADMIN |
| GET | `/carritos/{id}` | Obtener carrito | ✅ |
| GET | `/carritos/cliente/{clientePublicId}` | Carrito por cliente | ✅ |
| POST | `/carritos/add` | Crear carrito | ✅ |
| PUT | `/carritos/update/{id}` | Actualizar carrito | ✅ |
| DELETE | `/carritos/delete/{id}` | Eliminar carrito | ✅ |

### Tarjetas
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/tarjetas/cliente/{clientePublicId}` | Tarjetas por cliente | ✅ |
| GET | `/tarjetas/{id}` | Obtener tarjeta | ✅ |
| POST | `/tarjetas/add` | Añadir tarjeta | ✅ |
| DELETE | `/tarjetas/delete/{id}` | Eliminar tarjeta | ✅ ADMIN |

### Pagos
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/pagos/procesar` | Procesar pago | ✅ |

### Órdenes
| Método | Endpoint | Descripción | Auth | Rol |
|---|---|---|---|---|
| GET | `/ordenes/all` | Listar órdenes | ✅ | ADMIN |
| GET | `/ordenes/{id}` | Obtener orden | ✅ | - |
| GET | `/ordenes/cliente/{clientePublicId}` | Órdenes por cliente | ✅ | - |
| POST | `/ordenes/add` | Crear orden | ✅ | - |
| PUT | `/ordenes/{id}/estado` | Cambiar estado | ✅ | ADMIN |
| PUT | `/ordenes/{id}/cancelar` | Cancelar orden | ✅ | - |
| DELETE | `/ordenes/delete/{id}` | Eliminar orden | ✅ | ADMIN |

---

## 💳 Pasarela de Pago

La pasarela es ficticia pero funcional. Valida:
- Formato del número de tarjeta (16 dígitos)
- Formato del CVV (3-4 dígitos)
- Formato de fecha de expiración (MM/AA)
- CVV correcto mediante comparación de hash BCrypt
- Fecha de expiración correcta
- Saldo suficiente en la tarjeta
- Reembolso automático al cancelar una orden dentro de los 15 días

```http
POST /api/pagos/procesar
Authorization: Bearer <token>
Content-Type: application/json

{
  "carritoId": 1,
  "clientePublicId": "a1b2c3d4-...",
  "numeroTarjeta": "1234567890123456",
  "fechaExpiracion": "12/27",
  "cvv": "123",
  "titular": "Juan Pérez",
  "monto": 49.99
}
```

**Respuesta exitosa:**
```json
{
  "exitoso": true,
  "mensaje": "Pago procesado correctamente.",
  "codigoTransaccion": "382A7DF9-CBDE-4841-AF73-37A506DE2A82",
  "carritoId": 1,
  "clientePublicId": "a1b2c3d4-..."
}
```

---

## 🚨 Manejo de errores

```json
{
  "timestamp": "2026-05-05T10:30:45.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Producto not found with id 999"
}
```

| Código | Significado |
|---|---|
| 200 | Solicitud exitosa |
| 201 | Recurso creado |
| 400 | Validación fallida |
| 401 | Token inválido, expirado o contraseña incorrecta |
| 403 | Sin permisos suficientes |
| 404 | Recurso no encontrado |
| 409 | Conflicto (email duplicado, etc.) |
| 500 | Error del servidor |

---

## 🧪 Tests

```bash
# Ejecutar todos los tests
mvn test

# Test específico
mvn test -Dtest=UsuarioServiceTest

# Con cobertura de código
mvn test jacoco:report
```

---

## 🔄 CORS

Orígenes permitidos para desarrollo:
- `http://localhost:3000`
- `http://localhost:5173` (Vite)
- `http://localhost:8080`

---

## 📝 Notas para producción

1. Cambiar `jwt.secret` a una clave segura aleatoria
2. Mover todas las claves a variables de entorno
3. Sustituir H2 por PostgreSQL o MySQL
4. Actualizar orígenes CORS
5. Configurar HTTPS
6. Implementar pasarela de pago real (Stripe, PayPal, etc.)
7. Ajustar niveles de logging

---

## ❌ Posibles errores

- Algunos nombres de archivos pueden dar error si tienen la primera letra en minúscula. Verificar que todos los archivos Java siguen PascalCase (ej. `UsuarioEntity.java` no `usuarioEntity.java`)

---

**Versión**: 1.1.0  
**Última actualización**: Mayo 2026  
**Autor**: JaimeAmuedoJAH
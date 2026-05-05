# 🛒 E-commerce Backend

Backend completo para una plataforma de e-commerce desarrollado con **Spring Boot**, **Java 21** y autenticación **JWT**.

---

## 🚀 Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Spring Security | - |
| Spring Data JPA | - |
| H2 Database | - |
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

## 📦 Módulos

### 👤 Usuarios
Gestión de usuarios con registro, login y autenticación JWT.

### 🏷️ Categorías
Gestión de categorías de productos.

### 📦 Productos
Gestión de productos asociados a categorías.

### 🛒 Carrito
Gestión del carrito de compra con items y cantidades.

### 💳 Tarjetas
Gestión de tarjetas de pago asociadas a usuarios con saldo real.

### 💰 Pagos
Pasarela de pago ficticia con validación de saldo, CVV y fecha de expiración.

### 📋 Órdenes
Gestión de órdenes de compra con código de transacción.

---

## 🔧 Configuración

### application.properties

```properties
# Servidor
server.port=8110
server.servlet.context-path=/api

# JWT
jwt.secret=<tu-clave-secreta-minimo-32-caracteres>
jwt.expiration-ms=86400000

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:testdb
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

Swagger UI disponible en:
```
http://localhost:8110/swagger-ui.html
```

Consola H2 disponible en:
```
http://localhost:8110/h2-console
```
- URL: `jdbc:h2:mem:testdb`
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
  "usuario": {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "ROLE_USER"
  }
}
```

### Usar el token
Añadir en cada petición protegida:
```
Authorization: Bearer <token>
```

---

## 📋 Endpoints

### Usuarios
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/usuarios/register` | Registrar usuario | ❌ |
| POST | `/usuarios/login` | Login | ❌ |
| GET | `/usuarios/all` | Listar usuarios | ✅ |
| GET | `/usuarios/{id}` | Obtener usuario | ✅ |
| PUT | `/usuarios/update/{id}` | Actualizar usuario | ✅ |
| DELETE | `/usuarios/delete/{id}` | Eliminar usuario | ✅ |

### Categorías
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/categorias/all` | Listar categorías con productos | ✅ |
| GET | `/categorias/{id}` | Obtener categoría | ✅ |
| POST | `/categorias/add` | Crear categoría | ✅ |
| PUT | `/categorias/update/{id}` | Actualizar categoría | ✅ |
| DELETE | `/categorias/delete/{id}` | Eliminar categoría | ✅ |

### Productos
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/productos/all` | Listar todos los productos | ✅ |
| GET | `/productos/{id}` | Obtener producto | ✅ |
| GET | `/productos/categoria/{categoriaId}` | Productos por categoría | ✅ |
| POST | `/productos/add` | Crear producto | ✅ |
| PUT | `/productos/update/{id}` | Actualizar producto | ✅ |
| DELETE | `/productos/delete/{id}` | Eliminar producto | ✅ |

### Carrito
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/carritos/all` | Listar carritos | ✅ |
| GET | `/carritos/{id}` | Obtener carrito | ✅ |
| GET | `/carritos/cliente/{clienteId}` | Carrito por cliente | ✅ |
| POST | `/carritos/add` | Crear carrito | ✅ |
| PUT | `/carritos/update/{id}` | Actualizar carrito | ✅ |
| DELETE | `/carritos/delete/{id}` | Eliminar carrito | ✅ |

### Tarjetas
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/tarjetas/cliente/{clienteId}` | Tarjetas por cliente | ✅ |
| GET | `/tarjetas/{id}` | Obtener tarjeta | ✅ |
| POST | `/tarjetas/add` | Añadir tarjeta | ✅ |
| DELETE | `/tarjetas/delete/{id}` | Eliminar tarjeta | ✅ |

### Pagos
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/pagos/procesar` | Procesar pago | ✅ |

### Órdenes
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/ordenes/all` | Listar órdenes | ✅ |
| GET | `/ordenes/{id}` | Obtener orden | ✅ |
| GET | `/ordenes/cliente/{clienteId}` | Órdenes por cliente | ✅ |
| POST | `/ordenes/add` | Crear orden | ✅ |
| PUT | `/ordenes/update/{id}` | Actualizar orden | ✅ |
| DELETE | `/ordenes/delete/{id}` | Eliminar orden | ✅ |

---

## 💳 Pasarela de Pago

La pasarela es ficticia pero funcional. Valida:
- Formato del número de tarjeta (16 dígitos)
- Formato del CVV (3-4 dígitos)
- Formato de fecha de expiración (MM/AA)
- CVV correcto
- Fecha de expiración correcta
- Saldo suficiente en la tarjeta

**Procesar pago:**
```http
POST /api/pagos/procesar
Authorization: Bearer <token>
Content-Type: application/json

{
  "carritoId": 1,
  "clienteId": 1,
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
  "clienteId": 1
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
| 401 | Token inválido o expirado |
| 403 | Sin permisos |
| 404 | Recurso no encontrado |
| 500 | Error del servidor |

---

## 🧪 Tests

```bash
# Ejecutar todos los tests
mvn test

# Tests específicos
mvn test -Dtest=ProductoServiceTest
mvn test -Dtest=UsuarioServiceTest

# Con cobertura de código
mvn test jacoco:report
```

---

## 🔄 CORS

Orígenes permitidos configurados para desarrollo:
- `http://localhost:3000`
- `http://localhost:5173` (Vite)
- `http://localhost:8080`

---

## 📝 Notas para producción

1. Cambiar `jwt.secret` a una clave segura de al menos 32 caracteres
2. Sustituir H2 por PostgreSQL o MySQL
3. Actualizar los orígenes CORS permitidos
4. Ajustar niveles de logging
5. Implementar una pasarela de pago real (Stripe, PayPal, etc.)

---

## ❌ Posibles Errores

1. Algunos nombres de archivos pueden dar error al estar nombrados con la primera letra minúscula. Si la aplicación no ejecuta correctamente comprobar archivos de las carpetas y comprobarlo. (Ejemplo: Dentro de la carpeta usuario existe el archivo usuarioEntity.java, este nombre debe ser modificado por UsuarioEntity.java; usuario => Usuario)

---

**Versión**: 1.0  
**Última actualización**: Mayo 2026  
**Autor**: JaimeAmuedoJAH

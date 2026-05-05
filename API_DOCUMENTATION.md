# E-commerce Backend API

## 📋 Descripción
Backend completo para un sistema de e-commerce desarrollado con Spring Boot 4.0.5, Java 21 y autenticación JWT.

## 🚀 Características Implementadas

### Seguridad
- ✅ Autenticación con JWT (JSON Web Tokens)
- ✅ Cifrado de contraseñas con BCrypt
- ✅ Filtro de autenticación personalizado
- ✅ Configuración de CORS para desarrollo local
- ✅ Endpoints públicos y protegidos

### Arquitectura
- ✅ Arquitectura en capas (Controller → Service → Repository)
- ✅ DTOs para transfer de datos
- ✅ Mappers para conversión de entidades
- ✅ Manejo global de excepciones
- ✅ Validaciones robustas en entrada de datos

### Funcionalidades
- ✅ Gestión de Usuarios (registro, login, CRUD)
- ✅ Gestión de Productos (CRUD, búsqueda por categoría)
- ✅ Gestión de Categorías (CRUD)
- ✅ Gestión de Carrito (agregar items, actualizar cantidad)
- ✅ Gestión de Órdenes (crear, obtener, actualizar estado)

### Mejoras Implementadas
- ✅ Documentación automática con Swagger/OpenAPI
- ✅ Logging con SLF4J en servicios
- ✅ Validaciones mejoradas en DTOs (@Size, @Positive, etc.)
- ✅ Tests unitarios con Mockito (ProductoService, UsuarioService)
- ✅ Configuración de base de datos H2
- ✅ Propiedades de configuración centralizadas

## 📦 Dependencias Principales

```xml
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- JWT (io.jsonwebtoken)
- H2 Database
- Lombok
- SpringDoc OpenAPI (Swagger)
```

## 🔧 Configuración

### Variables de Entorno (application.properties)
```properties
# JWT
jwt.secret=<tu-clave-secreta-fuerte>
jwt.expiration-ms=86400000

# Base de datos
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.JaimeAmuedoJAH.backend=INFO
```

## 🏃 Ejecutar la Aplicación

### Opción 1: Desde Maven
```bash
mvn clean spring-boot:run
```

### Opción 2: Desde IDE
- Click derecho en el proyecto → Run As → Spring Boot App

### Puerto Predeterminado
```
http://localhost:8080
```

## 📚 Documentación de la API

### Swagger/OpenAPI
La documentación interactiva está disponible en:
```
http://localhost:8080/swagger-ui.html
```

Aquí puedes:
- Visualizar todos los endpoints
- Ver parámetros requeridos
- Probar los endpoints directamente
- Copiar los comandos curl

### H2 Console (Base de Datos)
Acceso a la consola web de H2:
```
http://localhost:8080/h2-console
```

**Credenciales:**
- URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: (dejar vacía)

## 🔐 Autenticación

### 1. Registrar Usuario
```bash
POST /usuarios/register
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "rol": "ROLE_USER"
}
```

### 2. Login
```bash
POST /usuarios/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "juan@example.com",
  "rol": "ROLE_USER"
}
```

### 3. Usar Token en Peticiones
Agregar el header `Authorization` a todas las peticiones protegidas:
```
Authorization: Bearer <token-recibido>
```

## 🧪 Ejecutar Tests

### Todos los Tests
```bash
mvn test
```

### Tests Específicos
```bash
# Tests de Producto
mvn test -Dtest=ProductoServiceTest

# Tests de Usuario
mvn test -Dtest=UsuarioServiceTest
```

### Con Cobertura de Código
```bash
mvn test jacoco:report
```

## 📋 Endpoints Principales

### Usuarios
- `GET /usuarios/all` - Obtener todos los usuarios
- `GET /usuarios/{id}` - Obtener usuario por ID
- `POST /usuarios/register` - Registrar nuevo usuario
- `POST /usuarios/login` - Login de usuario
- `PUT /usuarios/update/{id}` - Actualizar usuario
- `DELETE /usuarios/delete/{id}` - Eliminar usuario

### Productos
- `GET /productos` - Obtener todos los productos
- `GET /productos/{id}` - Obtener producto por ID
- `GET /productos/categoria/{categoriaId}` - Obtener productos por categoría
- `POST /productos` - Crear nuevo producto
- `PUT /productos/{id}` - Actualizar producto
- `DELETE /productos/{id}` - Eliminar producto

### Categorías
- `GET /categorias` - Obtener todas las categorías
- `GET /categorias/{id}` - Obtener categoría por ID
- `POST /categorias` - Crear nueva categoría
- `PUT /categorias/{id}` - Actualizar categoría
- `DELETE /categorias/{id}` - Eliminar categoría

### Carrito
- `GET /carrito/{usuarioId}` - Obtener carrito de usuario
- `POST /carrito/{usuarioId}/items` - Agregar item al carrito
- `PUT /carrito/items/{itemId}` - Actualizar cantidad de item
- `DELETE /carrito/items/{itemId}` - Eliminar item del carrito
- `DELETE /carrito/{usuarioId}` - Vaciar carrito

### Órdenes
- `GET /ordenes` - Obtener todas las órdenes
- `GET /ordenes/{id}` - Obtener orden por ID
- `POST /ordenes` - Crear nueva orden
- `PUT /ordenes/{id}` - Actualizar orden
- `DELETE /ordenes/{id}` - Eliminar orden

## 🎯 Validaciones Implementadas

### Usuario
- Email: debe ser válido y único
- Contraseña: mínimo 6 caracteres
- Nombre: 2-100 caracteres

### Producto
- Nombre: 3-200 caracteres
- Precio: mayor que 0
- Stock: no negativo
- Categoría: debe existir

### Categoría
- Nombre: 2-100 caracteres, no puede estar vacío

## 🔍 Logging

El proyecto utiliza SLF4J con Logback. Los logs están configurados por nivel:
- **ERROR**: Errores críticos
- **WARN**: Advertencias
- **INFO**: Información importante (nivel por defecto)
- **DEBUG**: Información de depuración (activado para Spring Security)

Ejemplo de logs en servicios:
```
2026-04-24 10:30:45 - INFO: Obteniendo todos los productos
2026-04-24 10:30:45 - DEBUG: Se obtuvieron 5 productos
2026-04-24 10:30:46 - WARN: Producto no encontrado con ID: 999
```

## 🚨 Manejo de Errores

La aplicación responde con códigos HTTP estándar y mensajes descriptivos:

```json
{
  "timestamp": "2026-04-24T10:30:45.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Producto not found with id 999"
}
```

### Códigos Comunes
- `200 OK`: Solicitud exitosa
- `201 CREATED`: Recurso creado
- `400 BAD REQUEST`: Validación falló
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: Sin permisos
- `404 NOT FOUND`: Recurso no encontrado
- `500 INTERNAL SERVER ERROR`: Error del servidor

## 🔄 CORS (Cross-Origin Resource Sharing)

El CORS está configurado para desarrollo local:
```
Orígenes permitidos:
- http://localhost:3000 (React/Angular)
- http://localhost:4200 (Angular)
- http://localhost:8110 (Frontend local)
```

## 📝 Notas Importantes

1. **Clave JWT**: Cambiar `jwt.secret` en producción a una clave fuerte
2. **Base de datos**: En producción, usar PostgreSQL/MySQL en lugar de H2
3. **CORS**: Actualizar orígenes permitidos en producción
4. **Logging**: Ajustar niveles según necesidad en producción
5. **Validaciones**: Aplicadas en DTOs y servicios

## 🐛 Troubleshooting

### Error: "JWT secret must be at least 32 characters long"
- Cambiar `jwt.secret` en application.properties a una cadena de al menos 32 caracteres

### Error: "403 Forbidden" en Swagger UI
- Asegurar que Swagger está en la lista de endpoints permitidos en SecurityConfig

### Error: "H2 console no accesible"
- Verificar que `spring.h2.console.enabled=true` en application.properties

## 📞 Soporte

Para reportar problemas o sugerencias, abrir un issue en el repositorio.

---

**Versión**: 1.0  
**Última actualización**: Abril 2026  
**Autor**: Jaime Amuedo JAH

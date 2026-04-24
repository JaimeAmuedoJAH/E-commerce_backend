package com.JaimeAmuedoJAH.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utilidad para generar, validar y extraer información de tokens JWT.
 * Proporciona métodos para crear tokens firmados y verificar su validez.
 */
@Component
public class JwtUtil {

    // Clave HMAC para firmar los tokens de forma segura
    private final Key key;
    // Tiempo de expiración del token en milisegundos
    private final long expirationMs;

    /**
     * Constructor que inicializa la clave y el tiempo de expiración desde las propiedades.
     * Valida que la clave secreta tenga al menos 32 caracteres por seguridad.
     *
     * @param secret Clave secreta desde application.properties
     * @param expirationMs Tiempo de expiración en milisegundos desde application.properties
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Genera un nuevo token JWT firmado con el email del usuario.
     * El token incluye la fecha de creación y fecha de expiración.
     *
     * @param subject Email del usuario que se incluirá en el token
     * @return Token JWT codificado como string
     */
    public String generateToken(String subject) {
        // Obtiene la fecha y hora actual
        Date now = new Date();
        // Calcula la fecha de expiración sumando el tiempo configurado
        Date expiryDate = new Date(now.getTime() + expirationMs);

        // Construye el token con los datos necesarios
        return Jwts.builder()
                .setSubject(subject)              // Email del usuario
                .setIssuedAt(now)                 // Fecha de creación
                .setExpiration(expiryDate)        // Fecha de expiración
                .signWith(key, SignatureAlgorithm.HS256)  // Firma con HMAC-SHA256
                .compact();                       // Convierte a string
    }

    /**
     * Extrae el email (subject) del token JWT.
     *
     * @param token Token JWT
     * @return Email del usuario almacenado en el token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token de forma genérica.
     *
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim deseado
     * @param <T> Tipo genérico del claim a extraer
     * @return El valor del claim solicitado
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims (datos) del token verificando su firma.
     *
     * @param token Token JWT a parsear
     * @return Todos los claims contenidos en el token
     */
    public Claims extractAllClaims(String token) {
        // Construye un parser de JWT con la clave de firma
        return Jwts.parserBuilder()
                .setSigningKey(key)              // Usa la clave para verificar la firma
                .build()
                .parseClaimsJws(token)           // Parsea y verifica el token
                .getBody();                      // Obtiene el cuerpo con los claims
    }

    /**
     * Valida que el token sea legítimo y no esté expirado.
     *
     * @param token Token JWT a validar
     * @return true si el token es válido y no está expirado, false en caso contrario
     */
    public boolean isTokenValid(String token) {
        try {
            // Extrae todos los claims del token (verifica la firma)
            Claims claims = extractAllClaims(token);
            // Verifica que la fecha de expiración sea posterior a la actual
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            // Si ocurre cualquier excepción (firma inválida, token malformado, etc.) el token es inválido
            return false;
        }
    }
}

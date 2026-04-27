package com.JaimeAmuedoJAH.backend.security;

import com.JaimeAmuedoJAH.backend.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación que intercepta cada petición HTTP para validar el token JWT.
 * Se ejecuta una única vez por petición (OncePerRequestFilter).
 * Extrae el token del header Authorization y autentica al usuario si el token es válido.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Utilidad para generar, validar y extraer información de tokens JWT
    private final JwtUtil jwtUtil;
    // Repositorio para acceder a los datos de usuarios en la base de datos
    private final UsuarioRepository usuarioRepository;

    /**
     * Método que se ejecuta en cada petición HTTP.
     * Valida el token JWT del header Authorization y autentica al usuario.
     *
     * @param request Petición HTTP recibida
     * @param response Respuesta HTTP a enviar
     * @param filterChain Cadena de filtros a continuar
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Obtiene el header Authorization de la petición
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        // Extrae el token del formato "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Elimina "Bearer " para obtener solo el token
        }

        // Valida el token y verifica que no haya autenticación previa
        if (token != null && jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extrae el email del token
            String email = jwtUtil.extractEmail(token);
            // Busca el usuario en la base de datos por email
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                // Crea un objeto User de Spring Security con los datos del usuario
                UserDetails principal = User.withUsername(usuario.getEmail())
                        .password(usuario.getPassword())
                        .authorities(usuario.getRol())  // Asigna el rol como autoridad
                        .build();

                // Crea un token de autenticación con el usuario y sus autoridades
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal,           // El usuario autenticado
                        null,                // No hay credenciales adicionales
                        principal.getAuthorities()  // Las autoridades (roles) del usuario
                );
                // Asigna detalles de la petición al token de autenticación
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Almacena la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            });
        }

        // Continúa con la siguiente cadena de filtros
        filterChain.doFilter(request, response);
    }
}

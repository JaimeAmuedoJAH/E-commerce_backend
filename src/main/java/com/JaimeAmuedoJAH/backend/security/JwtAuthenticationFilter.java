package com.JaimeAmuedoJAH.backend.security;

import com.JaimeAmuedoJAH.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

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

        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (token != null && jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtUtil.extractEmail(token);
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                // Usar UsuarioPrincipal en lugar de User genérico
                UsuarioPrincipal principal = new UsuarioPrincipal(usuario);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            });
        }

        filterChain.doFilter(request, response);
    }
}

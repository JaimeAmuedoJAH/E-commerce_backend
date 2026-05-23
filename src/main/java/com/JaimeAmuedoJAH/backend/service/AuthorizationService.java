package com.JaimeAmuedoJAH.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de autorización para usar en expresiones @PreAuthorize.
 *
 * En vez de acceder directamente a propiedades custom del principal en SpEL
 * (que Spring Security no siempre resuelve bien), delegamos la lógica a este
 * servicio donde el compilador garantiza que los métodos existen.
 *
 * Uso: @PreAuthorize("hasRole('ADMIN') or @authorizationService.esElMismoUsuario(authentication, #clientePublicId)")
 */
@Service("authorizationService")
@Slf4j
public class AuthorizationService {

    /**
     * Comprueba que el usuario autenticado es el propietario del recurso.
     * @param authentication Contexto de seguridad inyectado por Spring
     * @param clientePublicId UUID del cliente del recurso que se quiere acceder
     * @return true si el usuario autenticado tiene ese publicId
     */
    public boolean esElMismoUsuario(Authentication authentication, String clientePublicId) {
    if (authentication == null || !authentication.isAuthenticated()) {
        log.warn("AUTH: authentication null o no autenticado");
        return false;
    }
    if (!(authentication.getPrincipal() instanceof UsuarioPrincipal principal)) {
        log.warn("AUTH: principal no es UsuarioPrincipal, es: {}", 
                  authentication.getPrincipal().getClass().getName());
        return false;
    }
    log.warn("AUTH: principal.publicId={}, clientePublicId={}, coincide={}", 
              principal.getPublicId(), clientePublicId, 
              principal.getPublicId().equals(clientePublicId));
    return principal.getPublicId().equals(clientePublicId);
}
}
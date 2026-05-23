package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.entity.RefreshTokenEntity;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import com.JaimeAmuedoJAH.backend.exceptions.AuthenticationException;
import com.JaimeAmuedoJAH.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public RefreshTokenEntity crearRefreshToken(UsuarioEntity usuario) {
        // Eliminar refresh token anterior si existe
        refreshTokenRepository.deleteByUsuario(usuario);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiracion(Instant.now().plusMillis(refreshExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity validarRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Refresh token no válido"));

        if (refreshToken.isExpirado()) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthenticationException("Refresh token expirado, inicia sesión de nuevo");
        }

        return refreshToken;
    }

    public void eliminarRefreshToken(UsuarioEntity usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }
}
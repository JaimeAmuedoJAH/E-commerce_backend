package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.RefreshTokenEntity;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUsuario(UsuarioEntity usuario);
}
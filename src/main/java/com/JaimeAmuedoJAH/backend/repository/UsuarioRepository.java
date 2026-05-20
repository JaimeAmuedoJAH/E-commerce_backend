package com.JaimeAmuedoJAH.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByPublicId(String publicId);
    boolean existsByEmail(String email);
}

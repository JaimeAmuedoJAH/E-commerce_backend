package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.JaimeAmuedoJAH.backend.entity.TarjetaEntity;

import java.util.List;
import java.util.Optional;

public interface TarjetaRepository extends JpaRepository<TarjetaEntity, Long> {
    Optional<TarjetaEntity> findByNumeroTarjeta(String numeroTarjeta);
    List<TarjetaEntity> findByUsuario(UsuarioEntity usuario);
    List<TarjetaEntity> findByUsuarioId(Long usuarioId);
}

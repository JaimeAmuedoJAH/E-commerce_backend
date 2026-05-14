package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.JaimeAmuedoJAH.backend.entity.TarjetaEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TarjetaRepository extends JpaRepository<TarjetaEntity, Long> {
    Optional<TarjetaEntity> findByNumeroTarjeta(String numeroTarjeta);
    List<TarjetaEntity> findByUsuario(UsuarioEntity usuario);
    List<TarjetaEntity> findByUsuarioId(Long usuarioId);
    Optional<TarjetaEntity> findByNumeroHash(String numeroHash);

    /**
     * Get the raw hashed CVV from database for secure validation
     * @param id The card ID
     * @return The hashed CVV value
     */
    @Query(value = "SELECT cvv FROM tarjeta WHERE id = :id", nativeQuery = true)
    String findHashedCvvById(Long id);
}

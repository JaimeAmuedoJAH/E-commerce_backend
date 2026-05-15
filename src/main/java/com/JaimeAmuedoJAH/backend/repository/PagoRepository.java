package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    /**
     * Obtener todos los pagos de una orden
     */
    List<PagoEntity> findByOrdenId(Long ordenId);

    /**
     * Obtener pagos completados de una orden
     */
    //List<PagoEntity> findByOrdenIdAndEstado(Long ordenId, PagoEntity.EstadoPago estado);

    /**
     * Obtener pago original por código de transacción
     */
    Optional<PagoEntity> findByCodigoTransaccion(String codigoTransaccion);

    /**
     * Obtener reembolso de una orden
     */
    Optional<PagoEntity> findByOrdenIdAndEstado(Long ordenId, PagoEntity.EstadoPago estado);
}

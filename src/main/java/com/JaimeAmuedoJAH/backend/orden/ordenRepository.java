package com.JaimeAmuedoJAH.backend.orden;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrdenRepository extends JpaRepository<OrdenEntity, Long> {

    /**
     * Buscar órdenes por cliente
     */
    List<OrdenEntity> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    /**
     * Buscar órdenes por estado
     */
    List<OrdenEntity> findByEstadoOrderByFechaCreacionDesc(OrdenEntity.EstadoOrden estado);

    /**
     * Buscar órdenes por cliente y estado
     */
    List<OrdenEntity> findByClienteIdAndEstadoOrderByFechaCreacionDesc(
            Long clienteId, OrdenEntity.EstadoOrden estado);

    /**
     * Obtener el total de ventas por estado
     */
    @Query("SELECT SUM(o.total) FROM OrdenEntity o WHERE o.estado = :estado")
    Double obtenerTotalVentasPorEstado(@Param("estado") OrdenEntity.EstadoOrden estado);

    /**
     * Contar órdenes por estado
     */
    Long countByEstado(OrdenEntity.EstadoOrden estado);
}
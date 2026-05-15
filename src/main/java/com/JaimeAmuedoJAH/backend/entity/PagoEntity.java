package com.JaimeAmuedoJAH.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Auditoría de todas las transacciones de pago
 * Registra pagos completados y reembolsos
 */
@Entity
@Table(name = "pago")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenEntity orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id", nullable = false)
    private TarjetaEntity tarjeta;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Column(nullable = false)
    private String codigoTransaccion;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "fecha_transaccion", nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaTransaccion;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoPago.COMPLETADO;
        }
    }

    /**
     * Estados de pago
     * COMPLETADO: Pago procesado exitosamente
     * REEMBOLSADO: Dinero devuelto al usuario
     */
    public enum EstadoPago {
        COMPLETADO,
        REEMBOLSADO
    }
}

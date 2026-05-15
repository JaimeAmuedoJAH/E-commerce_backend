package com.JaimeAmuedoJAH.backend.entity;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orden")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private UsuarioEntity cliente;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenItemEntity> items;

    @Column(name = "codigo_transaccion")
    private String codigoTransaccion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PagoEntity> pagos;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoOrden.PENDIENTE;
        }
    }

    public enum EstadoOrden {
        PENDIENTE,
        CONFIRMADA,
        ENVIADA,
        ENTREGADA,
        CANCELADA
    }
}
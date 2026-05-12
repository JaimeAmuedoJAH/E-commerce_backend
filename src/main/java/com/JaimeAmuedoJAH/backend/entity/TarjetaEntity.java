package com.JaimeAmuedoJAH.backend.entity;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarjeta")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_tarjeta", nullable = false, unique = true, length = 16)
    private String numeroTarjeta;

    @Column(name = "titular", nullable = false)
    private String titular;

    @Column(name = "fecha_expiracion", nullable = false)
    private String fechaExpiracion;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "saldo", nullable = false)
    private Double saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
}
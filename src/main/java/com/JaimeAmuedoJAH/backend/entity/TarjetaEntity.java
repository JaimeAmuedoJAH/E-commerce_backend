package com.JaimeAmuedoJAH.backend.entity;

import com.JaimeAmuedoJAH.backend.security.CardNumberConverter;
import com.JaimeAmuedoJAH.backend.security.CVVConverter;
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

    @Column(name = "numero_tarjeta", nullable = false, unique = true, length = 256)
    @Convert(converter = CardNumberConverter.class)
    private String numeroTarjeta;

    // ✅ Hash SHA-256 del número plano, para búsquedas eficientes
    @Column(name = "numero_hash", nullable = false, length = 256)
    private String numeroHash;

    @Column(name = "titular", nullable = false)
    private String titular;

    @Column(name = "fecha_expiracion", nullable = false)
    private String fechaExpiracion;

    @Column(name = "cvv", nullable = false, length = 256)
    @Convert(converter = CVVConverter.class)
    private String cvv;

    @Column(name = "saldo", nullable = false)
    private Double saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
}
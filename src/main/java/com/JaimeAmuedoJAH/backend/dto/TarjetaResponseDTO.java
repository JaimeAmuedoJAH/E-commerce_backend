package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;

@Data
public class TarjetaResponseDTO {

    private Long id;
    private Long clienteId;
    private String numeroTarjeta;
    private String titular;
    private String fechaExpiracion;
    private Double saldo;
}
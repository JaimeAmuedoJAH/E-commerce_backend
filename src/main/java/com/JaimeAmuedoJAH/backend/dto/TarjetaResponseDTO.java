package com.JaimeAmuedoJAH.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO seguro para respuesta de tarjeta
 * Nunca expone el número completo ni el CVV
 * Solo muestra últimos 4 dígitos del número de tarjeta
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TarjetaResponseDTO {

    private Long id;
    private Long clienteId;
    
    /**
     * Número de tarjeta enmascarado (ej: **** **** **** 9010)
     * Solo contiene últimos 4 dígitos por seguridad
     */
    private String numeroTarjeta;
    
    private String titular;
    private String fechaExpiracion;
    private Double saldo;
    
    /**
     * CVV NUNCA es incluido en respuestas
     * Se ha hasheado y no debe ser expuesto
     */
}

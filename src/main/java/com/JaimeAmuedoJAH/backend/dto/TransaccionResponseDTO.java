package com.JaimeAmuedoJAH.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para auditoría de transacciones de pago
 * Contiene información sobre pagos completados y reembolsos
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransaccionResponseDTO {

    private Long id;
    private Long ordenId;
    private Long tarjetaId;
    private Double monto;
    private String estado; // COMPLETADO, REEMBOLSADO
    private String codigoTransaccion;
    private String descripcion;
    private LocalDateTime fechaTransaccion;
}

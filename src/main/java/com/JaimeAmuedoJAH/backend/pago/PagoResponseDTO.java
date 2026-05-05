package com.JaimeAmuedoJAH.backend.pago;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponseDTO {

    private boolean exitoso;
    private String mensaje;
    private String codigoTransaccion;
    private Long carritoId;
    private Long clienteId;
}
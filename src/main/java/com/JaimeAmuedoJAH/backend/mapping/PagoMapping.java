package com.JaimeAmuedoJAH.backend.mapping;

import com.JaimeAmuedoJAH.backend.entity.PagoEntity;
import com.JaimeAmuedoJAH.backend.dto.TransaccionResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir PagoEntity a TransaccionResponseDTO
 */
@Component
public class PagoMapping {

    public static TransaccionResponseDTO toResponseDTO(PagoEntity pago) {
        if (pago == null) return null;

        return TransaccionResponseDTO.builder()
                .id(pago.getId())
                .ordenId(pago.getOrden() != null ? pago.getOrden().getId() : null)
                .tarjetaId(pago.getTarjeta() != null ? pago.getTarjeta().getId() : null)
                .monto(pago.getMonto())
                .estado(pago.getEstado().name())
                .codigoTransaccion(pago.getCodigoTransaccion())
                .descripcion(pago.getDescripcion())
                .fechaTransaccion(pago.getFechaTransaccion())
                .build();
    }
}

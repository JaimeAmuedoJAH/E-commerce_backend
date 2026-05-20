package com.JaimeAmuedoJAH.backend.mapping;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import com.JaimeAmuedoJAH.backend.entity.TarjetaEntity;
import com.JaimeAmuedoJAH.backend.dto.TarjetaRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.TarjetaResponseDTO;

public class TarjetaMapping {

    public static TarjetaEntity toEntity(TarjetaRequestDTO dto, UsuarioEntity usuario) {
        return TarjetaEntity.builder()
                .usuario(usuario)
                .numeroTarjeta(dto.getNumeroTarjeta())
                .titular(dto.getTitular())
                .fechaExpiracion(dto.getFechaExpiracion())
                .cvv(dto.getCvv())
                .saldo(dto.getSaldo())
                .build();
    }

    public static TarjetaResponseDTO toResponseDTO(TarjetaEntity entity) {
        TarjetaResponseDTO dto = new TarjetaResponseDTO();
        dto.setId(entity.getId());
        dto.setClientePublicId(entity.getUsuario().getPublicId());  
        dto.setNumeroTarjeta(maskCardNumber(entity.getNumeroTarjeta()));
        dto.setTitular(entity.getTitular());
        dto.setFechaExpiracion(entity.getFechaExpiracion());
        dto.setSaldo(entity.getSaldo());
        return dto;
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return "****-****-****-" + lastFourDigits;
    }
}

package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;

@Data
public class UsuarioLoginResponseDTO {
    private String token;
    private String refreshToken;
    private UsuarioResponseDTO usuario;
}

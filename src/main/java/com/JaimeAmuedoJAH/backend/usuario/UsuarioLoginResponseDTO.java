package com.JaimeAmuedoJAH.backend.usuario;

import lombok.Data;

@Data
public class UsuarioLoginResponseDTO {

    private String token;
    private UsuarioResponseDTO usuario;
}

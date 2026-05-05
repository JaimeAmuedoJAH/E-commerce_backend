package com.JaimeAmuedoJAH.backend.usuario;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
}

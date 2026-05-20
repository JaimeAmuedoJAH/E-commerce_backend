package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private String publicId;
    private String nombre;
    private String email;
    private String rol;
}

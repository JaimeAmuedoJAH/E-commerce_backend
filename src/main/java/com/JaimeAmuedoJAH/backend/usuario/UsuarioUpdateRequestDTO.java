package com.JaimeAmuedoJAH.backend.usuario;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioUpdateRequestDTO {

    private String nombre;

    @Email(message = "El email debe ser válido")
    private String email;

    private String password;

    private String rol;
}

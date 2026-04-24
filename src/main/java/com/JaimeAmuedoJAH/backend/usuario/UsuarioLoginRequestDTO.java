package com.JaimeAmuedoJAH.backend.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioLoginRequestDTO {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}

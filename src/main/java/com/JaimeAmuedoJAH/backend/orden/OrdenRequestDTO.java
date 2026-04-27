package com.JaimeAmuedoJAH.backend.orden;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrdenRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotNull(message = "La dirección no puede ser nula")
    private String direccion;

    @NotEmpty(message = "La orden debe tener al menos un ítem")
    private List<OrdenItemRequestDTO> items;
}

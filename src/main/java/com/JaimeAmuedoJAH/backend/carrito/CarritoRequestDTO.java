package com.JaimeAmuedoJAH.backend.carrito;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CarritoRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotEmpty(message = "El carrito debe contener al menos un ítem")
    private List<CarritoItemRequestDTO> items;
}

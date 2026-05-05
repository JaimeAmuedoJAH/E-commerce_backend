package com.JaimeAmuedoJAH.backend.carrito;

import lombok.Data;

import java.util.List;

@Data
public class CarritoResponseDTO {

    private Long id;
    private Long clienteId;
    private List<CarritoItemResponseDTO> items;
    private Integer totalItems;
}

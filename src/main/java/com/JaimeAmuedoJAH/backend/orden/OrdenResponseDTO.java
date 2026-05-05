package com.JaimeAmuedoJAH.backend.orden;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String direccion;
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<OrdenItemResponseDTO> items;
}

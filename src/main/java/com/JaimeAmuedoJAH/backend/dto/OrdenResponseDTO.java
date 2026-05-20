package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenResponseDTO {

    private Long id;
    private String clientePublicId;
    private String clienteNombre;
    private String direccion;
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<OrdenItemResponseDTO> items;
    private String codigoTransaccion;
}

package com.JaimeAmuedoJAH.backend.categoria;

import com.JaimeAmuedoJAH.backend.producto.ProductoMapping;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaMapping {

    public static CategoriaResponseDTO toResponseDTO(CategoriaEntity categoria) {
        if (categoria == null) {
            return null;
        }
        
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        
        // Mapear productos a ProductoResponseDTO
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            List<ProductoResponseDTO> productosDTO = categoria.getProductos().stream()
                    .map(ProductoMapping::toResponseDTO)
                    .collect(Collectors.toList());
            response.setProductos(productosDTO);
        } else {
            response.setProductos(Collections.emptyList());
        }
        
        return response;
    }

    public static CategoriaEntity toEntity(CategoriaRequestDTO request) {
        if (request == null) {
            return null;
        }
        
        return CategoriaEntity.builder()
                .nombre(request.getNombre())
                .build();
    }

    public static void updateEntity(CategoriaRequestDTO request, CategoriaEntity categoria) {
        if (request == null || categoria == null) {
            return;
        }
        
        if (request.getNombre() != null) {
            categoria.setNombre(request.getNombre());
        }
    }
}
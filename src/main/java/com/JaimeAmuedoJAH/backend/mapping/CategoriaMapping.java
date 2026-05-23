package com.JaimeAmuedoJAH.backend.mapping;

import com.JaimeAmuedoJAH.backend.dto.CategoriaRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.CategoriaResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.CategoriaResponseDetalleDTO;
import com.JaimeAmuedoJAH.backend.dto.ProductoResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.CategoriaEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaMapping {

    public static CategoriaResponseDTO toResponseDTO(CategoriaEntity categoria) {
        if (categoria == null) return null;
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        return response;
    }

    public static CategoriaResponseDetalleDTO toDetalleDTO(CategoriaEntity categoria) {
        if (categoria == null) return null;
        CategoriaResponseDetalleDTO response = new CategoriaResponseDetalleDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            response.setProductos(categoria.getProductos().stream()
                    .map(ProductoMapping::toResponseDTO)
                    .collect(Collectors.toList()));
        } else {
            response.setProductos(Collections.emptyList());
        }
        return response;
    }

    public static CategoriaEntity toEntity(CategoriaRequestDTO request) {
        if (request == null) return null;
        return CategoriaEntity.builder()
                .nombre(request.getNombre())
                .build();
    }

    public static void updateEntity(CategoriaRequestDTO request, CategoriaEntity categoria) {
        if (request == null || categoria == null) return;
        if (request.getNombre() != null) {
            categoria.setNombre(request.getNombre());
        }
    }
}
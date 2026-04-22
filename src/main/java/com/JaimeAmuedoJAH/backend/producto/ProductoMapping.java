package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.categoria.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;

public class ProductoMapping {

    public static ProductoResponseDTO toResponseDTO(ProductoEntity producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setTalla(producto.getTalla());
        response.setColor(producto.getColor());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setImagen(producto.getImagen());
        response.setStock(producto.getStock());

        // Mapear información de la categoría
        if (producto.getCategoria() != null) {
            response.setCategoriaId(producto.getCategoria().getId());
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return response;
    }

    public static ProductoEntity toEntity(ProductoRequestDTO request, CategoriaEntity categoria) {
        if (request == null) {
            return null;
        }

        return ProductoEntity.builder()
                .nombre(request.getNombre())
                .talla(request.getTalla())
                .color(request.getColor())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .imagen(request.getImagen())
                .stock(request.getStock())
                .categoria(categoria)
                .build();
    }

    public static void updateEntityFromDto(ProductoRequestDTO dto, ProductoEntity producto, CategoriaEntity categoria) {
        if (dto == null || producto == null) {
            return;
        }

        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }
        if (dto.getTalla() != null) {
            producto.setTalla(dto.getTalla());
        }
        if (dto.getColor() != null) {
            producto.setColor(dto.getColor());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrecio() != null) {
            producto.setPrecio(dto.getPrecio());
        }
        if (dto.getImagen() != null) {
            producto.setImagen(dto.getImagen());
        }
        if (dto.getStock() != null) {
            producto.setStock(dto.getStock());
        }
        if (categoria != null) {
            producto.setCategoria(categoria);
        }
    }
}
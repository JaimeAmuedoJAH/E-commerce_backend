package com.JaimeAmuedoJAH.backend.carrito;

import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import com.JaimeAmuedoJAH.backend.producto.ProductoMapping;
import com.JaimeAmuedoJAH.backend.producto.ProductoResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarritoMapping {

    public static CarritoResponseDTO toResponseDTO(CarritoEntity carrito, Map<Long, ProductoResponseDTO> productos) {
        if (carrito == null) {
            return null;
        }

        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setId(carrito.getId());
        dto.setClienteId(carrito.getClienteId());

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            dto.setItems(Collections.emptyList());
            dto.setTotalItems(0);
            return dto;
        }

        List<CarritoItemResponseDTO> itemDTOs = carrito.getItems().stream()
                .map(item -> toItemResponseDTO(item, productos.get(item.getProductoId())))
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        dto.setTotalItems(itemDTOs.stream().mapToInt(CarritoItemResponseDTO::getCantidad).sum());
        return dto;
    }

    public static CarritoItemResponseDTO toItemResponseDTO(CarritoItemEntity item, ProductoResponseDTO producto) {
        CarritoItemResponseDTO dto = new CarritoItemResponseDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());
        dto.setProducto(producto);
        return dto;
    }

    public static CarritoEntity toEntity(CarritoRequestDTO request) {
        CarritoEntity carrito = new CarritoEntity();
        carrito.setClienteId(request.getClienteId());
        carrito.setItems(request.getItems().stream()
                .map(CarritoMapping::toItemEntity)
                .collect(Collectors.toList()));
        carrito.getItems().forEach(item -> item.setCarrito(carrito));
        return carrito;
    }

    public static CarritoItemEntity toItemEntity(CarritoItemRequestDTO itemRequest) {
        CarritoItemEntity item = new CarritoItemEntity();
        item.setProductoId(itemRequest.getProductoId());
        item.setCantidad(itemRequest.getCantidad());
        return item;
    }
}

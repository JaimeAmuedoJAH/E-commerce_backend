package com.JaimeAmuedoJAH.backend.orden;

import com.JaimeAmuedoJAH.backend.orden.OrdenRequestDTO;
import com.JaimeAmuedoJAH.backend.orden.OrdenResponseDTO;
import com.JaimeAmuedoJAH.backend.orden.OrdenItemRequestDTO;
import com.JaimeAmuedoJAH.backend.orden.OrdenItemResponseDTO;
import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import com.JaimeAmuedoJAH.backend.producto.ProductoMapping;
import com.JaimeAmuedoJAH.backend.usuario.UsuarioEntity;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenMapping {

    public static OrdenResponseDTO toResponseDTO(OrdenEntity orden) {
        if (orden == null) {
            return null;
        }

        OrdenResponseDTO response = new OrdenResponseDTO();
        response.setId(orden.getId());
        response.setDireccion(orden.getDireccion());
        response.setTotal(orden.getTotal());
        response.setEstado(orden.getEstado().name());
        response.setFechaCreacion(orden.getFechaCreacion());

        // Mapear información del cliente
        if (orden.getCliente() != null) {
            response.setClienteId(orden.getCliente().getId());
            response.setClienteNombre(orden.getCliente().getNombre());
        }

        // Mapear ítems de la orden
        if (orden.getItems() != null && !orden.getItems().isEmpty()) {
            List<OrdenItemResponseDTO> itemsDTO = orden.getItems().stream()
                    .map(OrdenMapping::toItemResponseDTO)
                    .collect(Collectors.toList());
            response.setItems(itemsDTO);
        } else {
            response.setItems(Collections.emptyList());
        }

        return response;
    }

    public static OrdenItemResponseDTO toItemResponseDTO(OrdenItemEntity item) {
        if (item == null) {
            return null;
        }

        OrdenItemResponseDTO response = new OrdenItemResponseDTO();
        response.setId(item.getId());
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setSubtotal(item.getSubtotal());

        // Mapear información del producto
        if (item.getProducto() != null) {
            response.setProducto(ProductoMapping.toResponseDTO(item.getProducto()));
        }

        return response;
    }

    public static OrdenEntity toEntity(OrdenRequestDTO request, UsuarioEntity cliente, List<ProductoEntity> productos) {
        if (request == null || cliente == null) {
            return null;
        }

        // Crear ítems de la orden
        List<OrdenItemEntity> items = request.getItems().stream()
                .map(itemRequest -> toItemEntity(itemRequest, productos))
                .collect(Collectors.toList());

        // Calcular total
        double total = items.stream()
                .mapToDouble(OrdenItemEntity::getSubtotal)
                .sum();

        return OrdenEntity.builder()
                .cliente(cliente)
                .direccion(request.getDireccion())
                .total(total)
                .items(items)
                .build();
    }

    private static OrdenItemEntity toItemEntity(OrdenItemRequestDTO request, List<ProductoEntity> productos) {
        // Encontrar el producto correspondiente
        ProductoEntity producto = productos.stream()
                .filter(p -> p.getId().equals(request.getProductoId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto not found with id: " + request.getProductoId()));

        OrdenItemEntity item = OrdenItemEntity.builder()
                .producto(producto)
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
                .build();

        item.calcularSubtotal();
        return item;
    }

    public static void updateEntity(OrdenRequestDTO request, OrdenEntity orden,
                                   UsuarioEntity cliente, List<ProductoEntity> productos) {
        if (request == null || orden == null) {
            return;
        }

        if (cliente != null) {
            orden.setCliente(cliente);
        }

        if (request.getDireccion() != null) {
            orden.setDireccion(request.getDireccion());
        }

        // Actualizar ítems si se proporcionan
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<OrdenItemEntity> items = request.getItems().stream()
                    .map(itemRequest -> toItemEntity(itemRequest, productos))
                    .collect(Collectors.toList());

            // Limpiar ítems existentes y agregar nuevos
            orden.getItems().clear();
            orden.getItems().addAll(items);

            // Recalcular total
            double total = items.stream()
                    .mapToDouble(OrdenItemEntity::getSubtotal)
                    .sum();
            orden.setTotal(total);
        }
    }
}
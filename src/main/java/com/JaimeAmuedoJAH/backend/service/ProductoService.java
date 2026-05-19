package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.entity.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.repository.CategoriaRepository;
import com.JaimeAmuedoJAH.backend.exceptions.BadRequestException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.dto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.ProductoResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.ProductoEntity;
import com.JaimeAmuedoJAH.backend.mapping.ProductoMapping;
import com.JaimeAmuedoJAH.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones sobre productos.
 * Contiene la lógica de negocio para CRUD de productos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Obtener todos los productos con paginación
     */
    public Page<ProductoResponseDTO> obtenerTodosLosProductos(int page, int size) {
        log.info("Obteniendo todos los productos - página: {}, tamaño: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findAll(pageable)
                .map(ProductoMapping::toResponseDTO);
    }

    /**
     * Obtener un producto por ID
     */
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        log.info("Obteniendo producto con ID: {}", id);
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", id);
                    return new ResourceNotFoundException(
                            "Producto not found with id " + id);
                });
        return ProductoMapping.toResponseDTO(producto);
    }

    /**
     * Obtener productos por categoría
     */
    public Page<ProductoResponseDTO> obtenerProductosPorCategoria(Long categoriaId, int page, int size) {
        log.info("Obteniendo productos por categoría: {} - página: {}, tamaño: {}", categoriaId, page, size);
        
        // Validar que la categoría existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoriaId);
                    return new ResourceNotFoundException(
                            "Categoria not found with id " + categoriaId);
                });

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoRepository.findByCategoriaId(categoriaId, pageable)
                .map(ProductoMapping::toResponseDTO);
        
        log.debug("Se obtuvieron {} productos para categoría {}", productos.getTotalElements(), categoriaId);
        return productos;
    }

    /**
     * Crear un nuevo producto
     */
    public ProductoResponseDTO crearProducto(ProductoRequestDTO productoDetails) {
        log.info("Creando nuevo producto: {}", productoDetails.getNombre());
        
        // Validar precio
        if (productoDetails.getPrecio() == null || productoDetails.getPrecio() <= 0) {
            log.warn("Precio inválido para nuevo producto: {}", productoDetails.getPrecio());
            throw new BadRequestException("El precio debe ser mayor que 0");
        }

        // Validar stock
        if (productoDetails.getStock() == null || productoDetails.getStock() < 0) {
            log.warn("Stock inválido para nuevo producto: {}", productoDetails.getStock());
            throw new BadRequestException("El stock no puede ser negativo");
        }

        // Validar y obtener categoría
        CategoriaEntity categoria = categoriaRepository.findById(productoDetails.getCategoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", productoDetails.getCategoriaId());
                    return new ResourceNotFoundException(
                            "Categoria not found with id " + productoDetails.getCategoriaId());
                });

        // Crear producto
        ProductoEntity producto = ProductoMapping.toEntity(productoDetails, categoria);
        ProductoEntity saved = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", saved.getId());

        return ProductoMapping.toResponseDTO(saved);
    }

    /**
     * Actualizar un producto existente
     */
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO productoDetails) {
        log.info("Actualizando producto con ID: {}", id);
        
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para actualizar con ID: {}", id);
                    return new ResourceNotFoundException(
                            "Producto not found with id " + id);
                });

        // Validar precio si se proporciona
        if (productoDetails.getPrecio() != null && productoDetails.getPrecio() <= 0) {
            log.warn("Precio inválido en actualización: {}", productoDetails.getPrecio());
            throw new BadRequestException("El precio debe ser mayor que 0");
        }

        // Validar stock si se proporciona
        if (productoDetails.getStock() != null && productoDetails.getStock() < 0) {
            log.warn("Stock inválido en actualización: {}", productoDetails.getStock());
            throw new BadRequestException("El stock no puede ser negativo");
        }

        // Validar y obtener categoría si se proporciona
        CategoriaEntity categoria = null;
        if (productoDetails.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(productoDetails.getCategoriaId())
                    .orElseThrow(() -> {
                        log.warn("Categoría no encontrada con ID: {}", productoDetails.getCategoriaId());
                        return new ResourceNotFoundException(
                                "Categoria not found with id " + productoDetails.getCategoriaId());
                    });
        }

        // Actualizar producto
        ProductoMapping.updateEntityFromDto(productoDetails, producto, categoria);
        ProductoEntity updated = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente con ID: {}", id);

        return ProductoMapping.toResponseDTO(updated);
    }

    /**
     * Eliminar un producto
     */
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para eliminar con ID: {}", id);
                    return new ResourceNotFoundException(
                            "Producto not found with id " + id);
                });
        
        productoRepository.delete(producto);
        log.info("Producto eliminado exitosamente con ID: {}", id);
    }
}

package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.categoria.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.categoria.CategoriaRepository;
import com.JaimeAmuedoJAH.backend.exception.BadRequestException;
import com.JaimeAmuedoJAH.backend.exception.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.producto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.producto.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones sobre productos.
 * Contiene la lógica de negocio para CRUD de productos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Obtener todos los productos
     */
    public List<ProductoResponseDTO> obtenerTodosLosProductos() {
        log.info("Obteniendo todos los productos");
        List<ProductoResponseDTO> productos = productoRepository.findAll().stream()
                .map(ProductoMapping::toResponseDTO)
                .collect(Collectors.toList());
        log.debug("Se obtuvieron {} productos", productos.size());
        return productos;
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
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(Long categoriaId) {
        log.info("Obteniendo productos por categoría: {}", categoriaId);
        // Validar que la categoría existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoriaId);
                    return new ResourceNotFoundException(
                            "Categoria not found with id " + categoriaId);
                });

        List<ProductoResponseDTO> productos = productoRepository.findByCategoriaId(categoriaId).stream()
                .map(ProductoMapping::toResponseDTO)
                .collect(Collectors.toList());
        log.debug("Se obtuvieron {} productos para categoría {}", productos.size(), categoriaId);
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
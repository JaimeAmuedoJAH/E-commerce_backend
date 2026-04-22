package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.categoria.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.categoria.CategoriaRepository;
import com.JaimeAmuedoJAH.backend.exceptions.BadRequestException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Obtener todos los productos
     */
    public List<ProductoResponseDTO> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(ProductoMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener un producto por ID
     */
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto not found with id " + id));
        return ProductoMapping.toResponseDTO(producto);
    }

    /**
     * Obtener productos por categoría
     */
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(Long categoriaId) {
        // Validar que la categoría existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria not found with id " + categoriaId));

        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(ProductoMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear un nuevo producto
     */
    public ProductoResponseDTO crearProducto(ProductoRequestDTO productoDetails) {
        // Validar precio
        if (productoDetails.getPrecio() == null || productoDetails.getPrecio() <= 0) {
            throw new BadRequestException("El precio debe ser mayor que 0");
        }

        // Validar stock
        if (productoDetails.getStock() == null || productoDetails.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }

        // Validar y obtener categoría
        CategoriaEntity categoria = categoriaRepository.findById(productoDetails.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria not found with id " + productoDetails.getCategoriaId()));

        // Crear producto
        ProductoEntity producto = ProductoMapping.toEntity(productoDetails, categoria);
        ProductoEntity saved = productoRepository.save(producto);

        return ProductoMapping.toResponseDTO(saved);
    }

    /**
     * Actualizar un producto existente
     */
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO productoDetails) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto not found with id " + id));

        // Validar precio si se proporciona
        if (productoDetails.getPrecio() != null && productoDetails.getPrecio() <= 0) {
            throw new BadRequestException("El precio debe ser mayor que 0");
        }

        // Validar stock si se proporciona
        if (productoDetails.getStock() != null && productoDetails.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }

        // Validar y obtener categoría si se proporciona
        CategoriaEntity categoria = null;
        if (productoDetails.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(productoDetails.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoria not found with id " + productoDetails.getCategoriaId()));
        }

        // Actualizar producto
        ProductoMapping.updateEntityFromDto(productoDetails, producto, categoria);
        ProductoEntity updated = productoRepository.save(producto);

        return ProductoMapping.toResponseDTO(updated);
    }

    /**
     * Eliminar un producto
     */
    public void eliminarProducto(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto not found with id " + id));
        productoRepository.delete(producto);
    }
}
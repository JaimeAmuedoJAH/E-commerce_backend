package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.producto.dto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Obtener todos los productos
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodosLosProductos() {
        List<ProductoResponseDTO> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponseDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Obtener productos por categoría
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorCategoria(
            @PathVariable Long categoriaId) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * Crear un nuevo producto
     */
    @PostMapping("/add")
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @RequestBody ProductoRequestDTO productoDetails) {
        ProductoResponseDTO producto = productoService.crearProducto(productoDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * Actualizar un producto existente
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoRequestDTO productoDetails) {
        ProductoResponseDTO producto = productoService.actualizarProducto(id, productoDetails);
        return ResponseEntity.ok(producto);
    }

    /**
     * Eliminar un producto
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
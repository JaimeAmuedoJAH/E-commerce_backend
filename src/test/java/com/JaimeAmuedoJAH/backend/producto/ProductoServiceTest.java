package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.categoria.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.categoria.CategoriaRepository;
import com.JaimeAmuedoJAH.backend.exception.BadRequestException;
import com.JaimeAmuedoJAH.backend.exception.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.producto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.producto.ProductoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductoService.
 * Verifica la lógica de negocio de productos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoEntity producto;
    private CategoriaEntity categoria;
    private ProductoRequestDTO productoRequestDTO;

    @BeforeEach
    void setup() {
        categoria = new CategoriaEntity();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        producto = new ProductoEntity();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(10);
        producto.setCategoria(categoria);

        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Laptop");
        productoRequestDTO.setPrecio(999.99);
        productoRequestDTO.setStock(10);
        productoRequestDTO.setCategoriaId(1L);
    }

    /**
     * Test para verificar que se obtienen todos los productos
     */
    @Test
    void testObtenerTodosLosProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));

        List<ProductoResponseDTO> result = productoService.obtenerTodosLosProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findAll();
    }

    /**
     * Test para verificar la obtención de un producto por ID existente
     */
    @Test
    void testObtenerProductoPorIdExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponseDTO result = productoService.obtenerProductoPorId(1L);

        assertNotNull(result);
        verify(productoRepository, times(1)).findById(1L);
    }

    /**
     * Test para verificar que se lanza excepción cuando producto no existe
     */
    @Test
    void testObtenerProductoPorIdNoExistente() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtenerProductoPorId(999L);
        });
    }

    /**
     * Test para verificar la creación de un nuevo producto
     */
    @Test
    void testCrearProductoExitoso() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(producto);

        ProductoResponseDTO result = productoService.crearProducto(productoRequestDTO);

        assertNotNull(result);
        verify(productoRepository, times(1)).save(any(ProductoEntity.class));
    }

    /**
     * Test para verificar que no se crea producto con precio inválido
     */
    @Test
    void testCrearProductoConPrecioInvalido() {
        productoRequestDTO.setPrecio(-50.0);

        assertThrows(BadRequestException.class, () -> {
            productoService.crearProducto(productoRequestDTO);
        });
    }

    /**
     * Test para verificar que no se crea producto con stock negativo
     */
    @Test
    void testCrearProductoConStockNegativo() {
        productoRequestDTO.setStock(-5);

        assertThrows(BadRequestException.class, () -> {
            productoService.crearProducto(productoRequestDTO);
        });
    }

    /**
     * Test para verificar la eliminación de un producto
     */
    @Test
    void testEliminarProductoExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).delete(producto);
    }

    /**
     * Test para verificar que no se elimina producto inexistente
     */
    @Test
    void testEliminarProductoNoExistente() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.eliminarProducto(999L);
        });
    }
}

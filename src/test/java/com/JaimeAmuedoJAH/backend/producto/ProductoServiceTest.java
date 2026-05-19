package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.entity.CategoriaEntity;
import com.JaimeAmuedoJAH.backend.repository.CategoriaRepository;
import com.JaimeAmuedoJAH.backend.exceptions.BadRequestException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.dto.ProductoRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.ProductoResponseDTO;
import com.JaimeAmuedoJAH.backend.repository.ProductoRepository;
import com.JaimeAmuedoJAH.backend.service.ProductoService;
import com.JaimeAmuedoJAH.backend.entity.ProductoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    // Paginación por defecto para los tests
    private final int PAGE = 0;
    private final int SIZE = 10;
    private Pageable pageable;

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

        pageable = PageRequest.of(PAGE, SIZE);
    }

    /**
     * Test para verificar que se obtienen todos los productos paginados
     */
    @Test
    void testObtenerTodosLosProductos() {
        // PageImpl simula la respuesta paginada del repository
        Page<ProductoEntity> pageProductos = new PageImpl<>(Arrays.asList(producto), pageable, 1);
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(pageProductos);

        Page<ProductoResponseDTO> result = productoService.obtenerTodosLosProductos(PAGE, SIZE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        verify(productoRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Test para verificar paginación con múltiples páginas
     */
    @Test
    void testObtenerTodosLosProductosVariasPaginas() {
        ProductoEntity producto2 = new ProductoEntity();
        producto2.setId(2L);
        producto2.setNombre("Monitor");
        producto2.setPrecio(299.99);
        producto2.setStock(5);
        producto2.setCategoria(categoria);

        // Simulamos que hay 15 productos en total pero solo devolvemos 10 (page 0)
        Page<ProductoEntity> pageProductos = new PageImpl<>(
                Arrays.asList(producto, producto2), pageable, 15
        );
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(pageProductos);

        Page<ProductoResponseDTO> result = productoService.obtenerTodosLosProductos(PAGE, SIZE);

        assertNotNull(result);
        assertEquals(15, result.getTotalElements());  // total real
        assertEquals(2, result.getContent().size());   // en esta página
        assertEquals(2, result.getTotalPages());        // páginas totales
        assertFalse(result.isLast());                   // no es la última página
        verify(productoRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Test para verificar que se obtienen productos por categoría paginados
     */
    @Test
    void testObtenerProductosPorCategoria() {
        Page<ProductoEntity> pageProductos = new PageImpl<>(Arrays.asList(producto), pageable, 1);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findByCategoriaId(eq(1L), any(Pageable.class))).thenReturn(pageProductos);

        Page<ProductoResponseDTO> result = productoService.obtenerProductosPorCategoria(1L, PAGE, SIZE);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(productoRepository, times(1)).findByCategoriaId(eq(1L), any(Pageable.class));
    }

    /**
     * Test para verificar que se lanza excepción si la categoría no existe
     */
    @Test
    void testObtenerProductosPorCategoriaNoExistente() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtenerProductosPorCategoria(999L, PAGE, SIZE);
        });
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
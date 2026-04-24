package com.JaimeAmuedoJAH.backend.categoria;

import com.JaimeAmuedoJAH.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Obtener todas las categorías con sus productos
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerTodasLasCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtener una categoría por ID con sus productos
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Crear una nueva categoría
     */
    @PostMapping("/add")
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@RequestBody CategoriaRequestDTO categoriaDetails) {
        CategoriaResponseDTO categoria = categoriaService.crearCategoria(categoriaDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    /**
     * Actualizar una categoría existente
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaRequestDTO categoriaDetails) {
        CategoriaResponseDTO categoria = categoriaService.actualizarCategoria(id, categoriaDetails);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Eliminar una categoría
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
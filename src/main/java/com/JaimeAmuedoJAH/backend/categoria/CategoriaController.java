package com.JaimeAmuedoJAH.backend.categoria;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/all")
    public ResponseEntity<List<CategoriaEntity>> getAllCategorias() {

        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEntity> getCategoriaById(@PathVariable Long id) {

        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria not found with id " + id));
        return ResponseEntity.ok(categoria);
    }

    
}
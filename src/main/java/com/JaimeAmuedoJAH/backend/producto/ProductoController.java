package com.JaimeAmuedoJAH.backend.producto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final viajeService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/all")
    public List<ProductoEntity> getAllProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }


}
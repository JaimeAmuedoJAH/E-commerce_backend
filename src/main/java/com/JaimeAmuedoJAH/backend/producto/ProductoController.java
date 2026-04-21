package com.JaimeAmuedoJAH.backend.producto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/all")
    public List<ProductoEntity> getAllProductos() {

        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> getProductoById(@PathVariable Long id) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto not found with id " + id));
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductoEntity> addProducto(
    @RequestBody ProductoEntity producto) {

        ProductoEntity newProducto = productoRepository.save(producto);
        return ResponseEntity.ok(newProducto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {

        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

   @PutMapping("/update/{id}")
    public ResponseEntity<ProductoResponseDTO> updateProducto(
    @PathVariable Long id,
    @RequestBody ProductoRequestDTO productoDetails) {

        ProductoResponseDTO updated = productoService.actualizarProducto(id, productoDetails);
        return ResponseEntity.ok(updated);
    }   
}
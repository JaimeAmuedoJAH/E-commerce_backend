package com.JaimeAmuedoJAH.backend.carrito;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/all")
    public ResponseEntity<List<CarritoResponseDTO>> obtenerTodosLosCarritos() {
        return ResponseEntity.ok(carritoService.obtenerTodosLosCarritos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarritoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoResponseDTO>> obtenerCarritosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarritosPorCliente(clienteId));
    }

    @PostMapping("/add")
    public ResponseEntity<CarritoResponseDTO> crearCarrito(@RequestBody CarritoRequestDTO request) {
        CarritoResponseDTO carrito = carritoService.crearCarrito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrito);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CarritoResponseDTO> actualizarCarrito(
            @PathVariable Long id,
            @RequestBody CarritoRequestDTO request) {
        return ResponseEntity.ok(carritoService.actualizarCarrito(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarCarrito(@PathVariable Long id) {
        carritoService.eliminarCarrito(id);
        return ResponseEntity.noContent().build();
    }
}

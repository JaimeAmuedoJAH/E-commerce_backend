package com.JaimeAmuedoJAH.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.JaimeAmuedoJAH.backend.dto.CarritoRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.CarritoResponseDTO;
import com.JaimeAmuedoJAH.backend.service.CarritoService;

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

    @GetMapping("/cliente/{clientePublicId}")
    public ResponseEntity<List<CarritoResponseDTO>> obtenerCarritosPorCliente(
            @PathVariable String clientePublicId) {  // era Long
        return ResponseEntity.ok(carritoService.obtenerCarritosPorCliente(clientePublicId));
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

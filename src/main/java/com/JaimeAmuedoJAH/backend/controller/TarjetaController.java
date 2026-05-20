package com.JaimeAmuedoJAH.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.JaimeAmuedoJAH.backend.dto.TarjetaRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.TarjetaResponseDTO;
import com.JaimeAmuedoJAH.backend.service.TarjetaService;

import java.util.List;

@RestController
@RequestMapping("/tarjetas")
@RequiredArgsConstructor
public class TarjetaController {

    private final TarjetaService tarjetaService;

    @PostMapping("/add")
    public ResponseEntity<TarjetaResponseDTO> crearTarjeta(@Valid @RequestBody TarjetaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjetaService.crearTarjeta(request));
    }

    @GetMapping("/cliente/{clientePublicId}")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetasPorCliente(
            @PathVariable String clientePublicId) {  // era: Long clienteId
        return ResponseEntity.ok(tarjetaService.obtenerTarjetasPorCliente(clientePublicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaResponseDTO> obtenerTarjetaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tarjetaService.obtenerTarjetaPorId(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable Long id) {
        tarjetaService.eliminarTarjeta(id);
        return ResponseEntity.noContent().build();
    }
}

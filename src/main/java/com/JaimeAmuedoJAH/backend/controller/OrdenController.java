package com.JaimeAmuedoJAH.backend.controller;

import com.JaimeAmuedoJAH.backend.dto.OrdenRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.OrdenResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.OrdenEntity;
import com.JaimeAmuedoJAH.backend.service.OrdenService;
import com.JaimeAmuedoJAH.backend.ratelimit.RateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Gestión de órdenes de compra")
public class OrdenController {

    private final OrdenService ordenService;

    /**
     * Obtener todas las órdenes
     */
    @GetMapping("/all")
    @Operation(summary = "Obtener todas las órdenes")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerTodasLasOrdenes() {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Obtener una orden por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden por ID")
    public ResponseEntity<OrdenResponseDTO> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenResponseDTO orden = ordenService.obtenerOrdenPorId(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Obtener órdenes por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener todas las órdenes de un cliente")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenesPorCliente(
            @PathVariable Long clienteId) {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerOrdenesPorCliente(clienteId);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Obtener órdenes por estado
     */
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener órdenes filtradas por estado")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenesPorEstado(
            @PathVariable OrdenEntity.EstadoOrden estado) {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerOrdenesPorEstado(estado);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Crear una nueva orden
     */
    @PostMapping("/add")
    @Operation(summary = "Crear una nueva orden")
    @RateLimit(maxAttempts = 20, windowSizeSeconds = 300)
    public ResponseEntity<OrdenResponseDTO> crearOrden(
            @RequestBody OrdenRequestDTO ordenRequest) {
        OrdenResponseDTO orden = ordenService.crearOrden(ordenRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    /**
     * Actualizar el estado de una orden
     */
    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de una orden")
    @RateLimit(maxAttempts = 20, windowSizeSeconds = 300)
    public ResponseEntity<OrdenResponseDTO> actualizarEstadoOrden(
            @PathVariable Long id,
            @RequestParam OrdenEntity.EstadoOrden estado) {
        OrdenResponseDTO orden = ordenService.actualizarEstadoOrden(id, estado);
        return ResponseEntity.ok(orden);
    }

    /**
     * Cancelar una orden (con reembolso automático)
     */
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una orden y procesar reembolso")
    @RateLimit(maxAttempts = 10, windowSizeSeconds = 300)
    public ResponseEntity<OrdenResponseDTO> cancelarOrden(@PathVariable Long id) {
        OrdenResponseDTO orden = ordenService.cancelarOrden(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Eliminar una orden (solo si está cancelada)
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar una orden cancelada")
    @RateLimit(maxAttempts = 5, windowSizeSeconds = 300)
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenService.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}

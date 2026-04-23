package com.JaimeAmuedoJAH.backend.orden;

import com.JaimeAmuedoJAH.backend.orden.dto.OrdenRequestDTO;
import com.JaimeAmuedoJAH.backend.orden.dto.OrdenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    /**
     * Obtener todas las órdenes
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerTodasLasOrdenes() {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Obtener una orden por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenResponseDTO orden = ordenService.obtenerOrdenPorId(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Obtener órdenes por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenesPorCliente(
            @PathVariable Long clienteId) {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerOrdenesPorCliente(clienteId);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Obtener órdenes por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenesPorEstado(
            @PathVariable OrdenEntity.EstadoOrden estado) {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerOrdenesPorEstado(estado);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Crear una nueva orden
     */
    @PostMapping("/add")
    public ResponseEntity<OrdenResponseDTO> crearOrden(
            @RequestBody OrdenRequestDTO ordenRequest) {
        OrdenResponseDTO orden = ordenService.crearOrden(ordenRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    /**
     * Actualizar el estado de una orden
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<OrdenResponseDTO> actualizarEstadoOrden(
            @PathVariable Long id,
            @RequestParam OrdenEntity.EstadoOrden estado) {
        OrdenResponseDTO orden = ordenService.actualizarEstadoOrden(id, estado);
        return ResponseEntity.ok(orden);
    }

    /**
     * Cancelar una orden
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponseDTO> cancelarOrden(@PathVariable Long id) {
        OrdenResponseDTO orden = ordenService.cancelarOrden(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Eliminar una orden (solo si está cancelada)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenService.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}
package com.JaimeAmuedoJAH.backend.controller;

import com.JaimeAmuedoJAH.backend.dto.TransaccionResponseDTO;
import com.JaimeAmuedoJAH.backend.service.PagoAuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para auditoría de transacciones de pago
 */
@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Auditoría de pagos y reembolsos")
public class TransaccionController {

    private final PagoAuditoriaService auditoriaService;

    /**
     * Obtener todas las transacciones de una orden
     */
    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Obtener historial de transacciones de una orden")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTransaccionesDeOrden(
            @PathVariable Long ordenId) {
        List<TransaccionResponseDTO> transacciones = auditoriaService.obtenerTransaccionesDeOrden(ordenId);
        return ResponseEntity.ok(transacciones);
    }

    /**
     * Obtener reembolsos de una orden
     */
    @GetMapping("/orden/{ordenId}/reembolsos")
    @Operation(summary = "Obtener reembolsos de una orden")
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerReembolsosDeOrden(
            @PathVariable Long ordenId) {
        List<TransaccionResponseDTO> reembolsos = auditoriaService.obtenerReembolsosDeOrden(ordenId);
        return ResponseEntity.ok(reembolsos);
    }

    /**
     * Obtener pago original de una orden
     */
    @GetMapping("/orden/{ordenId}/pago-original")
    @Operation(summary = "Obtener pago original completado de una orden")
    public ResponseEntity<TransaccionResponseDTO> obtenerPagoOriginal(@PathVariable Long ordenId) {
        TransaccionResponseDTO pago = auditoriaService.obtenerPagoOriginal(ordenId);
        return ResponseEntity.ok(pago);
    }
}

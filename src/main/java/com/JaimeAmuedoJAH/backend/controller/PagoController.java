package com.JaimeAmuedoJAH.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.JaimeAmuedoJAH.backend.dto.PagoRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.PagoResponseDTO;
import com.JaimeAmuedoJAH.backend.service.PagoService;
import com.JaimeAmuedoJAH.backend.ratelimit.RateLimit;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/procesar")
    @RateLimit(maxAttempts = 20, windowSizeSeconds = 600) // 20 attempts per 10 minutes
    public ResponseEntity<PagoResponseDTO> procesarPago(@Valid @RequestBody PagoRequestDTO request) {
        PagoResponseDTO response = pagoService.procesarPago(request);
        return ResponseEntity.ok(response);
    }
}

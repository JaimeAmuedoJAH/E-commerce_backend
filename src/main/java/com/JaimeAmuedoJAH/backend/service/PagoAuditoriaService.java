package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.dto.TransaccionResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.PagoEntity;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.mapping.PagoMapping;
import com.JaimeAmuedoJAH.backend.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de auditoría de transacciones de pago
 * Permite consultar historial de pagos y reembolsos
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoAuditoriaService {

    private final PagoRepository pagoRepository;

    /**
     * Obtener todas las transacciones (pagos y reembolsos) de una orden
     */
    public List<TransaccionResponseDTO> obtenerTransaccionesDeOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId)
                .stream()
                .map(PagoMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener solo los reembolsos de una orden
     */
    public List<TransaccionResponseDTO> obtenerReembolsosDeOrden(Long ordenId) {
        return pagoRepository.findByOrdenIdAndEstado(ordenId, PagoEntity.EstadoPago.REEMBOLSADO)
                .stream()
                .map(PagoMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener el pago original completado de una orden
     */
    public TransaccionResponseDTO obtenerPagoOriginal(Long ordenId) {
        PagoEntity pago = pagoRepository.findByOrdenIdAndEstado(ordenId, PagoEntity.EstadoPago.COMPLETADO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pago completado no encontrado para la orden: " + ordenId));

        return PagoMapping.toResponseDTO(pago);
    }
}

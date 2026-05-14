package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.exceptions.PaymentException;
import com.JaimeAmuedoJAH.backend.entity.TarjetaEntity;
import com.JaimeAmuedoJAH.backend.repository.TarjetaRepository;
import com.JaimeAmuedoJAH.backend.security.CVVValidationService;
import com.JaimeAmuedoJAH.backend.dto.PagoRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.PagoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private final TarjetaService tarjetaService;
    private final TarjetaRepository tarjetaRepository;
    private final CVVValidationService cvvValidationService;
    private final CarritoService carritoService;

    public PagoResponseDTO procesarPago(PagoRequestDTO request) {
        String numeroNormalizado = request.getNumeroTarjeta().replaceAll("[\\s-]", "");
        request.setNumeroTarjeta(numeroNormalizado);
        validarFormatoTarjeta(request);

        TarjetaEntity tarjeta = tarjetaService.obtenerEntidadPorNumero(
                request.getNumeroTarjeta().replaceAll("\\s", "")
        );

        // Verificar CVV usando hash comparison (no exposing CVV)
        String hashedCvv = tarjetaRepository.findHashedCvvById(tarjeta.getId());
        log.info("Hash CVV recuperado: {}", hashedCvv);
        log.info("CVV introducido: {}", request.getCvv());
        log.info("Matches: {}", cvvValidationService.validateCVV(request.getCvv(), hashedCvv));

        if (!cvvValidationService.validateCVV(request.getCvv(), hashedCvv)) {
            return PagoResponseDTO.builder()
                    .exitoso(false)
                    .mensaje("CVV incorrecto.")
                    .carritoId(request.getCarritoId())
                    .clienteId(request.getClienteId())
                    .build();
        }

        if (!tarjeta.getFechaExpiracion().equals(request.getFechaExpiracion())) {
            return PagoResponseDTO.builder()
                    .exitoso(false)
                    .mensaje("Fecha de expiración incorrecta.")
                    .carritoId(request.getCarritoId())
                    .clienteId(request.getClienteId())
                    .build();
        }

        // Verificar saldo
        if (tarjeta.getSaldo() < request.getMonto()) {
            return PagoResponseDTO.builder()
                    .exitoso(false)
                    .mensaje("Saldo insuficiente. Saldo disponible: " + tarjeta.getSaldo() + " €")
                    .carritoId(request.getCarritoId())
                    .clienteId(request.getClienteId())
                    .build();
        }

        // Descontar saldo
        tarjeta.setSaldo(tarjeta.getSaldo() - request.getMonto());
        tarjetaService.actualizarSaldo(tarjeta);

        // Vaciar el carrito tras pago exitoso
        carritoService.eliminarCarrito(request.getCarritoId());

        return PagoResponseDTO.builder()
                .exitoso(true)
                .mensaje("Pago procesado correctamente.")
                .codigoTransaccion(UUID.randomUUID().toString().toUpperCase())
                .carritoId(request.getCarritoId())
                .clienteId(request.getClienteId())
                .build();
    }

    private void validarFormatoTarjeta(PagoRequestDTO request) {
        String numero = request.getNumeroTarjeta().replaceAll("[\\s-]", "");

        if (numero.length() != 16 || !numero.matches("\\d+")) {
            throw new PaymentException("El número de tarjeta debe tener 16 dígitos");
        }
        if (!request.getCvv().matches("\\d{3,4}")) {
            throw new PaymentException("El CVV debe tener 3 o 4 dígitos");
        }
        if (!request.getFechaExpiracion().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new PaymentException("La fecha de expiración debe tener formato MM/AA");
        }
    }
}
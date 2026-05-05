package com.JaimeAmuedoJAH.backend.pago;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID del carrito no puede ser nulo")
    private Long carritoId;

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotBlank(message = "El número de tarjeta no puede estar vacío")
    private String numeroTarjeta;

    @NotBlank(message = "La fecha de expiración no puede estar vacía")
    private String fechaExpiracion;

    @NotBlank(message = "El CVV no puede estar vacío")
    private String cvv;

    @NotBlank(message = "El nombre del titular no puede estar vacío")
    private String titular;

    @NotNull(message = "El monto no puede ser nulo")
    @Min(value = 0, message = "El monto no puede ser negativo")
    private Double monto;
}
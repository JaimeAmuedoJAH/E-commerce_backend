package com.JaimeAmuedoJAH.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.JaimeAmuedoJAH.backend.validation.ValidCardNumber;
import com.JaimeAmuedoJAH.backend.validation.ValidExpirationDate;

@Data
public class TarjetaRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotBlank(message = "El número de tarjeta no puede estar vacío")
    @ValidCardNumber
    private String numeroTarjeta;

    @NotBlank(message = "El titular no puede estar vacío")
    private String titular;

    @NotBlank(message = "La fecha de expiración no puede estar vacía")
    @ValidExpirationDate
    private String fechaExpiracion;

    @NotBlank(message = "El CVV no puede estar vacío")
    private String cvv;

    @NotNull(message = "El saldo no puede ser nulo")
    @Min(value = 0, message = "El saldo no puede ser negativo")
    private Double saldo;
}

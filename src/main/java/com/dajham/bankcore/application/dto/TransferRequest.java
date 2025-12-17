package com.dajham.bankcore.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request DTO para realizar una transferencia de fondos entre cuentas.
 * 
 * @param sourceAccountId El ID de la cuenta origen
 * @param targetAccountId El ID de la cuenta destino
 * @param amount          El monto a transferir
 */
public record TransferRequest(

        @NotNull(message = "El ID de la cuenta origen es obligatorio") Long sourceAccountId,

        @NotNull(message = "El ID de la cuenta destino es obligatorio") Long targetAccountId,

        @NotNull(message = "El monto es obligatorio") @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero") BigDecimal amount) {
}

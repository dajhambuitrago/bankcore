package com.dajham.bankcore.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request DTO para crear una nueva cuenta bancaria.
 * Utiliza Java Record para inmutabilidad y concisión.
 * 
 * @param userId         El ID del usuario propietario de la cuenta
 * @param initialBalance El saldo inicial de la cuenta (debe ser mayor o igual a
 *                       cero)
 */
public record CreateAccountRequest(

        @NotNull(message = "El ID de usuario es obligatorio") Long userId,

        @NotNull(message = "El saldo inicial es obligatorio") @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial debe ser mayor o igual a cero") BigDecimal initialBalance) {
}

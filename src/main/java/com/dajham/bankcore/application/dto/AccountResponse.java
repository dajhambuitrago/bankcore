package com.dajham.bankcore.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO que representa una cuenta bancaria.
 * Utiliza Java Record para inmutabilidad y concisión.
 * 
 * @param id            El identificador único de la cuenta
 * @param accountNumber El número de cuenta
 * @param balance       El saldo actual de la cuenta
 * @param userId        El ID del usuario propietario
 * @param createdAt     La fecha y hora de creación de la cuenta
 */
public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        Long userId,
        LocalDateTime createdAt) {
}

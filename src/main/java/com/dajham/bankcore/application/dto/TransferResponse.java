package com.dajham.bankcore.application.dto;

/**
 * Response DTO que representa el resultado de una transferencia.
 * 
 * @param transactionId El ID de la transacción creada
 * @param referenceCode El código de referencia único de la transacción
 * @param status        El estado de la transferencia
 * @param message       Mensaje descriptivo del resultado
 */
public record TransferResponse(
        Long transactionId,
        String referenceCode,
        String status,
        String message) {
}

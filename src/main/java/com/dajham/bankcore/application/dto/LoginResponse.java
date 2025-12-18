package com.dajham.bankcore.application.dto;

/**
 * DTO para respuestas de login exitoso.
 */
public record LoginResponse(
        String token,
        String type,
        String username) {
    /**
     * Constructor conveniente con tipo por defecto "Bearer".
     */
    public LoginResponse(String token, String username) {
        this(token, "Bearer", username);
    }
}

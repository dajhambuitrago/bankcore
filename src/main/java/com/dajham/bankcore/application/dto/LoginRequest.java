package com.dajham.bankcore.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de login.
 */
public record LoginRequest(
        @NotBlank(message = "El username es obligatorio") String username,

        @NotBlank(message = "La contraseña es obligatoria") String password) {
}

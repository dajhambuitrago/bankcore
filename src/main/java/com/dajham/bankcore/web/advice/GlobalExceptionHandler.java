package com.dajham.bankcore.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Utiliza ProblemDetail (RFC 7807) para respuestas de error estandarizadas.
 * 
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807 - Problem Details
 *      for HTTP APIs</a>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de lógica de negocio (IllegalArgumentException).
     * Estas excepciones son lanzadas por el dominio cuando:
     * - Hay saldo insuficiente
     * - Los montos son negativos o inválidos
     * - Se intenta transferir a la misma cuenta
     * - Las cuentas no existen
     * 
     * @param ex La excepción capturada
     * @return ProblemDetail con HTTP 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());

        problemDetail.setTitle("Validación de Negocio Fallida");
        problemDetail.setType(URI.create("https://bankcore.dajham.com/errors/business-validation"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Maneja errores de validación de DTOs (anotaciones @Valid).
     * Captura fallos de validación como @NotNull, @DecimalMin, etc.
     * 
     * @param ex La excepción de validación
     * @return ProblemDetail con HTTP 400 y lista de campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "La solicitud contiene datos inválidos");

        problemDetail.setTitle("Errores de Validación");
        problemDetail.setType(URI.create("https://bankcore.dajham.com/errors/validation"));
        problemDetail.setProperty("timestamp", Instant.now());

        // Recopilar todos los errores de validación
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", validationErrors);

        return problemDetail;
    }

    /**
     * Maneja conflictos de concurrencia optimista (versionado JPA).
     * Retorna 409 Conflict cuando dos transacciones intentan modificar el mismo
     * recurso.
     *
     * @param ex Excepción de locking optimista
     * @return ProblemDetail con HTTP 409 (Conflict)
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "La operación no pudo completarse por un conflicto de concurrencia. Intente nuevamente.");

        problemDetail.setTitle("Conflicto de Concurrencia");
        problemDetail.setType(URI.create("https://bankcore.dajham.com/errors/concurrency-conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Maneja excepciones genéricas no capturadas por otros handlers.
     * Actúa como fallback para errores inesperados.
     * 
     * @param ex La excepción genérica
     * @return ProblemDetail con HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor");

        problemDetail.setTitle("Error Interno del Servidor");
        problemDetail.setType(URI.create("https://bankcore.dajham.com/errors/internal"));
        problemDetail.setProperty("timestamp", Instant.now());

        // En producción, no exponer detalles técnicos
        // Solo para desarrollo/debug:
        problemDetail.setProperty("debug", ex.getMessage());

        return problemDetail;
    }
}

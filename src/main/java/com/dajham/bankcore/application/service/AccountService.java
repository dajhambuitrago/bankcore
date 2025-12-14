package com.dajham.bankcore.application.service;

import com.dajham.bankcore.application.dto.CreateAccountRequest;
import com.dajham.bankcore.application.dto.AccountResponse;
import com.dajham.bankcore.domain.model.Account;
import com.dajham.bankcore.domain.port.AccountRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de aplicación para operaciones relacionadas con cuentas bancarias.
 * Esta clase orquesta las operaciones entre la capa de presentación y el
 * dominio.
 * Implementa casos de uso específicos de la aplicación.
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepositoryPort accountRepositoryPort;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param accountRepositoryPort Puerto de salida para operaciones de
     *                              persistencia
     */
    public AccountService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    /**
     * Crea una nueva cuenta bancaria.
     * 
     * @param request Los datos para crear la cuenta
     * @return Los datos de la cuenta creada
     * @throws IllegalArgumentException si ya existe una cuenta con el número
     *                                  generado
     */
    public AccountResponse createAccount(CreateAccountRequest request) {
        // Generar un número de cuenta único
        String accountNumber = generateAccountNumber();

        // Validar que el número de cuenta no exista (por seguridad adicional)
        while (accountRepositoryPort.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        // Crear la entidad de dominio
        Account account = new Account(
                accountNumber,
                request.initialBalance(),
                request.userId());

        // Guardar la cuenta a través del puerto
        Account savedAccount = accountRepositoryPort.save(account);

        // Mapear a DTO de respuesta
        return mapToResponse(savedAccount);
    }

    /**
     * Genera un número de cuenta único basado en timestamp y UUID.
     * Formato: ACC-[timestamp-corto]-[UUID-corto]
     * Ejemplo: ACC-1234567890-A1B2C3
     * 
     * @return Un número de cuenta único
     */
    private String generateAccountNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ACC-" + timestamp + "-" + uuid;
    }

    /**
     * Mapea una entidad de dominio Account a un DTO AccountResponse.
     * 
     * @param account La entidad de dominio
     * @return El DTO de respuesta
     */
    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUserId(),
                LocalDateTime.now() // Timestamp de respuesta
        );
    }
}

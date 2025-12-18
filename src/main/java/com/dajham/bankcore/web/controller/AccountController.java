package com.dajham.bankcore.web.controller;

import com.dajham.bankcore.application.dto.CreateAccountRequest;
import com.dajham.bankcore.application.dto.AccountResponse;
import com.dajham.bankcore.application.service.AccountService;
import com.dajham.bankcore.infrastructure.persistence.entity.UserEntity;
import com.dajham.bankcore.infrastructure.persistence.repository.UserEntityRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controlador REST para operaciones relacionadas con cuentas bancarias.
 * Expone endpoints HTTP para la gestión de cuentas.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserEntityRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param accountService Servicio de aplicación para operaciones de cuentas
     * @param userRepository Repositorio de usuarios
     */
    public AccountController(AccountService accountService, UserEntityRepository userRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint para obtener todas las cuentas del usuario autenticado.
     * 
     * @param authentication El contexto de seguridad con el usuario autenticado
     * @return Lista de cuentas del usuario
     */
    @GetMapping
    public ResponseEntity<java.util.List<AccountResponse>> getUserAccounts(Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);
        java.util.List<AccountResponse> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Endpoint para obtener una cuenta específica por su número de cuenta.
     * Útil para búsquedas y transferencias a otros usuarios.
     * NOTA: Este endpoint debe ir ANTES de /{accountId} para que Spring lo mapee
     * correctamente.
     * 
     * @param accountNumber El número de cuenta a buscar
     * @return Los datos de la cuenta encontrada
     */
    @GetMapping("/search")
    public ResponseEntity<AccountResponse> searchAccountByNumber(@RequestParam String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener una cuenta específica por ID.
     * 
     * @param accountId El ID de la cuenta
     * @return Los datos de la cuenta
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountId) {
        AccountResponse response = accountService.getAccountById(accountId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para crear una nueva cuenta bancaria.
     * El userId se obtiene del contexto de seguridad (usuario autenticado).
     * 
     * @param initialBalance El saldo inicial de la cuenta
     * @param authentication El contexto de seguridad con el usuario autenticado
     * @return ResponseEntity con el estado 201 (Created) y los datos de la cuenta
     *         creada
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestParam(required = false, defaultValue = "0.0") BigDecimal initialBalance,
            Authentication authentication) {

        Long userId = extractUserIdFromAuth(authentication);
        CreateAccountRequest request = new CreateAccountRequest(userId, initialBalance);
        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Extrae el userId del contexto de autenticación.
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        final String username;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        return user.getId();
    }
}

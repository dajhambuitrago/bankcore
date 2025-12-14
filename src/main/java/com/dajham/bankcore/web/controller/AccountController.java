package com.dajham.bankcore.web.controller;

import com.dajham.bankcore.application.dto.CreateAccountRequest;
import com.dajham.bankcore.application.dto.AccountResponse;
import com.dajham.bankcore.application.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones relacionadas con cuentas bancarias.
 * Expone endpoints HTTP para la gestión de cuentas.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param accountService Servicio de aplicación para operaciones de cuentas
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Endpoint para crear una nueva cuenta bancaria.
     * 
     * @param request Los datos de la cuenta a crear (validados)
     * @return ResponseEntity con el estado 201 (Created) y los datos de la cuenta
     *         creada
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

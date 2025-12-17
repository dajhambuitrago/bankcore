package com.dajham.bankcore.web.controller;

import com.dajham.bankcore.application.dto.TransferRequest;
import com.dajham.bankcore.application.dto.TransferResponse;
import com.dajham.bankcore.application.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de transferencia de fondos.
 */
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param transferService Servicio de transferencias
     */
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Endpoint para realizar una transferencia de fondos.
     * 
     * @param request Los datos de la transferencia (validados)
     * @return ResponseEntity con el estado 201 (Created) y el resultado de la
     *         transferencia
     */
    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request) {

        TransferResponse response = transferService.transfer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

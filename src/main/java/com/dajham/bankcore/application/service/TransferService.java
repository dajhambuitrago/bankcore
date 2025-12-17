package com.dajham.bankcore.application.service;

import com.dajham.bankcore.application.dto.TransferRequest;
import com.dajham.bankcore.application.dto.TransferResponse;
import com.dajham.bankcore.domain.model.Account;
import com.dajham.bankcore.domain.model.Transaction;
import com.dajham.bankcore.domain.port.AccountRepositoryPort;
import com.dajham.bankcore.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación para operaciones de transferencia de fondos.
 * Orquesta las operaciones entre cuentas y transacciones garantizando la
 * integridad de datos.
 */
@Service
@Transactional
public class TransferService {

    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param accountRepositoryPort     Puerto para operaciones de cuentas
     * @param transactionRepositoryPort Puerto para operaciones de transacciones
     */
    public TransferService(
            AccountRepositoryPort accountRepositoryPort,
            TransactionRepositoryPort transactionRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    /**
     * Realiza una transferencia de fondos entre dos cuentas.
     * La operación es atómica gracias a @Transactional.
     * 
     * @param request Los datos de la transferencia
     * @return El resultado de la transferencia
     * @throws IllegalArgumentException Si las cuentas no existen, son la misma, o
     *                                  no hay fondos suficientes
     */
    public TransferResponse transfer(TransferRequest request) {
        // 1. Validar que origen y destino sean diferentes
        if (request.sourceAccountId().equals(request.targetAccountId())) {
            throw new IllegalArgumentException(
                    "La cuenta origen y destino no pueden ser la misma");
        }

        // 2. Cargar cuenta origen
        Account sourceAccount = accountRepositoryPort.findById(request.sourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La cuenta origen con ID " + request.sourceAccountId() + " no existe"));

        // 3. Cargar cuenta destino
        Account targetAccount = accountRepositoryPort.findById(request.targetAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La cuenta destino con ID " + request.targetAccountId() + " no existe"));

        // 4. Realizar retiro de la cuenta origen (incluye validación de saldo en el
        // dominio)
        sourceAccount.withdraw(request.amount());

        // 5. Realizar depósito en la cuenta destino
        targetAccount.deposit(request.amount());

        // 6. Guardar ambas cuentas actualizadas
        accountRepositoryPort.save(sourceAccount);
        accountRepositoryPort.save(targetAccount);

        // 7. Crear y guardar registro de transacción
        Transaction transaction = new Transaction(
                sourceAccount.getId(),
                targetAccount.getId(),
                request.amount());
        Transaction savedTransaction = transactionRepositoryPort.save(transaction);

        // 8. Retornar respuesta exitosa
        return new TransferResponse(
                savedTransaction.getId(),
                savedTransaction.getReferenceCode(),
                "SUCCESS",
                String.format("Transferencia de %.2f completada exitosamente", request.amount()));
    }
}

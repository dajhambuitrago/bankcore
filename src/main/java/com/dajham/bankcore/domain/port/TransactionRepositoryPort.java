package com.dajham.bankcore.domain.port;

import com.dajham.bankcore.domain.model.Transaction;

/**
 * Puerto de salida para operaciones de persistencia de transacciones.
 * Define el contrato que debe implementar la capa de infraestructura.
 */
public interface TransactionRepositoryPort {

    /**
     * Guarda una transacción en el repositorio.
     * 
     * @param transaction La transacción a guardar
     * @return La transacción guardada con su ID asignado
     */
    Transaction save(Transaction transaction);
}

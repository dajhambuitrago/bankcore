package com.dajham.bankcore.domain.port;

import com.dajham.bankcore.domain.model.Account;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de cuentas.
 * Esta interfaz define el contrato que debe implementar la capa de
 * infraestructura.
 * Siguiendo los principios de Clean Architecture, el dominio define el
 * contrato.
 */
public interface AccountRepositoryPort {

    /**
     * Guarda una cuenta en el repositorio.
     * 
     * @param account La cuenta a guardar
     * @return La cuenta guardada con su ID asignado
     */
    Account save(Account account);

    /**
     * Busca una cuenta por su ID.
     * 
     * @param id El ID de la cuenta
     * @return Un Optional conteniendo la cuenta si existe
     */
    Optional<Account> findById(Long id);

    /**
     * Busca una cuenta por su número de cuenta.
     * 
     * @param accountNumber El número de cuenta
     * @return Un Optional conteniendo la cuenta si existe
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Elimina una cuenta por su ID.
     * 
     * @param id El ID de la cuenta a eliminar
     */
    void deleteById(Long id);

    /**
     * Verifica si existe una cuenta con el número dado.
     * 
     * @param accountNumber El número de cuenta
     * @return true si existe, false en caso contrario
     */
    boolean existsByAccountNumber(String accountNumber);
}

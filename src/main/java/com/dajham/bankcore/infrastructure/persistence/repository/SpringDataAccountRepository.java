package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para operaciones de persistencia de cuentas.
 * Extiende JpaRepository para obtener operaciones CRUD básicas.
 */
@Repository
public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Busca una cuenta por su número de cuenta.
     * 
     * @param accountNumber El número de cuenta
     * @return Un Optional conteniendo la cuenta si existe
     */
    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    /**
     * Verifica si existe una cuenta con el número dado.
     * 
     * @param accountNumber El número de cuenta
     * @return true si existe, false en caso contrario
     */
    boolean existsByAccountNumber(String accountNumber);
}

package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para operaciones de persistencia de cuentas.
 * Extiende JpaRepository para obtener operaciones CRUD básicas.
 */
@Repository
public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Busca una cuenta por su número de cuenta (case-insensitive).
     * 
     * @param accountNumber El número de cuenta
     * @return Un Optional conteniendo la cuenta si existe
     */
    @Query("SELECT a FROM AccountEntity a WHERE UPPER(a.accountNumber) = UPPER(:accountNumber)")
    Optional<AccountEntity> findByAccountNumber(@Param("accountNumber") String accountNumber);

    /**
     * Verifica si existe una cuenta con el número dado.
     * 
     * @param accountNumber El número de cuenta
     * @return true si existe, false en caso contrario
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Busca todas las cuentas de un usuario.
     * 
     * @param userId El ID del usuario
     * @return Lista de cuentas del usuario
     */
    java.util.List<AccountEntity> findByUserId(Long userId);
}

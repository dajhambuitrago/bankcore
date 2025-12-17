package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para operaciones de persistencia de
 * transacciones.
 */
@Repository
public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {
}

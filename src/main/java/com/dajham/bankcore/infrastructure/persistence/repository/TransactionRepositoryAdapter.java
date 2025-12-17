package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.domain.model.Transaction;
import com.dajham.bankcore.domain.port.TransactionRepositoryPort;
import com.dajham.bankcore.infrastructure.persistence.entity.TransactionEntity;
import com.dajham.bankcore.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

/**
 * Adaptador que implementa el puerto de repositorio de transacciones del
 * dominio.
 * Conecta la capa de dominio con Spring Data JPA.
 */
@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final SpringDataTransactionRepository jpaRepository;
    private final TransactionMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param jpaRepository El repositorio Spring Data JPA
     * @param mapper        El mapper para conversión entre dominio y entidad JPA
     */
    public TransactionRepositoryAdapter(
            SpringDataTransactionRepository jpaRepository,
            TransactionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}

package com.dajham.bankcore.infrastructure.persistence.mapper;

import com.dajham.bankcore.domain.model.Transaction;
import com.dajham.bankcore.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio (Transaction) y entidades
 * JPA (TransactionEntity).
 */
@Component
public class TransactionMapper {

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     * 
     * @param entity La entidad JPA
     * @return La entidad de dominio
     */
    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setId(entity.getId());
        transaction.setSourceAccountId(entity.getSourceAccountId());
        transaction.setTargetAccountId(entity.getTargetAccountId());
        transaction.setAmount(entity.getAmount());
        transaction.setTimestamp(entity.getTimestamp());
        transaction.setReferenceCode(entity.getReferenceCode());

        return transaction;
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA.
     * 
     * @param domain La entidad de dominio
     * @return La entidad JPA
     */
    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }

        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setSourceAccountId(domain.getSourceAccountId());
        entity.setTargetAccountId(domain.getTargetAccountId());
        entity.setAmount(domain.getAmount());
        entity.setTimestamp(domain.getTimestamp());
        entity.setReferenceCode(domain.getReferenceCode());

        return entity;
    }
}

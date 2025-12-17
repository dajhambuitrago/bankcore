package com.dajham.bankcore.infrastructure.persistence.mapper;

import com.dajham.bankcore.domain.model.Account;
import com.dajham.bankcore.infrastructure.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio (Account) y entidades JPA
 * (AccountEntity).
 * Este componente es parte de la capa de infraestructura y facilita la
 * traducción
 * entre el modelo de dominio y el modelo de persistencia.
 */
@Component
public class AccountMapper {

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     * 
     * @param entity La entidad JPA
     * @return La entidad de dominio
     */
    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        Account account = new Account();
        account.setId(entity.getId());
        account.setAccountNumber(entity.getAccountNumber());
        account.setBalance(entity.getBalance());
        account.setUserId(entity.getUserId());
        account.setVersion(entity.getVersion());

        return account;
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA.
     * 
     * @param domain La entidad de dominio
     * @return La entidad JPA
     */
    public AccountEntity toEntity(Account domain) {
        if (domain == null) {
            return null;
        }

        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId());
        entity.setAccountNumber(domain.getAccountNumber());
        entity.setBalance(domain.getBalance());
        entity.setUserId(domain.getUserId());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    /**
     * Actualiza una entidad JPA existente con los datos de una entidad de dominio.
     * Útil para operaciones de actualización donde queremos mantener campos como
     * createdAt.
     * 
     * @param entity La entidad JPA a actualizar
     * @param domain La entidad de dominio con los nuevos datos
     */
    public void updateEntity(AccountEntity entity, Account domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setAccountNumber(domain.getAccountNumber());
        entity.setBalance(domain.getBalance());
        entity.setUserId(domain.getUserId());
        // No actualizamos el ID ni los campos de auditoría
    }
}

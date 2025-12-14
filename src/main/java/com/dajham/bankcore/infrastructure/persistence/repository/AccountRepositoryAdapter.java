package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.domain.model.Account;
import com.dajham.bankcore.domain.port.AccountRepositoryPort;
import com.dajham.bankcore.infrastructure.persistence.entity.AccountEntity;
import com.dajham.bankcore.infrastructure.persistence.mapper.AccountMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de repositorio del dominio.
 * Esta clase conecta la capa de dominio con la capa de infraestructura (Spring
 * Data JPA).
 * Implementa el patrón Adapter de Clean Architecture.
 */
@Repository
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository jpaRepository;
    private final AccountMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param jpaRepository El repositorio Spring Data JPA
     * @param mapper        El mapper para conversión entre dominio y entidad JPA
     */
    public AccountRepositoryAdapter(
            SpringDataAccountRepository jpaRepository,
            AccountMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}

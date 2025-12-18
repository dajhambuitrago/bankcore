package com.dajham.bankcore.infrastructure.persistence.repository;

import com.dajham.bankcore.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad UserEntity.
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Busca un usuario por su username.
     *
     * @param username El nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el username dado.
     *
     * @param username El nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email El email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}

package com.dajham.bankcore.infrastructure.persistence;

import com.dajham.bankcore.infrastructure.persistence.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de UserDetailsService para cargar usuarios desde la base de
 * datos.
 * Spring Security utiliza este servicio para autenticar usuarios.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserEntityRepository userRepository;

    public CustomUserDetailsService(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su username desde la base de datos.
     * Este método es llamado por Spring Security durante el proceso de
     * autenticación.
     *
     * @param username El nombre de usuario a buscar
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con username: " + username));
    }
}

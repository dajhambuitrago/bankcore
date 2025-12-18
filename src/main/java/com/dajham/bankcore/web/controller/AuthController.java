package com.dajham.bankcore.web.controller;

import com.dajham.bankcore.application.dto.LoginRequest;
import com.dajham.bankcore.application.dto.LoginResponse;
import com.dajham.bankcore.application.dto.RegisterRequest;
import com.dajham.bankcore.infrastructure.persistence.entity.UserEntity;
import com.dajham.bankcore.infrastructure.persistence.repository.UserEntityRepository;
import com.dajham.bankcore.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación (login y registro).
 * Endpoints públicos para gestionar el acceso de usuarios.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserEntityRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint de login.
     * Autentica al usuario y devuelve un token JWT.
     *
     * @param request Credenciales del usuario (username, password)
     * @return LoginResponse con el token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // 1. Autenticar usando AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        // 2. Obtener UserDetails del resultado de autenticación
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generar token JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Retornar respuesta con token
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername()));
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * Crea un nuevo usuario en el sistema.
     *
     * @param request Datos del nuevo usuario
     * @return Mensaje de confirmación
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // Validar que el username no exista
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El username ya está en uso");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El email ya está registrado");
        }

        // Crear nuevo usuario
        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.username());
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setEmail(request.email());
        newUser.setFullName(request.fullName());
        newUser.setEnabled(true);

        // Guardar en base de datos
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario registrado exitosamente");
    }
}

package com.dajham.bankcore.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta todas las peticiones HTTP para validar tokens.
 * Se ejecuta una vez por petición (OncePerRequestFilter).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtra cada petición HTTP para validar el token JWT.
     * 
     * Pasos:
     * 1. Extrae el header "Authorization"
     * 2. Valida que tenga formato "Bearer <token>"
     * 3. Extrae el username del token
     * 4. Si el usuario no está autenticado, carga sus detalles y valida el token
     * 5. Establece el contexto de seguridad si el token es válido
     * 6. Continúa con la cadena de filtros
     *
     * @param request     La petición HTTP
     * @param response    La respuesta HTTP
     * @param filterChain La cadena de filtros
     * @throws ServletException Si ocurre un error de servlet
     * @throws IOException      Si ocurre un error de I/O
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraer el header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Validar que el header exista y tenga formato Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token JWT (remover "Bearer " del inicio)
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // 4. Si el username existe y no hay autenticación previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar detalles del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validar el token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                // Agregar detalles de la petición (IP, session, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}

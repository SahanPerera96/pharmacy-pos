package com.pharmacy.pos.config;

import com.pharmacy.pos.common.security.JwtAuthFilter;
import com.pharmacy.pos.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pharmacy.pos.common.security.UserPrincipal;

/**
 * SecurityConfig
 *
 * Core Spring Security configuration.
 *
 * Key decisions:
 *  - Stateless (no sessions) — JWT in every request
 *  - CSRF disabled (safe for stateless REST APIs)
 *  - @EnableMethodSecurity enables @PreAuthorize on service/controller methods
 *  - URL-level rules here are coarse-grained; fine-grained RBAC is done
 *    via @PreAuthorize in the service layer
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // Enables @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;

    // ── Public paths that never require authentication ───────────────
    private static final String[] PUBLIC_PATHS = {
        "/api/v1/auth/**",          // login, refresh-token
        "/swagger-ui/**",           // Swagger UI
        "/api/v1/api-docs/**",      // OpenAPI spec
        "/actuator/health",         // Health check (load balancer)
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Disable CSRF (not needed for stateless REST) ─────────
            .csrf(AbstractHttpConfigurer::disable)

            // ── No session — each request must carry a JWT ───────────
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── URL-level access rules ────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Public
                .requestMatchers(PUBLIC_PATHS).permitAll()

                // Preflight CORS requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Admin / Owner only endpoints (fine-grained via @PreAuthorize too)
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "OWNER")
                .requestMatchers("/api/v1/branches/manage/**").hasAnyRole("ADMIN", "OWNER")

                // All other endpoints: must be authenticated (role checks done in service)
                .anyRequest().authenticated()
            )

            // ── Wire in our JWT filter before Spring's default auth ──
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── UserDetailsService: loads User from DB by email ─────────────
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return email -> userRepository.findByEmailAndIsActiveTrue(email)
//                .map(UserPrincipal::from)
//                .orElseThrow(() -> new UsernameNotFoundException(
//                        "No active user found with email: " + email));
//    }
//
//    // ── DaoAuthenticationProvider: uses our UserDetailsService + BCrypt
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService());
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
//
//    // ── AuthenticationManager: used by AuthService to authenticate ──
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    // ── BCrypt password encoder (strength 12 for production security)
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
}

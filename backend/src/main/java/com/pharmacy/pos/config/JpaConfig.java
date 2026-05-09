package com.pharmacy.pos.config;

import com.pharmacy.pos.common.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JpaConfig
 *
 * Enables Spring Data JPA auditing so that BaseEntity fields
 * (createdAt, updatedAt, createdBy, updatedBy) are populated automatically.
 *
 * AuditorAware reads the current user's email from the SecurityContext —
 * this is the same email stored in the JWT.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(basePackages = "com.pharmacy.pos")
public class JpaConfig {

    /**
     * Provides the current username (email) to Spring Data JPA auditing.
     * Returns "SYSTEM" for automated/background processes with no auth context.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            if (auth.getPrincipal() instanceof UserPrincipal principal) {
                return Optional.of(principal.getEmail());
            }

            return Optional.of(auth.getName());
        };
    }
}

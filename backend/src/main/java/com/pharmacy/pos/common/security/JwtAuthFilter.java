package com.pharmacy.pos.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter
 *
 * Runs once per HTTP request (via OncePerRequestFilter).
 * Flow:
 *   1. Extract "Bearer <token>" from Authorization header
 *   2. Validate the token via JwtService
 *   3. Load the user from DB
 *   4. Set the Authentication object in SecurityContext
 *
 * If any step fails, the filter simply does nothing — Spring Security
 * will then reject the request as unauthenticated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String token = extractToken(request);

        // No token present — let Spring Security handle as anonymous
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtService.extractEmail(token);

            // Only authenticate if not already set in context
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserPrincipal principal = (UserPrincipal)
                        userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, principal)
                        && jwtService.isAccessToken(token)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user: {} | Role: {} | Path: {}",
                            email, principal.getRole(), request.getRequestURI());
                }
            }
        } catch (Exception e) {
            // Log but don't throw — unauthenticated requests are handled by Spring Security
            log.warn("JWT authentication failed for path {}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the raw JWT from the Authorization: Bearer <token> header.
     * Returns null if the header is absent or malformed.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

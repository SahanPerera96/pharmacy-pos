package com.pharmacy.pos.modules.auth;

import com.pharmacy.pos.common.exception.UnauthorizedException;
import com.pharmacy.pos.common.security.JwtService;
import com.pharmacy.pos.common.security.UserPrincipal;
import com.pharmacy.pos.modules.auth.dto.AuthDtos.*;
import com.pharmacy.pos.modules.user.User;
import com.pharmacy.pos.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService
 *
 * Handles:
 *  - Login (email + password → access + refresh tokens)
 *  - Token refresh (refresh token → new access token)
 *  - Password change
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService    userDetailsService;
    private final UserRepository        userRepository;
    private final JwtService            jwtService;
    private final PasswordEncoder       passwordEncoder;

    @Value("${app.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    // ── Login ────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        try {
            // Spring Security authenticates credentials (throws on failure)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );
        } catch (DisabledException e) {
            throw new UnauthorizedException("Account is locked or inactive. Contact your administrator.");
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        UserPrincipal principal = (UserPrincipal)
                userDetailsService.loadUserByUsername(request.email());

        String accessToken  = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        log.info("Login successful: {} | Role: {} | Branch: {}",
                principal.getEmail(), principal.getRole(), principal.getBranchId());

        return buildAuthResponse(accessToken, refreshToken, principal);
    }

    // ── Refresh Token ────────────────────────────────────────────────

    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();

        // Validate it's actually a refresh token (not an access token)
        if (!jwtService.isRefreshToken(token)) {
            throw new UnauthorizedException("Invalid refresh token.");
        }

        String email = jwtService.extractEmail(token);
        UserPrincipal principal = (UserPrincipal)
                userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(token, principal)) {
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        String newAccessToken = jwtService.generateAccessToken(principal);

        log.debug("Token refreshed for: {}", email);

        return buildAuthResponse(newAccessToken, token, principal);  // reuse same refresh token
    }

    // ── Change Password ──────────────────────────────────────────────

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
    }

    // ── Private helpers ──────────────────────────────────────────────

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken,
                                            UserPrincipal principal) {
        UserInfo userInfo = new UserInfo(
            principal.getId(),
            loadUserFullName(principal.getId()),
            principal.getEmail(),
            principal.getRole().name(),
            principal.getBranchId(),
            loadBranchName(principal.getBranchId()),
            loadMustChangePassword(principal.getId())
        );

        return AuthResponse.of(accessToken, refreshToken, accessTokenExpiryMs, userInfo);
    }

    private String loadUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFullName)
                .orElse("");
    }

    private String loadBranchName(Long branchId) {
        if (branchId == null) return null;
        return userRepository.findById(branchId)  // reuse repo for now
                .map(u -> u.getBranch() != null ? u.getBranch().getName() : null)
                .orElse(null);
    }

    private boolean loadMustChangePassword(Long userId) {
        return userRepository.findById(userId)
                .map(u -> Boolean.TRUE.equals(u.getMustChangePassword()))
                .orElse(false);
    }
}

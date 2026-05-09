package com.pharmacy.pos.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs for the Auth module.
 * Using Java Records for immutability and brevity.
 */
public final class AuthDtos {

    private AuthDtos() {}

    // ── Request: POST /api/v1/auth/login ─────────────────────────────
    public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
    ) {}

    // ── Request: POST /api/v1/auth/refresh ───────────────────────────
    public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
    ) {}

    // ── Request: POST /api/v1/auth/change-password ───────────────────
    public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        String newPassword
    ) {}

    // ── Response: returned after successful login / refresh ──────────
    public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long   expiresInMs,
        UserInfo user
    ) {
        // Factory for consistent construction
        public static AuthResponse of(String access, String refresh,
                                      long expiresInMs, UserInfo user) {
            return new AuthResponse(access, refresh, "Bearer", expiresInMs, user);
        }
    }

    // ── Embedded user info returned with every auth response ─────────
    public record UserInfo(
        Long   id,
        String fullName,
        String email,
        String role,
        Long   branchId,
        String branchName,
        boolean mustChangePassword
    ) {}
}

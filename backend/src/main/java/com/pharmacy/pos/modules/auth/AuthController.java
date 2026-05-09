package com.pharmacy.pos.modules.auth;

import com.pharmacy.pos.common.base.ApiResponse;
import com.pharmacy.pos.common.security.UserPrincipal;
import com.pharmacy.pos.modules.auth.dto.AuthDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 *
 * Public endpoints (no JWT required):
 *   POST /api/v1/auth/login
 *   POST /api/v1/auth/refresh
 *
 * Protected endpoints (JWT required):
 *   POST /api/v1/auth/change-password
 *   GET  /api/v1/auth/me
 *   POST /api/v1/auth/logout
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, token refresh, and password management")
public class AuthController {

    private final AuthService authService;

    // ── POST /api/v1/auth/login ──────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login with email and password",
               description = "Returns access token (15 min) and refresh token (7 days)")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    // ── POST /api/v1/auth/refresh ────────────────────────────────────
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    // ── GET /api/v1/auth/me ──────────────────────────────────────────
    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user's profile",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        // Return info already in the JWT — no DB call needed
        UserInfo userInfo = new UserInfo(
            principal.getId(),
            null,               // full name resolved by AuthService on login
            principal.getEmail(),
            principal.getRole().name(),
            principal.getBranchId(),
            null,
            false
        );

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    // ── POST /api/v1/auth/change-password ───────────────────────────
    @PostMapping("/change-password")
    @Operation(summary = "Change the current user's password",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    // ── POST /api/v1/auth/logout ─────────────────────────────────────
    /**
     * For stateless JWT, logout is handled client-side (discard the tokens).
     * This endpoint exists for future token blacklisting via Redis if needed.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout (client should discard tokens)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Future: add token to Redis blacklist here
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}

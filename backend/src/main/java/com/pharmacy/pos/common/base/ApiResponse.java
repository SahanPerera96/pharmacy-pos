package com.pharmacy.pos.common.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ApiResponse<T>
 *
 * Standardises every REST response body so the frontend always gets
 * a consistent shape:
 *
 *  {
 *    "success": true,
 *    "message": "Sale processed successfully",
 *    "data": { ... },          ← null on errors
 *    "error": null,            ← null on success
 *    "timestamp": "2025-01-01T10:00:00"
 *  }
 *
 * Usage in controllers:
 *   return ResponseEntity.ok(ApiResponse.success("Created", savedDto));
 *   return ResponseEntity.badRequest().body(ApiResponse.error("Invalid input"));
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)   // Don't serialise null fields
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String error;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // ── Factory helpers ──────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Success", data);
    }

    public static <T> ApiResponse<T> error(String errorMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorMessage)
                .build();
    }
}

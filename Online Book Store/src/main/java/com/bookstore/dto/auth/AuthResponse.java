package com.bookstore.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private long expiresAtEpochMs;
    private String role;
}

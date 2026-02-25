package com.bookstore.controller.api;

import com.bookstore.dto.auth.*;
import com.bookstore.security.JwtUtil;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_","")).orElse("USER");
        String token = jwtUtil.generateToken(req.getEmail(), com.bookstore.domain.enums.Role.valueOf(role), expirationMinutes);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .expiresAtEpochMs(jwtUtil.extractExpiresAtEpochMs(token))
                .role(role)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        userService.registerUser(req);
        return ResponseEntity.ok().build();
    }
}

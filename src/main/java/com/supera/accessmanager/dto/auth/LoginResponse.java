package com.supera.accessmanager.dto.auth;

public record LoginResponse(
        String token,
        String tipo,
        Long expiresIn
) {}

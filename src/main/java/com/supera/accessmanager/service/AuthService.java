package com.supera.accessmanager.service;

import com.supera.accessmanager.dto.auth.LoginRequest;
import com.supera.accessmanager.dto.auth.LoginResponse;
import com.supera.accessmanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse autenticar(LoginRequest request) {

        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.senha()
        );

        authenticationManager.authenticate(authToken);

        String token = jwtService.gerarToken(request.email());

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiracao()
        );
    }
}


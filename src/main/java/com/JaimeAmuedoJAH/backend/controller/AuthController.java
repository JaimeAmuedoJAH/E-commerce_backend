package com.JaimeAmuedoJAH.backend.controller;

import com.JaimeAmuedoJAH.backend.dto.RefreshTokenRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.RefreshTokenResponseDTO;
import com.JaimeAmuedoJAH.backend.entity.RefreshTokenEntity;
import com.JaimeAmuedoJAH.backend.security.JwtUtil;
import com.JaimeAmuedoJAH.backend.service.RefreshTokenService;
import com.JaimeAmuedoJAH.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.JaimeAmuedoJAH.backend.security.UsuarioPrincipal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO request) {

        RefreshTokenEntity refreshToken = refreshTokenService.validarRefreshToken(request.getRefreshToken());

        String nuevoAccessToken = jwtUtil.generateToken(refreshToken.getUsuario().getEmail());
        RefreshTokenEntity nuevoRefreshToken = refreshTokenService.crearRefreshToken(refreshToken.getUsuario());

        return ResponseEntity.ok(RefreshTokenResponseDTO.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(nuevoRefreshToken.getToken())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UsuarioPrincipal principal) {
        usuarioService.logout(principal.getPublicId());
        return ResponseEntity.noContent().build();
    }
}
package com.JaimeAmuedoJAH.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioUpdateRequestDTO;
import com.JaimeAmuedoJAH.backend.service.UsuarioService;
import com.JaimeAmuedoJAH.backend.ratelimit.RateLimit;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Validated
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasRole('ADMIN') or #publicId == authentication.principal.publicId")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable String publicId) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(publicId));
    }

    @PostMapping("/register")
    @RateLimit(maxAttempts = 5, windowSizeSeconds = 300) // 5 attempts per 5 minutes
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequest) {
        UsuarioResponseDTO usuario = usuarioService.crearUsuario(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    @RateLimit(maxAttempts = 10, windowSizeSeconds = 300) // 10 attempts per 5 minutes
    public ResponseEntity<UsuarioLoginResponseDTO> login(@Valid @RequestBody UsuarioLoginRequestDTO loginRequest) {
        UsuarioLoginResponseDTO response = usuarioService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{publicId}")
    @PreAuthorize("hasRole('ADMIN') or #publicId == authentication.principal.publicId")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable String publicId,
            @Valid @RequestBody UsuarioUpdateRequestDTO usuarioRequest) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(publicId, usuarioRequest));
    }

    @DeleteMapping("/delete/{publicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String publicId) {
        usuarioService.eliminarUsuario(publicId);
        return ResponseEntity.noContent().build();
    }
}

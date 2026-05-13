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

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Validated
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/all")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
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

    @PutMapping("/update/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequestDTO usuarioRequest) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}

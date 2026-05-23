package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.exceptions.AuthenticationException;
import com.JaimeAmuedoJAH.backend.exceptions.ConflictException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import com.JaimeAmuedoJAH.backend.repository.UsuarioRepository;
import com.JaimeAmuedoJAH.backend.dto.UsuarioRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioUpdateRequestDTO;
import com.JaimeAmuedoJAH.backend.mapping.UsuarioMapping;
import com.JaimeAmuedoJAH.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.JaimeAmuedoJAH.backend.entity.RefreshTokenEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(String publicId) {
        UsuarioEntity usuario = usuarioRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario not found: " + publicId));
    return UsuarioMapping.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe un usuario con este email");
        }

        UsuarioEntity usuario = UsuarioMapping.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Normalizar rol — siempre con ROLE_
        String rol = request.getRol() != null ? request.getRol() : "ROLE_USER";
        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol.toUpperCase();
        }
        usuario.setRol(rol);

        return UsuarioMapping.toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO loginRequest) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new AuthenticationException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());
        RefreshTokenEntity refreshToken = refreshTokenService.crearRefreshToken(usuario);

        return UsuarioMapping.toLoginResponseDTO(usuario, token, refreshToken.getToken());
    }

    public void logout(String publicId) {
        UsuarioEntity usuario = usuarioRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found: " + publicId));
        refreshTokenService.eliminarRefreshToken(usuario);
    }

    public UsuarioResponseDTO actualizarUsuario(String publicId, UsuarioUpdateRequestDTO request) {
        UsuarioEntity usuario = usuarioRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found: " + publicId));

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe un usuario con este email");
        }

        // Si quiere cambiar contraseña, verificar la actual
        if (request.getPassword() != null) {
            if (request.getPasswordActual() == null) {
                throw new AuthenticationException("Debes introducir tu contraseña actual para cambiarla");
            }
            if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
                throw new AuthenticationException("La contraseña actual es incorrecta");
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UsuarioMapping.updateEntity(request, usuario);

        return UsuarioMapping.toResponseDTO(usuarioRepository.save(usuario));
    }

    public void eliminarUsuario(String publicId) {
        UsuarioEntity usuario = usuarioRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario not found: " + publicId));
        usuarioRepository.delete(usuario);
    }
}

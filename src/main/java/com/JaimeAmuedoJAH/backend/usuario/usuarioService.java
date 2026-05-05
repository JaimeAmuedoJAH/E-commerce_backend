package com.JaimeAmuedoJAH.backend.usuario;

import com.JaimeAmuedoJAH.backend.exception.BadRequestException;
import com.JaimeAmuedoJAH.backend.exception.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
        return UsuarioMapping.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con este email");
        }

        UsuarioEntity usuario = UsuarioMapping.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        UsuarioEntity saved = usuarioRepository.save(usuario);
        return UsuarioMapping.toResponseDTO(saved);
    }

    public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO loginRequest) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());
        return UsuarioMapping.toLoginResponseDTO(usuario, token);
    }

    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioUpdateRequestDTO request) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con este email");
        }

        UsuarioMapping.updateEntity(request, usuario);

        if (request.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UsuarioEntity updated = usuarioRepository.save(usuario);
        return UsuarioMapping.toResponseDTO(updated);
    }

    public void eliminarUsuario(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
        usuarioRepository.delete(usuario);
    }
}

package com.JaimeAmuedoJAH.backend.usuario;

import com.JaimeAmuedoJAH.backend.exceptions.AuthenticationException;
import com.JaimeAmuedoJAH.backend.exceptions.ConflictException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.repository.UsuarioRepository;
import com.JaimeAmuedoJAH.backend.service.UsuarioService;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import com.JaimeAmuedoJAH.backend.dto.UsuarioRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginResponseDTO;
import com.JaimeAmuedoJAH.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioEntity usuario;
    private UsuarioRequestDTO usuarioRequestDTO;
    private String publicId;

    @BeforeEach
    void setup() {
        publicId = UUID.randomUUID().toString();

        usuario = UsuarioEntity.builder()
                .publicId(publicId)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .password("hashedPassword123")
                .rol("ROLE_USER")
                .build();

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setNombre("Juan Pérez");
        usuarioRequestDTO.setEmail("juan@example.com");
        usuarioRequestDTO.setPassword("password123");
        usuarioRequestDTO.setRol("ROLE_USER");
    }

    @Test
    void testObtenerTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> result = usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testObtenerUsuarioPorIdExistente() {
        when(usuarioRepository.findByPublicId(publicId)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO result = usuarioService.obtenerUsuarioPorId(publicId);

        assertNotNull(result);
        assertEquals(publicId, result.getPublicId());
        verify(usuarioRepository, times(1)).findByPublicId(publicId);
    }

    @Test
    void testObtenerUsuarioPorIdNoExistente() {
        String idInexistente = UUID.randomUUID().toString();
        when(usuarioRepository.findByPublicId(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                usuarioService.obtenerUsuarioPorId(idInexistente));
    }

    @Test
    void testCrearUsuarioExitoso() {
        when(usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(usuarioRequestDTO.getPassword())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);

        UsuarioResponseDTO result = usuarioService.crearUsuario(usuarioRequestDTO);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void testCrearUsuarioConEmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                usuarioService.crearUsuario(usuarioRequestDTO));
    }

    @Test
    void testLoginExitoso() {
        UsuarioLoginRequestDTO loginRequest = new UsuarioLoginRequestDTO();
        loginRequest.setEmail("juan@example.com");
        loginRequest.setPassword("password123");

        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("juan@example.com")).thenReturn("jwt-token-123");

        UsuarioLoginResponseDTO result = usuarioService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token-123", result.getToken());
        verify(jwtUtil, times(1)).generateToken("juan@example.com");
    }

    @Test
    void testLoginConEmailIncorrecto() {
        UsuarioLoginRequestDTO loginRequest = new UsuarioLoginRequestDTO();
        loginRequest.setEmail("inexistente@example.com");
        loginRequest.setPassword("password123");

        when(usuarioRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () ->
                usuarioService.login(loginRequest));
    }

    @Test
    void testLoginConContraseñaIncorrecta() {
        UsuarioLoginRequestDTO loginRequest = new UsuarioLoginRequestDTO();
        loginRequest.setEmail("juan@example.com");
        loginRequest.setPassword("passwordIncorrecto");

        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("passwordIncorrecto", usuario.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class, () ->
                usuarioService.login(loginRequest));
    }

    @Test
    void testEliminarUsuarioExistente() {
        when(usuarioRepository.findByPublicId(publicId)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(publicId);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void testEliminarUsuarioNoExistente() {
        String idInexistente = UUID.randomUUID().toString();
        when(usuarioRepository.findByPublicId(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                usuarioService.eliminarUsuario(idInexistente));
    }
}
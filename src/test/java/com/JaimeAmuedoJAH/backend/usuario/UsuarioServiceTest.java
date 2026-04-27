package com.JaimeAmuedoJAH.backend.usuario;

import com.JaimeAmuedoJAH.backend.exception.BadRequestException;
import com.JaimeAmuedoJAH.backend.exception.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UsuarioService.
 * Verifica la lógica de negocio de autenticación y gestión de usuarios.
 */
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

    @BeforeEach
    void setup() {
        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setNombre("Juan Pérez");
        usuario.setEmail("juan@example.com");
        usuario.setPassword("hashedPassword123");
        usuario.setRol("ROLE_USER");

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setNombre("Juan Pérez");
        usuarioRequestDTO.setEmail("juan@example.com");
        usuarioRequestDTO.setPassword("password123");
        usuarioRequestDTO.setRol("ROLE_USER");
    }

    /**
     * Test para verificar que se obtienen todos los usuarios
     */
    @Test
    void testObtenerTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> result = usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    /**
     * Test para verificar la obtención de un usuario por ID existente
     */
    @Test
    void testObtenerUsuarioPorIdExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO result = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).findById(1L);
    }

    /**
     * Test para verificar que se lanza excepción cuando usuario no existe
     */
    @Test
    void testObtenerUsuarioPorIdNoExistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obtenerUsuarioPorId(999L);
        });
    }

    /**
     * Test para verificar el registro exitoso de un nuevo usuario
     */
    @Test
    void testCrearUsuarioExitoso() {
        when(usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(usuarioRequestDTO.getPassword())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);

        UsuarioResponseDTO result = usuarioService.crearUsuario(usuarioRequestDTO);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    /**
     * Test para verificar que no se crea usuario si email ya existe
     */
    @Test
    void testCrearUsuarioConEmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            usuarioService.crearUsuario(usuarioRequestDTO);
        });
    }

    /**
     * Test para verificar login exitoso
     */
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

    /**
     * Test para verificar que login falla con email incorrecto
     */
    @Test
    void testLoginConEmailIncorrecto() {
        UsuarioLoginRequestDTO loginRequest = new UsuarioLoginRequestDTO();
        loginRequest.setEmail("inexistente@example.com");
        loginRequest.setPassword("password123");

        when(usuarioRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            usuarioService.login(loginRequest);
        });
    }

    /**
     * Test para verificar que login falla con contraseña incorrecta
     */
    @Test
    void testLoginConContraseñaIncorrecta() {
        UsuarioLoginRequestDTO loginRequest = new UsuarioLoginRequestDTO();
        loginRequest.setEmail("juan@example.com");
        loginRequest.setPassword("passwordIncorrecto");

        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("passwordIncorrecto", usuario.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            usuarioService.login(loginRequest);
        });
    }

    /**
     * Test para verificar la eliminación de un usuario existente
     */
    @Test
    void testEliminarUsuarioExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    /**
     * Test para verificar que no se elimina usuario inexistente
     */
    @Test
    void testEliminarUsuarioNoExistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.eliminarUsuario(999L);
        });
    }
}

package com.JaimeAmuedoJAH.backend.mapping;

import com.JaimeAmuedoJAH.backend.dto.UsuarioLoginResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioResponseDTO;
import com.JaimeAmuedoJAH.backend.dto.UsuarioUpdateRequestDTO;
import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import java.util.UUID;

public class UsuarioMapping {

    public static UsuarioResponseDTO toResponseDTO(UsuarioEntity usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setPublicId(usuario.getPublicId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setImagenPerfil(usuario.getImagenPerfil());
        return dto;
    }

    public static UsuarioEntity toEntity(UsuarioRequestDTO request) {
        if (request == null) {
            return null;
        }

        return UsuarioEntity.builder()
            .publicId(UUID.randomUUID().toString()) // se genera aquí
            .nombre(request.getNombre())
            .email(request.getEmail())
            .rol(request.getRol())
            .build();
    }

    public static UsuarioLoginResponseDTO toLoginResponseDTO(UsuarioEntity usuario, String token, String refreshToken) {
        if (usuario == null || token == null) return null;

        UsuarioLoginResponseDTO response = new UsuarioLoginResponseDTO();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUsuario(toResponseDTO(usuario));
        return response;
    }

    public static void updateEntity(UsuarioUpdateRequestDTO request, UsuarioEntity usuario) {
        if (request == null || usuario == null) {
            return;
        }

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        
        if (request.getEmail() != null) {
            usuario.setEmail(request.getEmail());
        }

        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }

        if (request.getImagenPerfil() != null) {
            usuario.setImagenPerfil(request.getImagenPerfil());
        }
    }
}

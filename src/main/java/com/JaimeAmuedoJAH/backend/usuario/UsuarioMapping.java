package com.JaimeAmuedoJAH.backend.usuario;

public class UsuarioMapping {

    public static UsuarioResponseDTO toResponseDTO(UsuarioEntity usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        return response;
    }

    public static UsuarioEntity toEntity(UsuarioRequestDTO request) {
        if (request == null) {
            return null;
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(request.getPassword())
                .rol(request.getRol() != null ? request.getRol() : "USER")
                .build();
        return usuario;
    }

    public static UsuarioLoginResponseDTO toLoginResponseDTO(UsuarioEntity usuario, String token) {
        if (usuario == null || token == null) {
            return null;
        }

        UsuarioLoginResponseDTO response = new UsuarioLoginResponseDTO();
        response.setToken(token);
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
        if (request.getPassword() != null) {
            usuario.setPassword(request.getPassword());
        }
        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }
    }
}

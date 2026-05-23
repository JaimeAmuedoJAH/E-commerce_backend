package com.JaimeAmuedoJAH.backend.security;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String publicId;
    private final String email;
    private final String password;
    private final String rol;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioPrincipal(UsuarioEntity usuario) {
        this.id = usuario.getId();
        this.publicId = usuario.getPublicId();
        this.email = usuario.getEmail();
        this.password = usuario.getPassword();
        this.rol = usuario.getRol();
        this.authorities = List.of(new SimpleGrantedAuthority(usuario.getRol()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Spring usa este como nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
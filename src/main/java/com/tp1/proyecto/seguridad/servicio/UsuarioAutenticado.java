package com.tp1.proyecto.seguridad.servicio;

import com.tp1.proyecto.usuario.entidad.Usuario;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioAutenticado implements UserDetails {

    private final Usuario usuario;
    private final List<SimpleGrantedAuthority> authorities;

    public UsuarioAutenticado(Usuario usuario) {
        this.usuario = usuario;
        this.authorities = Stream.concat(
                usuario.getRoles().stream().map(rol -> rol.getNombre()),
                usuario.getRoles().stream()
                    .map(rol -> rol.getNombre())
                    .filter("DIRECTOR_ACADEMICO"::equals)
                    .map(rol -> "ADMIN")
            )
            .distinct()
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
            .collect(Collectors.toList());
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    public String getCorreo() {
        return usuario.getCorreo();
    }

    public List<String> getRoles() {
        return usuario.getRoles().stream()
            .map(rol -> rol.getNombre())
            .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

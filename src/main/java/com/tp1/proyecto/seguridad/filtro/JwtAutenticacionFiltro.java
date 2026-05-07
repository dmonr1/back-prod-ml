package com.tp1.proyecto.seguridad.filtro;

import com.tp1.proyecto.seguridad.servicio.JwtServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import com.tp1.proyecto.seguridad.servicio.UsuarioDetalleServicio;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAutenticacionFiltro extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;
    private final UsuarioDetalleServicio usuarioDetalleServicio;

    public JwtAutenticacionFiltro(JwtServicio jwtServicio, UsuarioDetalleServicio usuarioDetalleServicio) {
        this.jwtServicio = jwtServicio;
        this.usuarioDetalleServicio = usuarioDetalleServicio;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            String username = jwtServicio.extraerUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsuarioAutenticado usuario = (UsuarioAutenticado) usuarioDetalleServicio.loadUserByUsername(username);
                if (jwtServicio.esTokenValido(jwt, usuario)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

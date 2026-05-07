package com.tp1.proyecto.seguridad.servicio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServicio {

    private final String jwtSecret;
    private final long jwtExpiracionSegundos;

    public JwtServicio(
        @Value("${app.security.jwt.secret}") String jwtSecret,
        @Value("${app.security.jwt.expiration-seconds}") long jwtExpiracionSegundos
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiracionSegundos = jwtExpiracionSegundos;
    }

    public String generarToken(UsuarioAutenticado usuario) {
        Instant now = Instant.now();
        Instant expiracion = now.plusSeconds(jwtExpiracionSegundos);

        return Jwts.builder()
            .setClaims(Map.of(
                "roles", usuario.getRoles(),
                "correo", usuario.getCorreo(),
                "usuarioId", usuario.getUsuario().getId()
            ))
            .setSubject(usuario.getUsername())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiracion))
            .signWith(getSignInKey())
            .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, UsuarioAutenticado usuario) {
        String username = extraerUsername(token);
        return username.equals(usuario.getUsername()) && !estaExpirado(token);
    }

    public long getJwtExpiracionSegundos() {
        return jwtExpiracionSegundos;
    }

    private boolean estaExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

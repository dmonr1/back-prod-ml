package com.tp1.proyecto.seguridad.controlador;

import com.tp1.proyecto.seguridad.dto.LoginRespuestaDto;
import com.tp1.proyecto.seguridad.dto.LoginSolicitudDto;
import com.tp1.proyecto.seguridad.dto.UsuarioSesionDto;
import com.tp1.proyecto.seguridad.servicio.AuthServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final AuthServicio authServicio;

    public AuthControlador(AuthServicio authServicio) {
        this.authServicio = authServicio;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginRespuestaDto login(@Valid @RequestBody LoginSolicitudDto solicitud) {
        return authServicio.login(solicitud);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UsuarioSesionDto me(@AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        return authServicio.obtenerSesionActual(usuarioAutenticado);
    }
}

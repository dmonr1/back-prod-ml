package com.tp1.proyecto.usuario.controlador;

import com.tp1.proyecto.usuario.dto.UsuarioActualizacionSolicitudDto;
import com.tp1.proyecto.usuario.dto.UsuarioGestionRespuestaDto;
import com.tp1.proyecto.usuario.servicio.UsuarioServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public List<UsuarioGestionRespuestaDto> listar() {
        return usuarioServicio.listar();
    }

    @PutMapping("/{usuarioId}")
    public UsuarioGestionRespuestaDto actualizar(
        @PathVariable Long usuarioId,
        @RequestBody UsuarioActualizacionSolicitudDto solicitud
    ) {
        return usuarioServicio.actualizar(usuarioId, solicitud);
    }

    @PatchMapping("/{usuarioId}/estado")
    public UsuarioGestionRespuestaDto actualizarEstado(
        @PathVariable Long usuarioId,
        @RequestParam boolean activo
    ) {
        return usuarioServicio.actualizarEstado(usuarioId, activo);
    }
}

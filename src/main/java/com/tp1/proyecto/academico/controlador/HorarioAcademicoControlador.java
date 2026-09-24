package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.BloqueHorarioRespuestaDto;
import com.tp1.proyecto.academico.dto.BloqueHorarioSolicitudDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalRespuestaDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalSolicitudDto;
import com.tp1.proyecto.academico.servicio.HorarioAcademicoServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horarios")
public class HorarioAcademicoControlador {
    private final HorarioAcademicoServicio servicio;

    public HorarioAcademicoControlador(HorarioAcademicoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/bloques")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<BloqueHorarioRespuestaDto> listarBloques(
        @RequestParam Long periodoAcademicoId,
        @RequestParam Long nivelId
    ) {
        return servicio.listarBloques(periodoAcademicoId, nivelId);
    }

    @PostMapping("/bloques")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BloqueHorarioRespuestaDto crearBloque(@Valid @RequestBody BloqueHorarioSolicitudDto solicitud) {
        return servicio.crearBloque(solicitud);
    }

    @PutMapping("/bloques/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BloqueHorarioRespuestaDto actualizarBloque(
        @PathVariable Long id,
        @Valid @RequestBody BloqueHorarioSolicitudDto solicitud
    ) {
        return servicio.actualizarBloque(id, solicitud);
    }

    @PatchMapping("/bloques/{id}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void actualizarEstadoBloque(@PathVariable Long id, @RequestParam boolean activo) {
        servicio.actualizarEstadoBloque(id, activo);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<HorarioSemanalRespuestaDto> listarPorPeriodo(@RequestParam Long periodoAcademicoId) {
        return servicio.listarPorPeriodo(periodoAcademicoId);
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAnyRole('DOCENTE','DOCENTE_TUTOR')")
    public List<HorarioSemanalRespuestaDto> listarMios(
        @RequestParam Long periodoAcademicoId,
        @AuthenticationPrincipal UsuarioAutenticado principal
    ) {
        return servicio.listarMios(periodoAcademicoId, principal.getUsuario().getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public HorarioSemanalRespuestaDto crear(@Valid @RequestBody HorarioSemanalSolicitudDto solicitud) {
        return servicio.crearHorario(solicitud);
    }

    @PatchMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void actualizarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        servicio.actualizarEstadoHorario(id, activo);
    }
}

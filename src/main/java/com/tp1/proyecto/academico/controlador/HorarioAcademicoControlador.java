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
import org.springframework.security.core.GrantedAuthority;

@RestController
@RequestMapping("/api/horarios")
public class HorarioAcademicoControlador {
    private final HorarioAcademicoServicio servicio;

    public HorarioAcademicoControlador(HorarioAcademicoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/bloques")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
    public List<BloqueHorarioRespuestaDto> listarBloques(
        @RequestParam Long periodoAcademicoId,
        @RequestParam(required = false) Long nivelId
    ) {
        return servicio.listarBloques(periodoAcademicoId, nivelId);
    }

    @GetMapping("/bloques/recreos")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
    public List<BloqueHorarioRespuestaDto> listarRecreos(@RequestParam Long periodoAcademicoId) {
        return servicio.listarRecreos(periodoAcademicoId);
    }

    @GetMapping("/bloques/no-lectivos")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
    public List<BloqueHorarioRespuestaDto> listarBloquesNoLectivos(@RequestParam Long periodoAcademicoId) {
        return servicio.listarBloquesNoLectivos(periodoAcademicoId);
    }

    @PostMapping("/bloques")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public BloqueHorarioRespuestaDto crearBloque(@Valid @RequestBody BloqueHorarioSolicitudDto solicitud) {
        return servicio.crearBloque(solicitud);
    }

    @PutMapping("/bloques/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public BloqueHorarioRespuestaDto actualizarBloque(
        @PathVariable Long id,
        @Valid @RequestBody BloqueHorarioSolicitudDto solicitud
    ) {
        return servicio.actualizarBloque(id, solicitud);
    }

    @PatchMapping("/bloques/{id}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public void actualizarEstadoBloque(@PathVariable Long id, @RequestParam boolean activo) {
        servicio.actualizarEstadoBloque(id, activo);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public List<HorarioSemanalRespuestaDto> listarPorPeriodo(@RequestParam Long periodoAcademicoId) {
        return servicio.listarPorPeriodo(periodoAcademicoId);
    }

    @GetMapping("/pendientes-reprogramacion")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public List<HorarioSemanalRespuestaDto> listarPendientesReprogramacion(@RequestParam Long periodoAcademicoId) {
        return servicio.listarPendientesReprogramacion(periodoAcademicoId);
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAnyRole('DOCENTE','DOCENTE_TUTOR')")
    public List<HorarioSemanalRespuestaDto> listarMios(
        @RequestParam Long periodoAcademicoId,
        @AuthenticationPrincipal UsuarioAutenticado principal
    ) {
        return servicio.listarMios(periodoAcademicoId, principal.getUsuario().getId());
    }

    @GetMapping("/seccion/{seccionId}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE_TUTOR')")
    public List<HorarioSemanalRespuestaDto> listarPorSeccion(
        @PathVariable Long seccionId,
        @RequestParam Long periodoAcademicoId,
        @AuthenticationPrincipal UsuarioAutenticado principal
    ) {
        boolean accesoInstitucional = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("ROLE_DIRECTOR_ACADEMICO"));
        return servicio.listarPorSeccion(
            seccionId,
            periodoAcademicoId,
            principal.getUsuario().getId(),
            accesoInstitucional
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public HorarioSemanalRespuestaDto crear(@Valid @RequestBody HorarioSemanalSolicitudDto solicitud) {
        return servicio.crearHorario(solicitud);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public HorarioSemanalRespuestaDto actualizar(
        @PathVariable Long id,
        @Valid @RequestBody HorarioSemanalSolicitudDto solicitud
    ) {
        return servicio.actualizarHorario(id, solicitud);
    }

    @PatchMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public void actualizarEstado(
        @PathVariable Long id,
        @RequestParam boolean activo,
        @RequestParam(defaultValue = "false") boolean devolverAPendientes
    ) {
        servicio.actualizarEstadoHorario(id, activo, devolverAPendientes);
    }
}

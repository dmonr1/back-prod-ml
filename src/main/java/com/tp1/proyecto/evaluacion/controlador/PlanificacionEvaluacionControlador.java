package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.PlanificacionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.servicio.impl.PlanificacionEvaluacionServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluaciones/planificacion")
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
public class PlanificacionEvaluacionControlador {

    private final PlanificacionEvaluacionServicio servicio;

    public PlanificacionEvaluacionControlador(PlanificacionEvaluacionServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/tipos")
    public List<TipoEvaluacionRespuestaDto> listarTipos(
        @RequestParam Long asignacionId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return servicio.listarTipos(asignacionId, actor);
    }

    @PostMapping("/tipos")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoEvaluacionRespuestaDto crearTipo(
        @Valid @RequestBody PlanificacionEvaluacionSolicitudDto.NuevoTipo solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return servicio.crearTipo(solicitud, actor);
    }

    @PostMapping("/agregar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void agregar(
        @Valid @RequestBody PlanificacionEvaluacionSolicitudDto.Agregar solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        servicio.agregar(solicitud, actor);
    }

    @PutMapping("/cantidad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarCantidad(
        @Valid @RequestBody PlanificacionEvaluacionSolicitudDto.Cantidad solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        servicio.cambiarCantidad(solicitud, actor);
    }

    @DeleteMapping("/{evaluacionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitar(
        @PathVariable Long evaluacionId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        servicio.quitar(evaluacionId, actor);
    }
}

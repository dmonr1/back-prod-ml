package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ActualizarFechaEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroNotasEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.EvaluacionServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluaciones")
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
public class EvaluacionControlador {

    private final EvaluacionServicio evaluacionServicio;

    public EvaluacionControlador(EvaluacionServicio evaluacionServicio) {
        this.evaluacionServicio = evaluacionServicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluacionRespuestaDto crear(
        @Valid @RequestBody EvaluacionSolicitudDto solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return evaluacionServicio.crear(solicitud, actor);
    }

    @PatchMapping("/{evaluacionId}/fecha")
    public EvaluacionRespuestaDto actualizarFecha(
        @PathVariable Long evaluacionId,
        @Valid @RequestBody ActualizarFechaEvaluacionSolicitudDto solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return evaluacionServicio.actualizarFecha(evaluacionId, solicitud, actor);
    }

    @GetMapping
    public List<EvaluacionRespuestaDto> listar(
        @RequestParam Long docenteCursoSeccionId,
        @RequestParam Long periodoEvaluacionId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return evaluacionServicio.listarPorAsignacionYPeriodoEvaluacion(docenteCursoSeccionId, periodoEvaluacionId, actor);
    }

    @PostMapping("/{evaluacionId}/notas")
    public List<DetalleNotaEvaluacionRespuestaDto> registrarNotas(
        @PathVariable Long evaluacionId,
        @Valid @RequestBody RegistroNotasEvaluacionSolicitudDto solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return evaluacionServicio.registrarNotas(evaluacionId, solicitud, actor);
    }

    @GetMapping("/{evaluacionId}/notas")
    public List<DetalleNotaEvaluacionRespuestaDto> listarNotas(
        @PathVariable Long evaluacionId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return evaluacionServicio.listarNotasPorEvaluacion(evaluacionId, actor);
    }
}

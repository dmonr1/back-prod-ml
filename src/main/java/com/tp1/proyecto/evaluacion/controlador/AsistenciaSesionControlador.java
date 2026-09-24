package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciaSesionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaSesionServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asistencias-sesiones")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class AsistenciaSesionControlador {

    private final AsistenciaSesionServicio asistenciaSesionServicio;

    public AsistenciaSesionControlador(AsistenciaSesionServicio asistenciaSesionServicio) {
        this.asistenciaSesionServicio = asistenciaSesionServicio;
    }

    @GetMapping
    public List<AsistenciaSesionRespuestaDto> listar(
        @RequestParam Long docenteCursoSeccionId,
        @RequestParam Long periodoEvaluacionId,
        @RequestParam LocalDate fecha,
        @RequestParam(required = false) Long horarioSemanalId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return asistenciaSesionServicio.listar(docenteCursoSeccionId, periodoEvaluacionId, fecha, horarioSemanalId, actor);
    }

    @PostMapping
    public List<AsistenciaSesionRespuestaDto> registrar(
        @Valid @RequestBody RegistroAsistenciaSesionSolicitudDto solicitud,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return asistenciaSesionServicio.registrar(solicitud, actor);
    }
}

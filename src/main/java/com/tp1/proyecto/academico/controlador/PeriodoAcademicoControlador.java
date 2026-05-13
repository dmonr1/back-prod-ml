package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.servicio.PeriodoAcademicoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/periodos-academicos")
public class PeriodoAcademicoControlador {

    private final PeriodoAcademicoServicio periodoAcademicoServicio;

    public PeriodoAcademicoControlador(PeriodoAcademicoServicio periodoAcademicoServicio) {
        this.periodoAcademicoServicio = periodoAcademicoServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<PeriodoAcademicoRespuestaDto> listar() {
        return periodoAcademicoServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PeriodoAcademicoRespuestaDto crear(@Valid @RequestBody PeriodoAcademicoSolicitudDto solicitud) {
        return periodoAcademicoServicio.crear(solicitud);
    }

    @PostMapping("/con-periodos-evaluacion")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PeriodoAcademicoConPeriodosRespuestaDto crearConPeriodosEvaluacion(
        @Valid @RequestBody PeriodoAcademicoConPeriodosSolicitudDto solicitud
    ) {
        return periodoAcademicoServicio.crearConPeriodosEvaluacion(solicitud);
    }
}

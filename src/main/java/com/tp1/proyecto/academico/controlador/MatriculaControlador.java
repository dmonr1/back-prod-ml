package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.MatriculaRespuestaDto;
import com.tp1.proyecto.academico.dto.MatriculaSolicitudDto;
import com.tp1.proyecto.academico.servicio.MatriculaServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaControlador {

    private final MatriculaServicio matriculaServicio;

    public MatriculaControlador(MatriculaServicio matriculaServicio) {
        this.matriculaServicio = matriculaServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<MatriculaRespuestaDto> listar(
        @RequestParam Long periodoAcademicoId,
        @RequestParam(required = false) Long seccionId
    ) {
        return matriculaServicio.listar(periodoAcademicoId, seccionId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MatriculaRespuestaDto crear(@RequestBody MatriculaSolicitudDto solicitud) {
        return matriculaServicio.crear(solicitud);
    }
}

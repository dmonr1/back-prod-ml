package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.servicio.PeriodoAcademicoServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}

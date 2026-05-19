package com.tp1.proyecto.alerta.controlador;

import com.tp1.proyecto.alerta.dto.HallazgoDataMiningRespuestaDto;
import com.tp1.proyecto.alerta.servicio.HallazgoDataMiningServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hallazgos")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class HallazgoDataMiningControlador {

    private final HallazgoDataMiningServicio hallazgoDataMiningServicio;

    public HallazgoDataMiningControlador(HallazgoDataMiningServicio hallazgoDataMiningServicio) {
        this.hallazgoDataMiningServicio = hallazgoDataMiningServicio;
    }

    @GetMapping
    public List<HallazgoDataMiningRespuestaDto> listar(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        return hallazgoDataMiningServicio.listarHallazgos(periodoEvaluacionId, seccionId);
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE_TUTOR')")
    public List<HallazgoDataMiningRespuestaDto> generar(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        return hallazgoDataMiningServicio.generarHallazgos(periodoEvaluacionId, seccionId);
    }
}

package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.AsistenciaBimestreRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasBimestreSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaBimestreServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asistencias-bimestre")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class AsistenciaBimestreControlador {

    private final AsistenciaBimestreServicio asistenciaBimestreServicio;

    public AsistenciaBimestreControlador(AsistenciaBimestreServicio asistenciaBimestreServicio) {
        this.asistenciaBimestreServicio = asistenciaBimestreServicio;
    }

    @PostMapping("/{bimestreId}")
    public List<AsistenciaBimestreRespuestaDto> registrar(
        @PathVariable Long bimestreId,
        @Valid @RequestBody RegistroAsistenciasBimestreSolicitudDto solicitud
    ) {
        return asistenciaBimestreServicio.registrarAsistencias(bimestreId, solicitud);
    }

    @GetMapping
    public List<AsistenciaBimestreRespuestaDto> listar(
        @RequestParam Long seccionId,
        @RequestParam Long periodoAcademicoId,
        @RequestParam Long bimestreId
    ) {
        return asistenciaBimestreServicio.listarPorSeccionYBimestre(seccionId, periodoAcademicoId, bimestreId);
    }
}

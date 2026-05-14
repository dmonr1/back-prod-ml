package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAnteriorSolicitudDto;
import com.tp1.proyecto.academico.servicio.CursoPeriodoAcademicoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos-periodo-academico")
public class CursoPeriodoAcademicoControlador {

    private final CursoPeriodoAcademicoServicio cursoPeriodoAcademicoServicio;

    public CursoPeriodoAcademicoControlador(CursoPeriodoAcademicoServicio cursoPeriodoAcademicoServicio) {
        this.cursoPeriodoAcademicoServicio = cursoPeriodoAcademicoServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<CursoPeriodoAcademicoRespuestaDto> listar(@RequestParam(required = false) Long periodoAcademicoId) {
        return cursoPeriodoAcademicoServicio.listar(periodoAcademicoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CursoPeriodoAcademicoRespuestaDto crear(@Valid @RequestBody CursoPeriodoAcademicoSolicitudDto solicitud) {
        return cursoPeriodoAcademicoServicio.crear(solicitud);
    }

    @PostMapping("/copiar-periodo-anterior")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public List<CursoPeriodoAcademicoRespuestaDto> copiarPeriodoAnterior(
        @Valid @RequestBody CursoPeriodoAnteriorSolicitudDto solicitud
    ) {
        return cursoPeriodoAcademicoServicio.copiarDesdePeriodoAnterior(solicitud);
    }

    @PatchMapping("/{cursoPeriodoAcademicoId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public CursoPeriodoAcademicoRespuestaDto actualizarEstado(
        @PathVariable Long cursoPeriodoAcademicoId,
        @RequestParam boolean activo
    ) {
        return cursoPeriodoAcademicoServicio.actualizarEstado(cursoPeriodoAcademicoId, activo);
    }
}

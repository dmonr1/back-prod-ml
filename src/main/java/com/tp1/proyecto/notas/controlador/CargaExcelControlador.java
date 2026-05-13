package com.tp1.proyecto.notas.controlador;

import com.tp1.proyecto.notas.dto.CargaExcelRespuestaDto;
import com.tp1.proyecto.notas.servicio.CargaExcelServicio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Flujo secundario de carga masiva por Excel.
 * El flujo principal del sistema se basa en evaluaciones parciales registradas
 * desde el modulo configurable de evaluacion.
 */
@Deprecated(since = "v2", forRemoval = false)
@RestController
@RequestMapping("/api/cargas-excel")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class CargaExcelControlador {

    private final CargaExcelServicio cargaExcelServicio;

    public CargaExcelControlador(CargaExcelServicio cargaExcelServicio) {
        this.cargaExcelServicio = cargaExcelServicio;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CargaExcelRespuestaDto registrarCarga(
        @RequestParam Long docenteId,
        @RequestParam Long periodoAcademicoId,
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId,
        @RequestParam("archivo") MultipartFile archivo
    ) {
        return cargaExcelServicio.registrarCarga(docenteId, periodoAcademicoId, periodoEvaluacionId, seccionId, archivo);
    }

    @GetMapping("/docente/{docenteId}")
    public List<CargaExcelRespuestaDto> listarPorDocente(@PathVariable Long docenteId) {
        return cargaExcelServicio.listarPorDocente(docenteId);
    }
}

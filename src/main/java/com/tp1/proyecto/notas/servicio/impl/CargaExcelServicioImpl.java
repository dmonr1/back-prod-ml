package com.tp1.proyecto.notas.servicio.impl;

import com.tp1.proyecto.academico.entidad.Bimestre;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.BimestreRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.notas.dto.CargaExcelRespuestaDto;
import com.tp1.proyecto.notas.entidad.CargaExcel;
import com.tp1.proyecto.notas.repositorio.CargaExcelRepositorio;
import com.tp1.proyecto.notas.servicio.CargaExcelServicio;
import com.tp1.proyecto.notas.servicio.ProcesadorExcelServicio;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementacion de compatibilidad para carga masiva por Excel.
 * Se mantiene como flujo opcional mientras el sistema migra al registro
 * directo de evaluaciones parciales desde la interfaz web.
 */
@Deprecated(since = "v2", forRemoval = false)
@Service
@Transactional
public class CargaExcelServicioImpl implements CargaExcelServicio {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private final CargaExcelRepositorio cargaExcelRepositorio;
    private final DocenteRepositorio docenteRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final BimestreRepositorio bimestreRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final ProcesadorExcelServicio procesadorExcelServicio;
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public CargaExcelServicioImpl(
        CargaExcelRepositorio cargaExcelRepositorio,
        DocenteRepositorio docenteRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        BimestreRepositorio bimestreRepositorio,
        SeccionRepositorio seccionRepositorio,
        ProcesadorExcelServicio procesadorExcelServicio,
        PrediccionRiesgoServicio prediccionRiesgoServicio
    ) {
        this.cargaExcelRepositorio = cargaExcelRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.bimestreRepositorio = bimestreRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.procesadorExcelServicio = procesadorExcelServicio;
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @Override
    public CargaExcelRespuestaDto registrarCarga(
        Long docenteId,
        Long periodoAcademicoId,
        Long bimestreId,
        Long seccionId,
        MultipartFile archivo
    ) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar un archivo Excel");
        }

        validarExtensionExcel(archivo.getOriginalFilename());

        Docente docente = docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));

        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId));

        Bimestre bimestre = bimestreRepositorio.findById(bimestreId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Bimestre no encontrado con id: " + bimestreId));

        Seccion seccion = seccionRepositorio.findById(seccionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + seccionId));

        validarConsistenciaPeriodoBimestre(periodoAcademico, bimestre);

        CargaExcel cargaExcel = new CargaExcel();
        cargaExcel.setDocente(docente);
        cargaExcel.setPeriodoAcademico(periodoAcademico);
        cargaExcel.setBimestre(bimestre);
        cargaExcel.setSeccion(seccion);
        cargaExcel.setNombreArchivo(archivo.getOriginalFilename());
        cargaExcel.setTotalFilas(0);
        cargaExcel.setFilasValidas(0);
        cargaExcel.setFilasError(0);
        cargaExcel.setEstadoProceso(ESTADO_PENDIENTE);
        cargaExcel.setObservacion("Archivo recibido. Pendiente de procesamiento.");
        cargaExcel.setFechaCarga(LocalDateTime.now());

        CargaExcel cargaGuardada = cargaExcelRepositorio.save(cargaExcel);
        try {
            procesadorExcelServicio.procesar(cargaGuardada, archivo);
            // Compatibilidad temporal: la carga por Excel sigue generando predicciones
            // mientras el flujo principal usa evaluaciones y asistencias bimestrales.
            prediccionRiesgoServicio.generarPrediccionesGlobales(cargaGuardada);
        } catch (IOException ex) {
            throw new ReglaNegocioException("No se pudo leer el archivo Excel: " + ex.getMessage());
        }

        CargaExcel cargaActualizada = cargaExcelRepositorio.findById(cargaGuardada.getId())
            .orElse(cargaGuardada);
        return mapearRespuesta(cargaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CargaExcelRespuestaDto> listarPorDocente(Long docenteId) {
        docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));

        return cargaExcelRepositorio.findByDocenteIdOrderByFechaCargaDesc(docenteId)
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    private void validarExtensionExcel(String nombreArchivo) {
        if (nombreArchivo == null) {
            throw new ReglaNegocioException("El archivo debe tener un nombre valido");
        }

        String nombre = nombreArchivo.toLowerCase();
        if (!nombre.endsWith(".xlsx") && !nombre.endsWith(".xls")) {
            throw new ReglaNegocioException("Solo se permiten archivos Excel con extension .xlsx o .xls");
        }
    }

    private void validarConsistenciaPeriodoBimestre(PeriodoAcademico periodoAcademico, Bimestre bimestre) {
        if (bimestre.getPeriodoAcademico() == null || !bimestre.getPeriodoAcademico().getId().equals(periodoAcademico.getId())) {
            throw new ReglaNegocioException("El bimestre no pertenece al periodo academico seleccionado");
        }
    }

    private CargaExcelRespuestaDto mapearRespuesta(CargaExcel cargaExcel) {
        CargaExcelRespuestaDto dto = new CargaExcelRespuestaDto();
        dto.setId(cargaExcel.getId());
        if (cargaExcel.getDocente() != null) {
            dto.setDocenteId(cargaExcel.getDocente().getId());
            dto.setDocenteNombreCompleto(
                cargaExcel.getDocente().getNombres() + " " + cargaExcel.getDocente().getApellidos()
            );
        }
        if (cargaExcel.getPeriodoAcademico() != null) {
            dto.setPeriodoAcademicoId(cargaExcel.getPeriodoAcademico().getId());
        }
        if (cargaExcel.getBimestre() != null) {
            dto.setBimestreId(cargaExcel.getBimestre().getId());
        }
        if (cargaExcel.getSeccion() != null) {
            dto.setSeccionId(cargaExcel.getSeccion().getId());
        }
        dto.setNombreArchivo(cargaExcel.getNombreArchivo());
        dto.setTotalFilas(cargaExcel.getTotalFilas());
        dto.setFilasValidas(cargaExcel.getFilasValidas());
        dto.setFilasError(cargaExcel.getFilasError());
        dto.setEstadoProceso(cargaExcel.getEstadoProceso());
        dto.setObservacion(cargaExcel.getObservacion());
        dto.setFechaCarga(cargaExcel.getFechaCarga());
        return dto;
    }
}

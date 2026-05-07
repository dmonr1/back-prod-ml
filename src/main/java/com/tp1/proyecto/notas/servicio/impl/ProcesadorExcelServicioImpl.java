package com.tp1.proyecto.notas.servicio.impl;

import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.notas.dto.ResultadoProcesamientoFilaDto;
import com.tp1.proyecto.notas.entidad.Asistencia;
import com.tp1.proyecto.notas.entidad.CargaExcel;
import com.tp1.proyecto.notas.entidad.Nota;
import com.tp1.proyecto.notas.repositorio.AsistenciaRepositorio;
import com.tp1.proyecto.notas.repositorio.CargaExcelRepositorio;
import com.tp1.proyecto.notas.repositorio.NotaRepositorio;
import com.tp1.proyecto.notas.servicio.ProcesadorExcelServicio;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Procesador legado de Excel para importacion masiva.
 * Se conserva como apoyo operativo, no como flujo principal del sistema.
 */
@Deprecated(since = "v2", forRemoval = false)
@Service
@Transactional
public class ProcesadorExcelServicioImpl implements ProcesadorExcelServicio {

    private static final int LONGITUD_MAXIMA_OBSERVACION = 255;
    private static final String ESTADO_PROCESANDO = "PROCESANDO";
    private static final String ESTADO_PROCESADO = "PROCESADO";
    private static final String ESTADO_ERROR = "ERROR";

    private final CargaExcelRepositorio cargaExcelRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final NotaRepositorio notaRepositorio;
    private final AsistenciaRepositorio asistenciaRepositorio;

    public ProcesadorExcelServicioImpl(
        CargaExcelRepositorio cargaExcelRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        CursoRepositorio cursoRepositorio,
        NotaRepositorio notaRepositorio,
        AsistenciaRepositorio asistenciaRepositorio
    ) {
        this.cargaExcelRepositorio = cargaExcelRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.notaRepositorio = notaRepositorio;
        this.asistenciaRepositorio = asistenciaRepositorio;
    }

    @Override
    public void procesar(CargaExcel cargaExcel, MultipartFile archivo) throws IOException {
        cargaExcel.setEstadoProceso(ESTADO_PROCESANDO);
        cargaExcel.setObservacion("Procesando archivo Excel");
        cargaExcelRepositorio.save(cargaExcel);

        List<ResultadoProcesamientoFilaDto> resultados = new ArrayList<>();

        try (InputStream inputStream = archivo.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            validarCabecera(sheet.getRow(0));

            Iterator<Row> filas = sheet.rowIterator();
            if (filas.hasNext()) {
                filas.next();
            }

            int totalFilas = 0;
            int filasValidas = 0;
            int filasError = 0;

            while (filas.hasNext()) {
                Row fila = filas.next();
                if (filaVacia(fila)) {
                    continue;
                }

                totalFilas++;
                ResultadoProcesamientoFilaDto resultado = procesarFila(cargaExcel, fila);
                resultados.add(resultado);

                if (resultado.isValida()) {
                    filasValidas++;
                } else {
                    filasError++;
                }
            }

            cargaExcel.setTotalFilas(totalFilas);
            cargaExcel.setFilasValidas(filasValidas);
            cargaExcel.setFilasError(filasError);
            cargaExcel.setEstadoProceso(filasError > 0 ? ESTADO_PROCESADO : ESTADO_PROCESADO);
            cargaExcel.setObservacion(construirObservacion(resultados, filasError));
            cargaExcelRepositorio.save(cargaExcel);
        } catch (IOException | RuntimeException ex) {
            cargaExcel.setEstadoProceso(ESTADO_ERROR);
            cargaExcel.setObservacion("Error al procesar archivo: " + ex.getMessage());
            cargaExcelRepositorio.save(cargaExcel);
            throw ex;
        }
    }

    private ResultadoProcesamientoFilaDto procesarFila(CargaExcel cargaExcel, Row fila) {
        int numeroFila = fila.getRowNum() + 1;

        try {
            DataFormatter formatter = new DataFormatter(Locale.US);
            String codigoAlumno = leerTexto(fila.getCell(0), formatter);
            String nombreCurso = leerTexto(fila.getCell(1), formatter);
            BigDecimal notaValor = leerDecimal(fila.getCell(2), formatter);
            Integer clasesProgramadas = leerEntero(fila.getCell(3), formatter);
            Integer clasesAsistidas = leerEntero(fila.getCell(4), formatter);

            if (codigoAlumno == null || codigoAlumno.isBlank()) {
                throw new ReglaNegocioException("Codigo de alumno vacio");
            }
            if (nombreCurso == null || nombreCurso.isBlank()) {
                throw new ReglaNegocioException("Curso vacio");
            }
            if (notaValor.compareTo(BigDecimal.ZERO) < 0 || notaValor.compareTo(BigDecimal.valueOf(20)) > 0) {
                throw new ReglaNegocioException("La nota debe estar entre 0 y 20");
            }
            if (clasesProgramadas < 0 || clasesAsistidas < 0 || clasesAsistidas > clasesProgramadas) {
                throw new ReglaNegocioException("Asistencia invalida");
            }

            Matricula matricula = obtenerMatricula(cargaExcel, codigoAlumno.trim().toUpperCase());
            Curso curso = obtenerCurso(matricula, nombreCurso.trim().toUpperCase());

            guardarNota(cargaExcel, matricula, curso, notaValor);
            guardarAsistencia(cargaExcel, matricula, clasesProgramadas, clasesAsistidas);

            return new ResultadoProcesamientoFilaDto(numeroFila, true, "OK");
        } catch (Exception ex) {
            return new ResultadoProcesamientoFilaDto(numeroFila, false, ex.getMessage());
        }
    }

    private void guardarNota(CargaExcel cargaExcel, Matricula matricula, Curso curso, BigDecimal valorNota) {
        List<Nota> notas = notaRepositorio.findByMatriculaIdAndBimestreId(matricula.getId(), cargaExcel.getBimestre().getId());
        Optional<Nota> notaExistente = notas.stream()
            .filter(n -> n.getCurso() != null && n.getCurso().getId().equals(curso.getId()))
            .findFirst();

        Nota nota = notaExistente.orElseGet(Nota::new);
        nota.setMatricula(matricula);
        nota.setCurso(curso);
        nota.setBimestre(cargaExcel.getBimestre());
        nota.setCargaExcel(cargaExcel);
        nota.setNota(valorNota);
        nota.setObservacion("Procesado desde archivo Excel");
        nota.setRegistradoPor(cargaExcel.getDocente());
        nota.setEstado(EstadoRegistro.ACTIVO);
        notaRepositorio.save(nota);
    }

    private void guardarAsistencia(CargaExcel cargaExcel, Matricula matricula, Integer clasesProgramadas, Integer clasesAsistidas) {
        Asistencia asistencia = asistenciaRepositorio
            .findByMatriculaIdAndBimestreId(matricula.getId(), cargaExcel.getBimestre().getId())
            .orElseGet(Asistencia::new);

        asistencia.setMatricula(matricula);
        asistencia.setBimestre(cargaExcel.getBimestre());
        asistencia.setClasesProgramadas(clasesProgramadas);
        asistencia.setClasesAsistidas(clasesAsistidas);
        asistencia.setObservacion("Procesado desde archivo Excel");
        asistencia.setEstado(EstadoRegistro.ACTIVO);
        asistenciaRepositorio.save(asistencia);
    }

    private Matricula obtenerMatricula(CargaExcel cargaExcel, String codigoAlumno) {
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            cargaExcel.getSeccion().getId(),
            cargaExcel.getPeriodoAcademico().getId()
        );

        return matriculas.stream()
            .filter(m -> m.getAlumno() != null && codigoAlumno.equalsIgnoreCase(m.getAlumno().getCodigo()))
            .findFirst()
            .orElseThrow(() -> new ReglaNegocioException("Alumno no pertenece a la seccion o periodo"));
    }

    private Curso obtenerCurso(Matricula matricula, String nombreCurso) {
        Long nivelId = matricula.getGrado().getNivel().getId();
        return cursoRepositorio.findByNivelId(nivelId)
            .stream()
            .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombreCurso))
            .findFirst()
            .orElseThrow(() -> new ReglaNegocioException("Curso no encontrado para el nivel del alumno"));
    }

    private void validarCabecera(Row cabecera) {
        if (cabecera == null) {
            throw new ReglaNegocioException("El archivo no contiene cabecera");
        }

        DataFormatter formatter = new DataFormatter(Locale.US);
        String[] esperadas = {"CODIGO_ALUMNO", "CURSO", "NOTA", "CLASES_PROGRAMADAS", "CLASES_ASISTIDAS"};
        for (int i = 0; i < esperadas.length; i++) {
            String valor = leerTexto(cabecera.getCell(i), formatter);
            if (!esperadas[i].equalsIgnoreCase(valor == null ? "" : valor.trim().toUpperCase())) {
                throw new ReglaNegocioException("Cabecera invalida. Se esperaba la columna: " + esperadas[i]);
            }
        }
    }

    private boolean filaVacia(Row fila) {
        if (fila == null) {
            return true;
        }
        for (int i = 0; i < 5; i++) {
            Cell cell = fila.getCell(i);
            if (cell != null && !new DataFormatter().formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String leerTexto(Cell celda, DataFormatter formatter) {
        return celda == null ? null : formatter.formatCellValue(celda);
    }

    private BigDecimal leerDecimal(Cell celda, DataFormatter formatter) {
        String valor = leerTexto(celda, formatter);
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("La nota es obligatoria");
        }
        return new BigDecimal(valor.trim());
    }

    private Integer leerEntero(Cell celda, DataFormatter formatter) {
        String valor = leerTexto(celda, formatter);
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El valor de asistencia es obligatorio");
        }
        return Integer.valueOf(valor.trim());
    }

    private String construirObservacion(List<ResultadoProcesamientoFilaDto> resultados, int filasError) {
        if (filasError == 0) {
            return "Archivo procesado correctamente";
        }

        StringBuilder builder = new StringBuilder("Archivo procesado con errores. ");
        resultados.stream()
            .filter(resultado -> !resultado.isValida())
            .limit(5)
            .forEach(resultado -> builder
                .append("[Fila ")
                .append(resultado.getNumeroFila())
                .append(": ")
                .append(resultado.getMensaje())
                .append("] "));

        String observacion = builder.toString().trim();
        if (observacion.length() <= LONGITUD_MAXIMA_OBSERVACION) {
            return observacion;
        }

        return observacion.substring(0, LONGITUD_MAXIMA_OBSERVACION - 3).trim() + "...";
    }
}

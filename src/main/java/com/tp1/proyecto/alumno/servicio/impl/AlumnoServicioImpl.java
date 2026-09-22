package com.tp1.proyecto.alumno.servicio.impl;

import com.tp1.proyecto.academico.dto.MatriculaRespuestaDto;
import com.tp1.proyecto.academico.dto.MatriculaSolicitudDto;
import com.tp1.proyecto.academico.servicio.MatriculaServicio;
import com.tp1.proyecto.alumno.dto.AlumnoMatriculaSolicitudDto;
import com.tp1.proyecto.alumno.dto.AlumnoRespuestaDto;
import com.tp1.proyecto.alumno.dto.AlumnoSolicitudDto;
import com.tp1.proyecto.alumno.entidad.Alumno;
import com.tp1.proyecto.alumno.repositorio.AlumnoRepositorio;
import com.tp1.proyecto.alumno.servicio.AlumnoServicio;
import com.tp1.proyecto.documento.entidad.TipoDocumento;
import com.tp1.proyecto.documento.servicio.TipoDocumentoServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlumnoServicioImpl implements AlumnoServicio {

    private final AlumnoRepositorio alumnoRepositorio;
    private final MatriculaServicio matriculaServicio;
    private final TipoDocumentoServicio tipoDocumentoServicio;

    public AlumnoServicioImpl(
        AlumnoRepositorio alumnoRepositorio,
        MatriculaServicio matriculaServicio,
        TipoDocumentoServicio tipoDocumentoServicio
    ) {
        this.alumnoRepositorio = alumnoRepositorio;
        this.matriculaServicio = matriculaServicio;
        this.tipoDocumentoServicio = tipoDocumentoServicio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoRespuestaDto> listar() {
        return alumnoRepositorio.findAll()
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoRespuestaDto obtenerPorId(Long id) {
        return mapearRespuesta(buscarAlumno(id));
    }

    @Override
    public AlumnoRespuestaDto crear(AlumnoSolicitudDto solicitud) {
        solicitud.setCodigo(resolverCodigo(solicitud.getCodigo(), null));
        TipoDocumento tipoDocumento = tipoDocumentoServicio.obtenerTipoDocumentoActivo(solicitud.getTipoDocumentoId());
        String numeroDocumento = tipoDocumentoServicio.validarNumeroDocumento(
            tipoDocumento, solicitud.getNumeroDocumento(), false
        );
        validarDuplicados(solicitud.getCodigo(), tipoDocumento.getId(), numeroDocumento, null);

        Alumno alumno = new Alumno();
        asignarCampos(alumno, solicitud, tipoDocumento, numeroDocumento);

        return mapearRespuesta(alumnoRepositorio.save(alumno));
    }

    @Override
    public AlumnoRespuestaDto actualizar(Long id, AlumnoSolicitudDto solicitud) {
        Alumno alumno = buscarAlumno(id);
        solicitud.setCodigo(resolverCodigo(solicitud.getCodigo(), alumno.getCodigo()));
        TipoDocumento tipoDocumento = tipoDocumentoServicio.obtenerTipoDocumentoActivo(solicitud.getTipoDocumentoId());
        String numeroDocumento = tipoDocumentoServicio.validarNumeroDocumento(
            tipoDocumento, solicitud.getNumeroDocumento(), false
        );
        validarDuplicados(solicitud.getCodigo(), tipoDocumento.getId(), numeroDocumento, alumno.getId());

        asignarCampos(alumno, solicitud, tipoDocumento, numeroDocumento);

        return mapearRespuesta(alumnoRepositorio.save(alumno));
    }

    @Override
    public MatriculaRespuestaDto crearYMatricular(AlumnoMatriculaSolicitudDto solicitud) {
        AlumnoSolicitudDto alumnoSolicitud = solicitud.getAlumno();
        alumnoSolicitud.setCodigo(resolverCodigo(alumnoSolicitud.getCodigo(), null));
        TipoDocumento tipoDocumento = tipoDocumentoServicio.obtenerTipoDocumentoActivo(alumnoSolicitud.getTipoDocumentoId());
        String numeroDocumento = tipoDocumentoServicio.validarNumeroDocumento(
            tipoDocumento, alumnoSolicitud.getNumeroDocumento(), false
        );
        validarDuplicados(alumnoSolicitud.getCodigo(), tipoDocumento.getId(), numeroDocumento, null);

        Alumno alumno = new Alumno();
        asignarCampos(alumno, alumnoSolicitud, tipoDocumento, numeroDocumento);
        Alumno alumnoGuardado = alumnoRepositorio.save(alumno);

        MatriculaSolicitudDto matriculaSolicitud = new MatriculaSolicitudDto();
        matriculaSolicitud.setAlumnoId(alumnoGuardado.getId());
        matriculaSolicitud.setSeccionId(solicitud.getSeccionId());
        matriculaSolicitud.setPeriodoAcademicoId(solicitud.getPeriodoAcademicoId());

        return matriculaServicio.crear(matriculaSolicitud);
    }

    private Alumno buscarAlumno(Long id) {
        return alumnoRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Alumno no encontrado con id: " + id));
    }

    private void validarDuplicados(
        String codigo,
        Long tipoDocumentoId,
        String numeroDocumento,
        Long alumnoActualId
    ) {
        String codigoNormalizado = normalizarTextoOpcional(codigo);
        if (codigoNormalizado != null) {
            alumnoRepositorio.findByCodigo(codigoNormalizado)
                .ifPresent(alumnoExistente -> {
                    boolean esOtroAlumno = alumnoActualId == null || !alumnoExistente.getId().equals(alumnoActualId);
                    if (esOtroAlumno) {
                        throw new ReglaNegocioException("Ya existe un alumno con ese codigo");
                    }
                });
        }

        if (numeroDocumento != null) {
            alumnoRepositorio.findByTipoDocumentoIdAndNumeroDocumento(tipoDocumentoId, numeroDocumento)
                .ifPresent(alumnoExistente -> {
                    boolean esOtroAlumno = alumnoActualId == null || !alumnoExistente.getId().equals(alumnoActualId);
                    if (esOtroAlumno) {
                        throw new ReglaNegocioException("Ya existe un alumno con ese tipo y numero de documento.");
                    }
                });
        }
    }

    private String resolverCodigo(String codigoSolicitado, String codigoActual) {
        String codigoNormalizado = normalizarTextoOpcional(codigoSolicitado);
        if (codigoNormalizado != null) {
            return codigoNormalizado;
        }

        if (codigoActual != null && !codigoActual.isBlank()) {
            return codigoActual;
        }

        return generarCodigoAutomatico();
    }

    private String generarCodigoAutomatico() {
        int anio = LocalDate.now().getYear();
        String prefijo = "AL-" + anio + "-";

        int siguienteNumero = alumnoRepositorio.findTopByCodigoStartingWithOrderByCodigoDesc(prefijo)
            .map(Alumno::getCodigo)
            .map(codigo -> codigo.substring(prefijo.length()))
            .map(this::parsearCorrelativo)
            .map(numero -> numero + 1)
            .orElse(1);

        return prefijo + String.format("%03d", siguienteNumero);
    }

    private int parsearCorrelativo(String correlativo) {
        try {
            return Integer.parseInt(correlativo);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void asignarCampos(
        Alumno alumno,
        AlumnoSolicitudDto solicitud,
        TipoDocumento tipoDocumento,
        String numeroDocumento
    ) {
        alumno.setCodigo(normalizarTexto(solicitud.getCodigo()));
        alumno.setTipoDocumento(tipoDocumento);
        alumno.setNumeroDocumento(numeroDocumento);
        alumno.setNombres(normalizarTexto(solicitud.getNombres()));
        alumno.setApellidos(normalizarTexto(solicitud.getApellidos()));
        alumno.setFechaNacimiento(solicitud.getFechaNacimiento());
        alumno.setSexo(normalizarTextoOpcional(solicitud.getSexo()));
        alumno.setDireccion(normalizarTextoOpcional(solicitud.getDireccion()));
        alumno.setNombreApoderado(normalizarTextoOpcional(solicitud.getNombreApoderado()));
        alumno.setTelefonoApoderado(normalizarTextoOpcional(solicitud.getTelefonoApoderado()));
    }

    private AlumnoRespuestaDto mapearRespuesta(Alumno alumno) {
        AlumnoRespuestaDto dto = new AlumnoRespuestaDto();
        dto.setId(alumno.getId());
        dto.setCodigo(alumno.getCodigo());
        dto.setNumeroDocumento(alumno.getNumeroDocumento());
        if (alumno.getTipoDocumento() != null) {
            dto.setTipoDocumentoId(alumno.getTipoDocumento().getId());
            dto.setTipoDocumentoCodigo(alumno.getTipoDocumento().getCodigo());
            dto.setTipoDocumentoNombre(alumno.getTipoDocumento().getDescripcionCorta());
        }
        dto.setNombres(alumno.getNombres());
        dto.setApellidos(alumno.getApellidos());
        dto.setFechaNacimiento(alumno.getFechaNacimiento());
        dto.setSexo(alumno.getSexo());
        dto.setDireccion(alumno.getDireccion());
        dto.setNombreApoderado(alumno.getNombreApoderado());
        dto.setTelefonoApoderado(alumno.getTelefonoApoderado());
        dto.setEstado(alumno.getEstado() != null ? alumno.getEstado().name() : null);
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return texto.trim().toUpperCase();
    }

}

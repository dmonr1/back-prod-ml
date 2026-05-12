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
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlumnoServicioImpl implements AlumnoServicio {

    private final AlumnoRepositorio alumnoRepositorio;
    private final MatriculaServicio matriculaServicio;

    public AlumnoServicioImpl(AlumnoRepositorio alumnoRepositorio, MatriculaServicio matriculaServicio) {
        this.alumnoRepositorio = alumnoRepositorio;
        this.matriculaServicio = matriculaServicio;
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
        validarDuplicados(solicitud.getCodigo(), solicitud.getDni(), null);

        Alumno alumno = new Alumno();
        asignarCampos(alumno, solicitud);

        return mapearRespuesta(alumnoRepositorio.save(alumno));
    }

    @Override
    public AlumnoRespuestaDto actualizar(Long id, AlumnoSolicitudDto solicitud) {
        Alumno alumno = buscarAlumno(id);
        validarDuplicados(solicitud.getCodigo(), solicitud.getDni(), alumno.getId());

        asignarCampos(alumno, solicitud);

        return mapearRespuesta(alumnoRepositorio.save(alumno));
    }

    @Override
    public MatriculaRespuestaDto crearYMatricular(AlumnoMatriculaSolicitudDto solicitud) {
        AlumnoSolicitudDto alumnoSolicitud = solicitud.getAlumno();
        validarDuplicados(alumnoSolicitud.getCodigo(), alumnoSolicitud.getDni(), null);

        Alumno alumno = new Alumno();
        asignarCampos(alumno, alumnoSolicitud);
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

    private void validarDuplicados(String codigo, String dni, Long alumnoActualId) {
        alumnoRepositorio.findByCodigo(normalizarTexto(codigo))
            .ifPresent(alumnoExistente -> {
                boolean esOtroAlumno = alumnoActualId == null || !alumnoExistente.getId().equals(alumnoActualId);
                if (esOtroAlumno) {
                    throw new ReglaNegocioException("Ya existe un alumno con ese codigo");
                }
            });

        if (dni != null && !dni.trim().isEmpty()) {
            alumnoRepositorio.findByDni(dni.trim())
                .ifPresent(alumnoExistente -> {
                    boolean esOtroAlumno = alumnoActualId == null || !alumnoExistente.getId().equals(alumnoActualId);
                    if (esOtroAlumno) {
                        throw new ReglaNegocioException("Ya existe un alumno con ese DNI");
                    }
                });
        }
    }

    private void asignarCampos(Alumno alumno, AlumnoSolicitudDto solicitud) {
        alumno.setCodigo(normalizarTexto(solicitud.getCodigo()));
        alumno.setDni(normalizarDni(solicitud.getDni()));
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
        dto.setDni(alumno.getDni());
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

    private String normalizarDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        return dni.trim();
    }
}

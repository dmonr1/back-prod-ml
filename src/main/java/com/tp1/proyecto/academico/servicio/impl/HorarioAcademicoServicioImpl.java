package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.BloqueHorarioRespuestaDto;
import com.tp1.proyecto.academico.dto.BloqueHorarioSolicitudDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalRespuestaDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalSolicitudDto;
import com.tp1.proyecto.academico.entidad.BloqueHorario;
import com.tp1.proyecto.academico.entidad.DiaSemana;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.academico.entidad.Nivel;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.BloqueHorarioRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.NivelRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.servicio.HorarioAcademicoServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HorarioAcademicoServicioImpl implements HorarioAcademicoServicio {
    private final BloqueHorarioRepositorio bloqueRepositorio;
    private final HorarioSemanalRepositorio horarioRepositorio;
    private final DocenteCursoSeccionRepositorio asignacionRepositorio;
    private final PeriodoAcademicoRepositorio periodoRepositorio;
    private final NivelRepositorio nivelRepositorio;
    private final DocenteRepositorio docenteRepositorio;

    public HorarioAcademicoServicioImpl(
        BloqueHorarioRepositorio bloqueRepositorio,
        HorarioSemanalRepositorio horarioRepositorio,
        DocenteCursoSeccionRepositorio asignacionRepositorio,
        PeriodoAcademicoRepositorio periodoRepositorio,
        NivelRepositorio nivelRepositorio,
        DocenteRepositorio docenteRepositorio
    ) {
        this.bloqueRepositorio = bloqueRepositorio;
        this.horarioRepositorio = horarioRepositorio;
        this.asignacionRepositorio = asignacionRepositorio;
        this.periodoRepositorio = periodoRepositorio;
        this.nivelRepositorio = nivelRepositorio;
        this.docenteRepositorio = docenteRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueHorarioRespuestaDto> listarBloques(Long periodoId, Long nivelId) {
        return bloqueRepositorio.findByPeriodoAcademicoIdAndNivelIdAndEstadoOrderByOrdenAsc(
            periodoId, nivelId, EstadoRegistro.ACTIVO
        ).stream().map(this::mapearBloque).toList();
    }

    @Override
    public BloqueHorarioRespuestaDto crearBloque(BloqueHorarioSolicitudDto solicitud) {
        PeriodoAcademico periodo = obtenerPeriodo(solicitud.getPeriodoAcademicoId());
        Nivel nivel = nivelRepositorio.findById(solicitud.getNivelId())
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el nivel académico."));
        validarRangoBloque(solicitud, periodo.getId(), nivel.getId(), null);
        BloqueHorario bloque = new BloqueHorario();
        bloque.setPeriodoAcademico(periodo);
        bloque.setNivel(nivel);
        aplicarSolicitud(bloque, solicitud);
        return mapearBloque(bloqueRepositorio.save(bloque));
    }

    @Override
    public BloqueHorarioRespuestaDto actualizarBloque(Long id, BloqueHorarioSolicitudDto solicitud) {
        BloqueHorario bloque = bloqueRepositorio.findByIdAndEstado(id, EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el bloque horario activo."));
        if (!bloque.getPeriodoAcademico().getId().equals(solicitud.getPeriodoAcademicoId()) ||
            !bloque.getNivel().getId().equals(solicitud.getNivelId())) {
            throw new ReglaNegocioException("No se puede mover un bloque a otro período o nivel.");
        }
        validarRangoBloque(solicitud, solicitud.getPeriodoAcademicoId(), solicitud.getNivelId(), id);
        validarHorariosAlActualizarBloque(bloque, solicitud);
        aplicarSolicitud(bloque, solicitud);
        return mapearBloque(bloqueRepositorio.save(bloque));
    }

    @Override
    public void actualizarEstadoBloque(Long id, boolean activo) {
        BloqueHorario bloque = bloqueRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el bloque horario."));
        if (activo) {
            throw new ReglaNegocioException("Un bloque inactivo no se puede reactivar; crea uno nuevo con otro orden.");
        }
        boolean tieneHorarioActivo = horarioRepositorio.findByBloquePeriodoAcademicoIdAndEstado(
            bloque.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        ).stream().anyMatch(item -> item.getBloque().getId().equals(id));
        if (tieneHorarioActivo) {
            throw new ReglaNegocioException("No se puede desactivar el bloque porque tiene clases programadas.");
        }
        bloque.setEstado(EstadoRegistro.INACTIVO);
        bloqueRepositorio.save(bloque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioSemanalRespuestaDto> listarPorPeriodo(Long periodoId) {
        obtenerPeriodo(periodoId);
        return horarioRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(periodoId, EstadoRegistro.ACTIVO)
            .stream().map(this::mapearHorario).sorted(ordenHorario()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioSemanalRespuestaDto> listarMios(Long periodoId, Long usuarioId) {
        obtenerPeriodo(periodoId);
        Docente docente = docenteRepositorio.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene un perfil docente vinculado."));
        return horarioRepositorio.findByAsignacionDocenteIdAndAsignacionPeriodoAcademicoIdAndEstado(
            docente.getId(), periodoId, EstadoRegistro.ACTIVO
        ).stream().map(this::mapearHorario).sorted(ordenHorario()).toList();
    }

    @Override
    public HorarioSemanalRespuestaDto crearHorario(HorarioSemanalSolicitudDto solicitud) {
        DocenteCursoSeccion asignacion = asignacionRepositorio.findById(solicitud.getAsignacionId())
            .filter(item -> item.getEstado() == EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró una asignación académica activa."));
        BloqueHorario bloque = bloqueRepositorio.findByIdAndEstado(solicitud.getBloqueHorarioId(), EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un bloque horario activo."));
        Long periodoId = asignacion.getPeriodoAcademico().getId();
        Long nivelId = asignacion.getCurso().getNivel().getId();
        if (!bloque.getPeriodoAcademico().getId().equals(periodoId) || !bloque.getNivel().getId().equals(nivelId)) {
            throw new ReglaNegocioException("El bloque debe pertenecer al mismo período y nivel de la asignación.");
        }
        List<HorarioSemanal> existentes = horarioRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(
            periodoId, EstadoRegistro.ACTIVO
        );
        for (HorarioSemanal existente : existentes) {
            if (existente.getDiaSemana() != solicitud.getDiaSemana() || !seCruzanEnHora(existente.getBloque(), bloque)) {
                continue;
            }
            DocenteCursoSeccion otra = existente.getAsignacion();
            if (otra.getDocente().getId().equals(asignacion.getDocente().getId())) {
                throw new ReglaNegocioException("El docente ya tiene una clase programada que se cruza en ese horario.");
            }
            if (otra.getSeccion().getId().equals(asignacion.getSeccion  ().getId())) {
                throw new ReglaNegocioException("La sección ya tiene una clase programada que se cruza en ese horario.");
            }
        }
        if (horarioRepositorio.existsByAsignacionIdAndDiaSemanaAndBloqueIdAndEstado(
            asignacion.getId(), solicitud.getDiaSemana(), bloque.getId(), EstadoRegistro.ACTIVO
        )) {
            throw new ReglaNegocioException("Esta asignación ya está programada en ese día y bloque.");
        }
        HorarioSemanal horario = new HorarioSemanal();
        horario.setAsignacion(asignacion);
        horario.setBloque(bloque);
        horario.setDiaSemana(solicitud.getDiaSemana());
        return mapearHorario(horarioRepositorio.save(horario));
    }

    @Override
    public void actualizarEstadoHorario(Long id, boolean activo) {
        HorarioSemanal horario = horarioRepositorio.findByIdAndEstado(id, EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la clase programada."));
        if (activo) {
            throw new ReglaNegocioException("La clase fue desactivada; crea una nueva programación para reponerla.");
        }
        horario.setEstado(EstadoRegistro.INACTIVO);
        horarioRepositorio.save(horario);
    }

    private void validarRangoBloque(BloqueHorarioSolicitudDto solicitud, Long periodoId, Long nivelId, Long ignorarId) {
        if (!solicitud.getHoraInicio().isBefore(solicitud.getHoraFin())) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        if (bloqueRepositorio.existsByPeriodoAcademicoIdAndNivelIdAndOrdenAndEstado(
            periodoId, nivelId, solicitud.getOrden(), EstadoRegistro.ACTIVO
        ) && (ignorarId == null || bloqueRepositorio.findByPeriodoAcademicoIdAndNivelIdAndEstadoOrderByOrdenAsc(
            periodoId, nivelId, EstadoRegistro.ACTIVO
        ).stream().anyMatch(item -> !item.getId().equals(ignorarId) && item.getOrden().equals(solicitud.getOrden())))) {
            throw new ReglaNegocioException("Ya existe un bloque activo con ese orden para este nivel y período.");
        }
        List<BloqueHorario> bloques = bloqueRepositorio.findByPeriodoAcademicoIdAndNivelIdAndEstado(
            periodoId, nivelId, EstadoRegistro.ACTIVO
        );
        for (BloqueHorario existente : bloques) {
            if (existente.getId().equals(ignorarId)) continue;
            boolean seCruzan = solicitud.getHoraInicio().isBefore(existente.getHoraFin()) &&
                solicitud.getHoraFin().isAfter(existente.getHoraInicio());
            if (seCruzan) throw new ReglaNegocioException("El horario se cruza con el bloque «" + existente.getNombre() + "».");
        }
    }

    private boolean seCruzanEnHora(BloqueHorario a, BloqueHorario b) {
        return a.getHoraInicio().isBefore(b.getHoraFin()) && b.getHoraInicio().isBefore(a.getHoraFin());
    }

    private void validarHorariosAlActualizarBloque(BloqueHorario bloque, BloqueHorarioSolicitudDto solicitud) {
        List<HorarioSemanal> horariosDelBloque = horarioRepositorio.findByBloqueIdAndEstado(
            bloque.getId(), EstadoRegistro.ACTIVO
        );
        List<HorarioSemanal> todosDelPeriodo = horarioRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(
            bloque.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        );
        for (HorarioSemanal horario : horariosDelBloque) {
            for (HorarioSemanal otro : todosDelPeriodo) {
                if (otro.getId().equals(horario.getId()) || otro.getDiaSemana() != horario.getDiaSemana()) continue;
                boolean seCruzan = solicitud.getHoraInicio().isBefore(otro.getBloque().getHoraFin()) &&
                    solicitud.getHoraFin().isAfter(otro.getBloque().getHoraInicio());
                if (!seCruzan) continue;
                DocenteCursoSeccion asignacion = horario.getAsignacion();
                DocenteCursoSeccion otra = otro.getAsignacion();
                if (asignacion.getDocente().getId().equals(otra.getDocente().getId())) {
                    throw new ReglaNegocioException("El nuevo rango se cruza con otra clase del docente.");
                }
                if (asignacion.getSeccion().getId().equals(otra.getSeccion().getId())) {
                    throw new ReglaNegocioException("El nuevo rango se cruza con otra clase de la sección.");
                }
            }
        }
    }

    private void aplicarSolicitud(BloqueHorario bloque, BloqueHorarioSolicitudDto solicitud) {
        bloque.setNombre(solicitud.getNombre().trim());
        bloque.setOrden(solicitud.getOrden());
        bloque.setHoraInicio(solicitud.getHoraInicio());
        bloque.setHoraFin(solicitud.getHoraFin());
    }

    private PeriodoAcademico obtenerPeriodo(Long id) {
        return periodoRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el período académico."));
    }

    private BloqueHorarioRespuestaDto mapearBloque(BloqueHorario bloque) {
        BloqueHorarioRespuestaDto dto = new BloqueHorarioRespuestaDto();
        dto.setId(bloque.getId());
        dto.setPeriodoAcademicoId(bloque.getPeriodoAcademico().getId());
        dto.setNivelId(bloque.getNivel().getId());
        dto.setNivel(bloque.getNivel().getNombre());
        dto.setNombre(bloque.getNombre());
        dto.setOrden(bloque.getOrden());
        dto.setHoraInicio(bloque.getHoraInicio());
        dto.setHoraFin(bloque.getHoraFin());
        return dto;
    }

    private HorarioSemanalRespuestaDto mapearHorario(HorarioSemanal horario) {
        DocenteCursoSeccion asignacion = horario.getAsignacion();
        BloqueHorario bloque = horario.getBloque();
        HorarioSemanalRespuestaDto dto = new HorarioSemanalRespuestaDto();
        dto.setId(horario.getId());
        dto.setAsignacionId(asignacion.getId());
        dto.setDocenteId(asignacion.getDocente().getId());
        dto.setDocente(asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos());
        dto.setCursoId(asignacion.getCurso().getId());
        dto.setCurso(asignacion.getCurso().getNombre());
        dto.setSeccionId(asignacion.getSeccion().getId());
        dto.setSeccion(asignacion.getSeccion().getNombre());
        dto.setGrado(asignacion.getSeccion().getGrado().getNombre());
        dto.setNivel(asignacion.getCurso().getNivel().getNombre());
        dto.setPeriodoAcademicoId(asignacion.getPeriodoAcademico().getId());
        dto.setBloqueHorarioId(bloque.getId());
        dto.setBloque(bloque.getNombre());
        dto.setOrdenBloque(bloque.getOrden());
        dto.setHoraInicio(bloque.getHoraInicio());
        dto.setHoraFin(bloque.getHoraFin());
        dto.setDiaSemana(horario.getDiaSemana());
        return dto;
    }

    private Comparator<HorarioSemanalRespuestaDto> ordenHorario() {
        return Comparator.comparing((HorarioSemanalRespuestaDto item) -> item.getDiaSemana().ordinal())
            .thenComparing(HorarioSemanalRespuestaDto::getOrdenBloque);
    }
}

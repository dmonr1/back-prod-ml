package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioSemanalRepositorio extends JpaRepository<HorarioSemanal, Long> {
    List<HorarioSemanal> findByEstado(com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado);
    List<HorarioSemanal> findByAsignacionPeriodoAcademicoIdAndEstado(Long periodoAcademicoId, EstadoRegistro estado);
    List<HorarioSemanal> findByBloquePeriodoAcademicoIdAndEstado(Long periodoAcademicoId, EstadoRegistro estado);
    List<HorarioSemanal> findByBloqueIdAndEstado(Long bloqueId, EstadoRegistro estado);
    List<HorarioSemanal> findByAsignacionDocenteIdAndAsignacionPeriodoAcademicoIdAndEstado(
        Long docenteId, Long periodoAcademicoId, EstadoRegistro estado
    );
    Optional<HorarioSemanal> findByIdAndEstado(Long id, EstadoRegistro estado);
    boolean existsByAsignacionIdAndDiaSemanaAndBloqueIdAndEstado(
        Long asignacionId, com.tp1.proyecto.academico.entidad.DiaSemana diaSemana,
        Long bloqueId, EstadoRegistro estado
    );
}

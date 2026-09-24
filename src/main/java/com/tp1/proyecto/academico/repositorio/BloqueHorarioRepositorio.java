package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.BloqueHorario;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueHorarioRepositorio extends JpaRepository<BloqueHorario, Long> {
    List<BloqueHorario> findByPeriodoAcademicoIdAndNivelIdAndEstadoOrderByOrdenAsc(
        Long periodoAcademicoId, Long nivelId, EstadoRegistro estado
    );
    List<BloqueHorario> findByPeriodoAcademicoIdAndNivelIdAndEstado(
        Long periodoAcademicoId, Long nivelId, EstadoRegistro estado
    );
    List<BloqueHorario> findByPeriodoAcademicoIdAndEstadoOrderByNivelIdAscOrdenAsc(
        Long periodoAcademicoId, EstadoRegistro estado
    );
    Optional<BloqueHorario> findByIdAndEstado(Long id, EstadoRegistro estado);
    boolean existsByPeriodoAcademicoIdAndNivelIdAndOrdenAndEstado(
        Long periodoAcademicoId, Long nivelId, Short orden, EstadoRegistro estado
    );
}

package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Tutoria;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutoriaRepositorio extends JpaRepository<Tutoria, Long> {

    List<Tutoria> findByDocenteIdAndPeriodoAcademicoId(Long docenteId, Long periodoAcademicoId);

    List<Tutoria> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Tutoria> findBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);

    boolean existsByDocenteId(Long docenteId);

    boolean existsBySeccionIdAndPeriodoAcademicoIdAndEstado(
        Long seccionId,
        Long periodoAcademicoId,
        EstadoRegistro estado
    );
}

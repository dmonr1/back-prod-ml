package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Matricula;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;

public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {

    List<Matricula> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Matricula> findBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);

    List<Matricula> findBySeccionIdAndPeriodoAcademicoIdAndEstado(
        Long seccionId, Long periodoAcademicoId, EstadoRegistro estado
    );

    Optional<Matricula> findByAlumnoIdAndPeriodoAcademicoId(Long alumnoId, Long periodoAcademicoId);
}

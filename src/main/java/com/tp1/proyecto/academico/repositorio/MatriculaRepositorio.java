package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Matricula;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {

    List<Matricula> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Matricula> findBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);

    Optional<Matricula> findByAlumnoIdAndPeriodoAcademicoId(Long alumnoId, Long periodoAcademicoId);
}

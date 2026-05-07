package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Bimestre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BimestreRepositorio extends JpaRepository<Bimestre, Long> {

    List<Bimestre> findByPeriodoAcademicoId(Long periodoAcademicoId);
}

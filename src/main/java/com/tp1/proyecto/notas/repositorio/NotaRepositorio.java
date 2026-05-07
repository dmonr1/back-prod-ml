package com.tp1.proyecto.notas.repositorio;

import com.tp1.proyecto.notas.entidad.Nota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepositorio extends JpaRepository<Nota, Long> {

    List<Nota> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);

    List<Nota> findByCargaExcelId(Long cargaExcelId);
}

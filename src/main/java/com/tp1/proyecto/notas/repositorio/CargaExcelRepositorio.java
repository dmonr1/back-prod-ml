package com.tp1.proyecto.notas.repositorio;

import com.tp1.proyecto.notas.entidad.CargaExcel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargaExcelRepositorio extends JpaRepository<CargaExcel, Long> {

    List<CargaExcel> findByDocenteIdOrderByFechaCargaDesc(Long docenteId);
}

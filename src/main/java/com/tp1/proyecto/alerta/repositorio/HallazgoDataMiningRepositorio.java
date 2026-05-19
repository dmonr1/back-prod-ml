package com.tp1.proyecto.alerta.repositorio;

import com.tp1.proyecto.alerta.entidad.HallazgoDataMining;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallazgoDataMiningRepositorio extends JpaRepository<HallazgoDataMining, Long> {

    List<HallazgoDataMining> findByPeriodoEvaluacionIdAndSeccionIdOrderByFechaGeneracionDescIdDesc(
        Long periodoEvaluacionId,
        Long seccionId
    );

    void deleteByPeriodoEvaluacionIdAndSeccionId(Long periodoEvaluacionId, Long seccionId);
}

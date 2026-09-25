package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorteSeguimientoRepositorio extends JpaRepository<CorteSeguimiento, Long> {
    List<CorteSeguimiento> findByPeriodoAcademicoIdAndEstadoOrderBySemanaAsc(Long periodoAcademicoId, EstadoRegistro estado);
    List<CorteSeguimiento> findByPeriodoAcademicoIdAndFechaCorteLessThanEqualAndEstadoOrderByFechaCorteAsc(
        Long periodoAcademicoId, java.time.LocalDate fechaCorte, EstadoRegistro estado
    );
}

package com.tp1.proyecto.evaluacion.evento;

import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.alerta.servicio.HallazgoDataMiningServicio;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaPeriodoEvaluacionServicio;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RecalcularPrediccionPorAsistenciaListener {

    private static final Logger log = LoggerFactory.getLogger(RecalcularPrediccionPorAsistenciaListener.class);
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;
    private final AsistenciaPeriodoEvaluacionServicio asistenciaPeriodoEvaluacionServicio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final HallazgoDataMiningServicio hallazgoDataMiningServicio;

    public RecalcularPrediccionPorAsistenciaListener(
        PrediccionRiesgoServicio prediccionRiesgoServicio,
        AsistenciaPeriodoEvaluacionServicio asistenciaPeriodoEvaluacionServicio,
        MatriculaRepositorio matriculaRepositorio,
        HallazgoDataMiningServicio hallazgoDataMiningServicio
    ) {
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
        this.asistenciaPeriodoEvaluacionServicio = asistenciaPeriodoEvaluacionServicio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.hallazgoDataMiningServicio = hallazgoDataMiningServicio;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alRegistrarAsistencia(AsistenciaSesionRegistradaEvent event) {
        Set<Long> seccionesIds = new HashSet<>();
        for (Long matriculaId : event.matriculaIds()) {
            try {
                asistenciaPeriodoEvaluacionServicio.sincronizarDesdeSesionesPorMatricula(matriculaId, event.periodoEvaluacionId());
            } catch (RuntimeException error) {
                log.error("No se pudo sincronizar la asistencia de matrícula {} después de guardar asistencia", matriculaId, error);
            }
            try {
                prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matriculaId, event.periodoEvaluacionId());
            } catch (RuntimeException error) {
                log.error("No se pudo recalcular la predicción de matrícula {} después de guardar asistencia", matriculaId, error);
            }
            matriculaRepositorio.findById(matriculaId).ifPresent(m -> {
                if (m.getSeccion() != null) {
                    seccionesIds.add(m.getSeccion().getId());
                }
            });
        }

        for (Long seccionId : seccionesIds) {
            try {
                hallazgoDataMiningServicio.generarHallazgos(event.periodoEvaluacionId(), seccionId);
            } catch (RuntimeException error) {
                log.error("No se pudo actualizar hallazgos para sección {} y periodo {}", seccionId, event.periodoEvaluacionId(), error);
            }
        }
    }
}

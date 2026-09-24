package com.tp1.proyecto.evaluacion.evento;

import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RecalcularPrediccionPorAsistenciaListener {

    private static final Logger log = LoggerFactory.getLogger(RecalcularPrediccionPorAsistenciaListener.class);
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public RecalcularPrediccionPorAsistenciaListener(PrediccionRiesgoServicio prediccionRiesgoServicio) {
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alRegistrarAsistencia(AsistenciaSesionRegistradaEvent event) {
        for (Long matriculaId : event.matriculaIds()) {
            try {
                prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matriculaId, event.periodoEvaluacionId());
            } catch (RuntimeException error) {
                log.error("No se pudo recalcular la predicción de matrícula {} después de guardar asistencia", matriculaId, error);
            }
        }
    }
}

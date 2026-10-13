package com.tp1.proyecto.prediccion.servicio;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ProgresoRecalculoPredicciones {
    private static final long EXPIRACION_SEGUNDOS = 3600;
    private final ConcurrentHashMap<String, Estado> estados = new ConcurrentHashMap<>();

    public void iniciar(String trabajoId) {
        Instant ahora = Instant.now();
        estados.entrySet().removeIf(entry -> ahora.getEpochSecond() - entry.getValue().actualizado().getEpochSecond() > EXPIRACION_SEGUNDOS);
        estados.put(trabajoId, new Estado(0, 0, 0, 0, false, false, ahora));
    }

    public void preparar(String trabajoId, int total) {
        actualizar(trabajoId, 0, total, 0, 0, false, false);
    }

    public void iniciarAlumno(String trabajoId, int procesados, int total) {
        Estado anterior = estados.get(trabajoId);
        actualizar(trabajoId, procesados, total, procesados + 1,
            anterior == null ? 0 : anterior.erroresPrediccion(), false, false);
    }

    public void avanzar(String trabajoId, int procesados, int total) {
        Estado anterior = estados.get(trabajoId);
        actualizar(trabajoId, procesados, total, 0,
            anterior == null ? 0 : anterior.erroresPrediccion(), false, false);
    }

    public void registrarErrorPrediccion(String trabajoId) {
        Estado anterior = estados.get(trabajoId);
        if (anterior == null) return;
        actualizar(trabajoId, anterior.procesados(), anterior.total(), anterior.alumnoEnCurso(),
            anterior.erroresPrediccion() + 1, false, false);
    }

    public void finalizar(String trabajoId) {
        Estado anterior = estados.get(trabajoId);
        if (anterior == null) return;
        actualizar(trabajoId, anterior.procesados(), anterior.total(), 0,
            anterior.erroresPrediccion(), true, false);
    }

    public void fallar(String trabajoId) {
        Estado anterior = estados.get(trabajoId);
        if (anterior == null) return;
        actualizar(trabajoId, anterior.procesados(), anterior.total(), 0,
            anterior.erroresPrediccion(), true, true);
    }

    public Estado obtener(String trabajoId) {
        return estados.get(trabajoId);
    }

    private void actualizar(String trabajoId, int procesados, int total, int alumnoEnCurso, int erroresPrediccion, boolean finalizado, boolean error) {
        estados.computeIfPresent(trabajoId, (id, anterior) ->
            new Estado(procesados, total, alumnoEnCurso, erroresPrediccion, finalizado, error, Instant.now()));
    }

    public record Estado(int procesados, int total, int alumnoEnCurso, int erroresPrediccion, boolean finalizado, boolean error, Instant actualizado) {
        public int porcentaje() {
            if (finalizado) return 100;
            if (total <= 0) return 0;
            int avance = Math.max(procesados, alumnoEnCurso);
            return Math.min(99, (int) Math.floor((avance * 100.0) / total));
        }
    }
}

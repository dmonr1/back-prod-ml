package com.tp1.proyecto.academico.dto;

import java.time.LocalDate;

public record PreparacionCorteRespuestaDto(
    String periodoAcademico,
    String periodoEvaluacion,
    LocalDate fechaCorte,
    boolean corteDisponible,
    int alumnosMatriculados,
    int alumnosConDatos,
    int alumnosConAsistencia,
    int evaluacionesSinFecha,
    int evaluacionesAlCorte,
    int notasEsperadas,
    int notasRegistradas,
    int bloquesSemanales,
    int asistenciasRegistradas
) {}

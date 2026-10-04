package com.tp1.proyecto.auditoria.servicio.impl;

import com.tp1.proyecto.auditoria.dto.EstadisticasAuditoriaDto;
import com.tp1.proyecto.auditoria.dto.RegistroAuditoriaDto;
import com.tp1.proyecto.auditoria.servicio.AuditoriaServicio;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaServicioImpl implements AuditoriaServicio {

    private final JdbcTemplate jdbcTemplate;

    public AuditoriaServicioImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RegistroAuditoriaDto> listarLogs(
        String modulo,
        String nivelCriticidad,
        String busqueda,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer limite
    ) {
        List<RegistroAuditoriaDto> resultados = new ArrayList<>();

        // 1. Historial de ediciones de asistencia (Crítico)
        if (modulo == null || modulo.isBlank() || "ASISTENCIA".equalsIgnoreCase(modulo)) {
            try {
                String sqlAsistencia = """
                    SELECT h.id,
                           'ASISTENCIA' AS modulo,
                           'EDICION_ASISTENCIA' AS tipo_evento,
                           CONCAT('Cambio de estado: ', COALESCE(h.estado_anterior, 'SIN_REGISTRO'), ' -> ', h.estado_nuevo, '. Motivo: ', h.motivo) AS descripcion,
                           CONCAT('Alumno: ', a.nombres, ' ', a.apellidos, ' (', a.codigo, ') - ', c.nombre, ' ', sec.nombre) AS entidad_afectada,
                           u.username AS usuario_username,
                           CONCAT(u.nombres, ' ', u.apellidos) AS usuario_nombre,
                           'CRITICO' AS nivel_criticidad,
                           h.fecha_edicion AS fecha_evento,
                           CONCAT('Obs ant: ', COALESCE(h.observacion_anterior, '-'), ' | Obs nueva: ', COALESCE(h.observacion_nueva, '-')) AS detalle_adicional
                    FROM db_tp1.historial_ediciones_asistencia h
                    JOIN db_tp1.asistencias_sesion asis ON asis.id = h.asistencia_sesion_id
                    JOIN db_tp1.matriculas m ON m.id = asis.matricula_id
                    JOIN db_tp1.alumnos a ON a.id = m.alumno_id
                    JOIN db_tp1.docente_curso_seccion dcs ON dcs.id = asis.docente_curso_seccion_id
                    JOIN db_tp1.cursos c ON c.id = dcs.curso_id
                    JOIN db_tp1.secciones sec ON sec.id = dcs.seccion_id
                    JOIN db_tp1.usuarios u ON u.id = h.usuario_editor_id
                    ORDER BY h.fecha_edicion DESC
                    LIMIT 200
                """;
                List<RegistroAuditoriaDto> list = jdbcTemplate.query(sqlAsistencia, (rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("fecha_evento");
                    LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                    return new RegistroAuditoriaDto(
                        "ASIS-" + rs.getLong("id"),
                        rs.getString("modulo"),
                        rs.getString("tipo_evento"),
                        rs.getString("descripcion"),
                        rs.getString("entidad_afectada"),
                        rs.getString("usuario_username"),
                        rs.getString("usuario_nombre"),
                        rs.getString("nivel_criticidad"),
                        fecha,
                        rs.getString("detalle_adicional")
                    );
                });
                resultados.addAll(list);
            } catch (Exception ex) {
                // Si tabla no existe en algún entorno, continuar sin fallar
            }
        }

        // 2. Cargas masivas de archivos Excel (Trazabilidad y auditoría de ejecutor)
        if (modulo == null || modulo.isBlank() || "CARGA_MASIVA".equalsIgnoreCase(modulo)) {
            try {
                String sqlCargas = """
                    SELECT ca.id,
                           'CARGA_MASIVA' AS modulo,
                           'IMPORTACION_EXCEL' AS tipo_evento,
                           CONCAT('Carga masiva de ', ca.tipo, ': ', ca.registros_procesados, ' procesados, ', ca.registros_con_error, ' con error. Archivo: ', ca.nombre_archivo) AS descripcion,
                           CONCAT('Lote: ', ca.nombre_archivo, ' (Docente ID: ', COALESCE(ca.docente_id::text, 'No asignado'), ')') AS entidad_afectada,
                           COALESCE(u.username, 'admin') AS usuario_username,
                           COALESCE(CONCAT(u.nombres, ' ', u.apellidos), 'Administrador del Sistema') AS usuario_nombre,
                           CASE WHEN ca.registros_con_error > 0 THEN 'ADVERTENCIA' ELSE 'INFO' END AS nivel_criticidad,
                           ca.fecha_carga AS fecha_evento,
                           CONCAT('Estado: ', ca.estado, ' | Errores: ', ca.registros_con_error) AS detalle_adicional
                    FROM db_tp1.cargas_archivos ca
                    LEFT JOIN db_tp1.usuarios u ON u.id = ca.usuario_ejecutor_id
                    ORDER BY ca.fecha_carga DESC
                    LIMIT 200
                """;
                List<RegistroAuditoriaDto> list = jdbcTemplate.query(sqlCargas, (rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("fecha_evento");
                    LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                    return new RegistroAuditoriaDto(
                        "CARGA-" + rs.getLong("id"),
                        rs.getString("modulo"),
                        rs.getString("tipo_evento"),
                        rs.getString("descripcion"),
                        rs.getString("entidad_afectada"),
                        rs.getString("usuario_username"),
                        rs.getString("usuario_nombre"),
                        rs.getString("nivel_criticidad"),
                        fecha,
                        rs.getString("detalle_adicional")
                    );
                });
                resultados.addAll(list);
            } catch (Exception ex) {
                // Ignorar si falla
            }
        }

        // 3. Evaluaciones creadas / modificadas
        if (modulo == null || modulo.isBlank() || "EVALUACION".equalsIgnoreCase(modulo)) {
            try {
                String sqlEvaluaciones = """
                    SELECT ev.id,
                           'EVALUACION' AS modulo,
                           'GESTION_EVALUACION' AS tipo_evento,
                           CONCAT('Evaluación configurada: ', ev.nombre, ' (N.° ', ev.numero_evaluacion, ')') AS descripcion,
                           CONCAT('Curso: ', c.nombre, ' - Sección: ', sec.nombre) AS entidad_afectada,
                           COALESCE(u.username, 'sistema') AS usuario_username,
                           COALESCE(CONCAT(u.nombres, ' ', u.apellidos), 'Docente / Admin') AS usuario_nombre,
                           'INFO' AS nivel_criticidad,
                           COALESCE(ev.fecha_actualizacion, ev.fecha_creacion, CURRENT_TIMESTAMP) AS fecha_evento,
                           CONCAT('Fecha programada: ', COALESCE(ev.fecha_evaluacion::text, 'Sin fecha')) AS detalle_adicional
                    FROM db_tp1.evaluaciones ev
                    JOIN db_tp1.docente_curso_seccion dcs ON dcs.id = ev.docente_curso_seccion_id
                    JOIN db_tp1.cursos c ON c.id = dcs.curso_id
                    JOIN db_tp1.secciones sec ON sec.id = dcs.seccion_id
                    LEFT JOIN db_tp1.usuarios u ON u.id = COALESCE(ev.modificado_por_usuario_id, ev.creado_por_usuario_id)
                    ORDER BY COALESCE(ev.fecha_actualizacion, ev.fecha_creacion) DESC
                    LIMIT 200
                """;
                List<RegistroAuditoriaDto> list = jdbcTemplate.query(sqlEvaluaciones, (rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("fecha_evento");
                    LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                    return new RegistroAuditoriaDto(
                        "EVAL-" + rs.getLong("id"),
                        rs.getString("modulo"),
                        rs.getString("tipo_evento"),
                        rs.getString("descripcion"),
                        rs.getString("entidad_afectada"),
                        rs.getString("usuario_username"),
                        rs.getString("usuario_nombre"),
                        rs.getString("nivel_criticidad"),
                        fecha,
                        rs.getString("detalle_adicional")
                    );
                });
                resultados.addAll(list);
            } catch (Exception ex) {
                // Ignorar si falla
            }
        }

        // Aplicar filtros en memoria
        return resultados.stream()
            .filter(r -> nivelCriticidad == null || nivelCriticidad.isBlank() || r.getNivelCriticidad().equalsIgnoreCase(nivelCriticidad))
            .filter(r -> {
                if (fechaInicio != null && r.getFechaEvento().toLocalDate().isBefore(fechaInicio)) {
                    return false;
                }
                if (fechaFin != null && r.getFechaEvento().toLocalDate().isAfter(fechaFin)) {
                    return false;
                }
                return true;
            })
            .filter(r -> {
                if (busqueda == null || busqueda.isBlank()) return true;
                String q = busqueda.toLowerCase().trim();
                return (r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(q))
                    || (r.getEntidadAfectada() != null && r.getEntidadAfectada().toLowerCase().contains(q))
                    || (r.getUsuarioUsername() != null && r.getUsuarioUsername().toLowerCase().contains(q))
                    || (r.getUsuarioNombre() != null && r.getUsuarioNombre().toLowerCase().contains(q));
            })
            .sorted(Comparator.comparing(RegistroAuditoriaDto::getFechaEvento).reversed())
            .limit(limite != null && limite > 0 ? limite : 150)
            .collect(Collectors.toList());
    }

    @Override
    public EstadisticasAuditoriaDto obtenerEstadisticas() {
        List<RegistroAuditoriaDto> todos = listarLogs(null, null, null, null, null, 1000);
        long total = todos.size();
        long criticos = todos.stream().filter(r -> "CRITICO".equalsIgnoreCase(r.getNivelCriticidad())).count();
        long advertencias = todos.stream().filter(r -> "ADVERTENCIA".equalsIgnoreCase(r.getNivelCriticidad())).count();
        long informativos = todos.stream().filter(r -> "INFO".equalsIgnoreCase(r.getNivelCriticidad())).count();
        long asistencias = todos.stream().filter(r -> "ASISTENCIA".equalsIgnoreCase(r.getModulo())).count();
        long cargas = todos.stream().filter(r -> "CARGA_MASIVA".equalsIgnoreCase(r.getModulo())).count();
        long evaluaciones = todos.stream().filter(r -> "EVALUACION".equalsIgnoreCase(r.getModulo())).count();

        return new EstadisticasAuditoriaDto(
            total,
            criticos,
            advertencias,
            informativos,
            asistencias,
            cargas,
            evaluaciones
        );
    }
}

package com.tp1.proyecto.notas.servicio;

import com.tp1.proyecto.notas.dto.CargaExcelRespuestaDto;
import com.tp1.proyecto.usuario.entidad.Usuario;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de compatibilidad para carga masiva por Excel.
 * Se conserva como mecanismo opcional de importacion.
 */
@Deprecated(since = "v2", forRemoval = false)
public interface CargaExcelServicio {

    CargaExcelRespuestaDto registrarCarga(
        Long docenteId,
        Long periodoAcademicoId,
        Long periodoEvaluacionId,
        Long seccionId,
        MultipartFile archivo
    );

    CargaExcelRespuestaDto registrarCarga(
        Long docenteId,
        Long periodoAcademicoId,
        Long periodoEvaluacionId,
        Long seccionId,
        MultipartFile archivo,
        Usuario usuarioEjecutor
    );

    List<CargaExcelRespuestaDto> listarPorDocente(Long docenteId);
}

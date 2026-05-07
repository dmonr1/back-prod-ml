package com.tp1.proyecto.notas.servicio;

import com.tp1.proyecto.notas.entidad.CargaExcel;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Procesador legado de archivos Excel.
 * El flujo principal ahora se centra en evaluaciones parciales configurables.
 */
@Deprecated(since = "v2", forRemoval = false)
public interface ProcesadorExcelServicio {

    void procesar(CargaExcel cargaExcel, MultipartFile archivo) throws IOException;
}

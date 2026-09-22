package com.tp1.proyecto.documento.servicio;

import com.tp1.proyecto.documento.dto.TipoDocumentoRespuestaDto;
import com.tp1.proyecto.documento.entidad.TipoDocumento;
import java.util.List;

public interface TipoDocumentoServicio {

    List<TipoDocumentoRespuestaDto> listarActivos();

    TipoDocumento obtenerTipoDocumentoActivo(Long tipoDocumentoId);

    TipoDocumento obtenerDni();

    String validarNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento, boolean obligatorio);
}

package com.tp1.proyecto.documento.servicio.impl;

import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.documento.dto.TipoDocumentoRespuestaDto;
import com.tp1.proyecto.documento.entidad.TipoDocumento;
import com.tp1.proyecto.documento.repositorio.TipoDocumentoRepositorio;
import com.tp1.proyecto.documento.servicio.TipoDocumentoServicio;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TipoDocumentoServicioImpl implements TipoDocumentoServicio {

    private final TipoDocumentoRepositorio tipoDocumentoRepositorio;

    public TipoDocumentoServicioImpl(TipoDocumentoRepositorio tipoDocumentoRepositorio) {
        this.tipoDocumentoRepositorio = tipoDocumentoRepositorio;
    }

    @Override
    public List<TipoDocumentoRespuestaDto> listarActivos() {
        return tipoDocumentoRepositorio.findByEstadoOrderByCodigoAsc(EstadoRegistro.ACTIVO)
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public TipoDocumento obtenerTipoDocumentoActivo(Long tipoDocumentoId) {
        if (tipoDocumentoId == null) {
            return obtenerDni();
        }

        TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(tipoDocumentoId)
            .orElseThrow(() -> new ReglaNegocioException("Tipo de documento no encontrado."));
        if (tipoDocumento.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El tipo de documento seleccionado esta inactivo.");
        }
        return tipoDocumento;
    }

    @Override
    public TipoDocumento obtenerDni() {
        return tipoDocumentoRepositorio.findByCodigo("01")
            .filter(tipoDocumento -> tipoDocumento.getEstado() == EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new ReglaNegocioException("No se encontro el tipo de documento DNI."));
    }

    @Override
    public String validarNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento, boolean obligatorio) {
        String numeroNormalizado = numeroDocumento == null ? "" : numeroDocumento.trim().toUpperCase(Locale.ROOT);
        if (numeroNormalizado.isBlank()) {
            if (obligatorio) {
                throw new ReglaNegocioException("El numero de documento es obligatorio.");
            }
            return null;
        }

        if ("NUMERICO".equals(tipoDocumento.getTipo()) && !numeroNormalizado.matches("\\d+")) {
            throw new ReglaNegocioException("El " + tipoDocumento.getDescripcionCorta() + " solo admite numeros.");
        }
        if ("ALFANUMERICO".equals(tipoDocumento.getTipo()) && !numeroNormalizado.matches("[A-Z0-9]+")) {
            throw new ReglaNegocioException("El " + tipoDocumento.getDescripcionCorta() + " solo admite letras y numeros.");
        }

        int longitudEsperada = tipoDocumento.getLongitud();
        if (tipoDocumento.isLongitudExacta() && numeroNormalizado.length() != longitudEsperada) {
            throw new ReglaNegocioException(
                "El " + tipoDocumento.getDescripcionCorta() + " debe tener exactamente " + longitudEsperada + " caracteres."
            );
        }
        if (!tipoDocumento.isLongitudExacta() && numeroNormalizado.length() > longitudEsperada) {
            throw new ReglaNegocioException(
                "El " + tipoDocumento.getDescripcionCorta() + " no debe exceder " + longitudEsperada + " caracteres."
            );
        }
        return numeroNormalizado;
    }

    private TipoDocumentoRespuestaDto mapearRespuesta(TipoDocumento tipoDocumento) {
        TipoDocumentoRespuestaDto dto = new TipoDocumentoRespuestaDto();
        dto.setId(tipoDocumento.getId());
        dto.setCodigo(tipoDocumento.getCodigo());
        dto.setDescripcionLarga(tipoDocumento.getDescripcionLarga());
        dto.setDescripcionCorta(tipoDocumento.getDescripcionCorta());
        dto.setLongitud(tipoDocumento.getLongitud());
        dto.setTipo(tipoDocumento.getTipo());
        dto.setAlcanceNacionalidad(tipoDocumento.getAlcanceNacionalidad());
        dto.setLongitudExacta(tipoDocumento.isLongitudExacta());
        return dto;
    }
}

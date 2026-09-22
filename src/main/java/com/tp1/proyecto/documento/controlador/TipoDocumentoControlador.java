package com.tp1.proyecto.documento.controlador;

import com.tp1.proyecto.documento.dto.TipoDocumentoRespuestaDto;
import com.tp1.proyecto.documento.servicio.TipoDocumentoServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoControlador {

    private final TipoDocumentoServicio tipoDocumentoServicio;

    public TipoDocumentoControlador(TipoDocumentoServicio tipoDocumentoServicio) {
        this.tipoDocumentoServicio = tipoDocumentoServicio;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TipoDocumentoRespuestaDto> listarActivos() {
        return tipoDocumentoServicio.listarActivos();
    }
}

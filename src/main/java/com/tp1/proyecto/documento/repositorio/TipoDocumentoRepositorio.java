package com.tp1.proyecto.documento.repositorio;

import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.documento.entidad.TipoDocumento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Long> {

    List<TipoDocumento> findByEstadoOrderByCodigoAsc(EstadoRegistro estado);

    Optional<TipoDocumento> findByCodigo(String codigo);
}

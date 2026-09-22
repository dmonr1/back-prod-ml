package com.tp1.proyecto.docente.repositorio;

import com.tp1.proyecto.docente.entidad.Docente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepositorio extends JpaRepository<Docente, Long> {

    Optional<Docente> findByTipoDocumentoIdAndNumeroDocumento(Long tipoDocumentoId, String numeroDocumento);

    Optional<Docente> findByUsuarioId(Long usuarioId);
}

package com.tp1.proyecto.docente.servicio.impl;

import com.tp1.proyecto.docente.dto.DocenteRegistroSolicitudDto;
import com.tp1.proyecto.docente.dto.DocenteRespuestaDto;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.docente.servicio.DocenteServicio;
import com.tp1.proyecto.documento.entidad.TipoDocumento;
import com.tp1.proyecto.documento.servicio.TipoDocumentoServicio;
import com.tp1.proyecto.usuario.entidad.Rol;
import com.tp1.proyecto.usuario.entidad.Usuario;
import com.tp1.proyecto.usuario.repositorio.RolRepositorio;
import com.tp1.proyecto.usuario.repositorio.UsuarioRepositorio;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DocenteServicioImpl implements DocenteServicio {

    private final DocenteRepositorio docenteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final TipoDocumentoServicio tipoDocumentoServicio;

    public DocenteServicioImpl(
        DocenteRepositorio docenteRepositorio,
        UsuarioRepositorio usuarioRepositorio,
        RolRepositorio rolRepositorio,
        PasswordEncoder passwordEncoder,
        TipoDocumentoServicio tipoDocumentoServicio
    ) {
        this.docenteRepositorio = docenteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.tipoDocumentoServicio = tipoDocumentoServicio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocenteRespuestaDto> listar() {
        return docenteRepositorio.findAll()
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public DocenteRespuestaDto crear(DocenteRegistroSolicitudDto solicitud) {
        TipoDocumento tipoDocumento = tipoDocumentoServicio.obtenerTipoDocumentoActivo(solicitud.getTipoDocumentoId());
        String numeroDocumento = tipoDocumentoServicio.validarNumeroDocumento(
            tipoDocumento, solicitud.getNumeroDocumento(), true
        );
        String correo = limpiarCorreo(solicitud.getCorreo());

        docenteRepositorio.findByTipoDocumentoIdAndNumeroDocumento(tipoDocumento.getId(), numeroDocumento).ifPresent(docenteExistente -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un docente registrado con ese documento.");
        });

        usuarioRepositorio.findByCorreo(correo).ifPresent(usuarioExistente -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario registrado con ese correo.");
        });

        Rol rolDocente = rolRepositorio.findByNombre("DOCENTE")
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontro el rol DOCENTE."));

        Usuario usuario = new Usuario();
        usuario.setUsername(generarUsernameUnico(solicitud, numeroDocumento));
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(numeroDocumento));
        usuario.setDebeCambiarPassword(true);
        usuario.setRoles(new LinkedHashSet<>(List.of(rolDocente)));
        usuario = usuarioRepositorio.save(usuario);

        Docente docente = new Docente();
        docente.setUsuario(usuario);
        docente.setTipoDocumento(tipoDocumento);
        docente.setNumeroDocumento(numeroDocumento);
        docente.setNombres(limpiar(solicitud.getNombres()));
        docente.setApellidos(limpiar(solicitud.getApellidos()));
        docente.setTelefono(limpiarOpcional(solicitud.getTelefono()));
        docente.setEspecialidad(limpiarOpcional(solicitud.getEspecialidad()));

        Docente guardado = docenteRepositorio.save(docente);
        return mapearRespuesta(guardado);
    }

    private DocenteRespuestaDto mapearRespuesta(Docente docente) {
        DocenteRespuestaDto dto = new DocenteRespuestaDto();
        dto.setId(docente.getId());
        dto.setNumeroDocumento(docente.getNumeroDocumento());
        if (docente.getTipoDocumento() != null) {
            dto.setTipoDocumentoId(docente.getTipoDocumento().getId());
            dto.setTipoDocumentoCodigo(docente.getTipoDocumento().getCodigo());
            dto.setTipoDocumentoNombre(docente.getTipoDocumento().getDescripcionCorta());
        }
        dto.setNombres(docente.getNombres());
        dto.setApellidos(docente.getApellidos());
        dto.setTelefono(docente.getTelefono());
        dto.setEspecialidad(docente.getEspecialidad());
        dto.setEstado(docente.getEstado() != null ? docente.getEstado().name() : null);

        if (docente.getUsuario() != null) {
            dto.setUsuarioId(docente.getUsuario().getId());
            dto.setUsername(docente.getUsuario().getUsername());
            dto.setCorreo(docente.getUsuario().getCorreo());
        }

        return dto;
    }

    private String generarUsernameUnico(DocenteRegistroSolicitudDto solicitud, String numeroDocumento) {
        String base = normalizarUsername(
            extraerInicial(solicitud.getNombres()) + extraerPrimerApellido(solicitud.getApellidos())
        );

        if (base.isBlank()) {
            base = "docente" + numeroDocumento;
        }

        String candidato = base;
        int sufijo = 2;
        while (usuarioRepositorio.findByUsername(candidato).isPresent()) {
            candidato = base + sufijo;
            sufijo++;
        }
        return candidato;
    }

    private String extraerInicial(String valor) {
        String limpio = limpiar(valor);
        return limpio.isBlank() ? "d" : limpio.substring(0, 1);
    }

    private String extraerPrimerApellido(String valor) {
        String limpio = limpiar(valor);
        if (limpio.isBlank()) {
            return "docente";
        }
        String[] partes = limpio.split("\\s+");
        return partes[0];
    }

    private String normalizarUsername(String valor) {
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        String limpio = sinAcentos.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return limpio.length() > 50 ? limpio.substring(0, 50) : limpio;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarCorreo(String valor) {
        return limpiar(valor).toLowerCase(Locale.ROOT);
    }

    private String limpiarOpcional(String valor) {
        String limpio = limpiar(valor);
        return limpio.isBlank() ? null : limpio;
    }
}

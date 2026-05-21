package com.tp1.proyecto.usuario.servicio.impl;

import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.usuario.dto.UsuarioActualizacionSolicitudDto;
import com.tp1.proyecto.usuario.dto.UsuarioGestionRespuestaDto;
import com.tp1.proyecto.usuario.entidad.Rol;
import com.tp1.proyecto.usuario.entidad.Usuario;
import com.tp1.proyecto.usuario.repositorio.RolRepositorio;
import com.tp1.proyecto.usuario.repositorio.UsuarioRepositorio;
import com.tp1.proyecto.usuario.servicio.UsuarioServicio;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final DocenteRepositorio docenteRepositorio;
    private final TutoriaRepositorio tutoriaRepositorio;

    public UsuarioServicioImpl(
        UsuarioRepositorio usuarioRepositorio,
        RolRepositorio rolRepositorio,
        DocenteRepositorio docenteRepositorio,
        TutoriaRepositorio tutoriaRepositorio
    ) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.tutoriaRepositorio = tutoriaRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioGestionRespuestaDto> listar() {
        return usuarioRepositorio.findAll().stream()
            .map(this::mapear)
            .toList();
    }

    @Override
    public UsuarioGestionRespuestaDto actualizar(Long usuarioId, UsuarioActualizacionSolicitudDto solicitud) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        String username = limpiar(solicitud.getUsername());
        String correo = limpiarCorreo(solicitud.getCorreo());

        if (username.isBlank() || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username y correo son obligatorios.");
        }

        usuarioRepositorio.findByUsername(username).ifPresent(existente -> {
            if (!existente.getId().equals(usuario.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro usuario con ese username.");
            }
        });

        usuarioRepositorio.findByCorreo(correo).ifPresent(existente -> {
            if (!existente.getId().equals(usuario.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro usuario con ese correo.");
            }
        });

        Set<Rol> roles = new LinkedHashSet<>();
        for (String nombreRol : solicitud.getRoles()) {
            Rol rol = rolRepositorio.findByNombre(nombreRol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no valido: " + nombreRol));
            roles.add(rol);
        }

        Docente docenteVinculado = docenteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);
        if (docenteVinculado != null) {
            roles.add(obtenerRol("DOCENTE"));

            boolean tieneTutoriaActiva = tutoriaRepositorio.existsByDocenteIdAndEstado(
                docenteVinculado.getId(),
                EstadoRegistro.ACTIVO
            );
            if (tieneTutoriaActiva) {
                roles.add(obtenerRol("DOCENTE_TUTOR"));
            } else {
                roles.removeIf(rol -> "DOCENTE_TUTOR".equalsIgnoreCase(rol.getNombre()));
            }
        }

        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe conservar al menos un rol.");
        }

        usuario.setUsername(username);
        usuario.setCorreo(correo);
        usuario.setRoles(roles);
        return mapear(usuarioRepositorio.save(usuario));
    }

    @Override
    public UsuarioGestionRespuestaDto actualizarEstado(Long usuarioId, boolean activo) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (!activo && tieneRol(usuario, "ADMIN")) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Las cuentas con rol ADMIN no pueden inactivarse desde esta gestion."
            );
        }

        Docente docenteVinculado = docenteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);
        if (!activo && docenteVinculado != null && tutoriaRepositorio.existsByDocenteIdAndEstado(docenteVinculado.getId(), EstadoRegistro.ACTIVO)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No puedes inactivar un docente que mantiene una tutoria activa."
            );
        }

        EstadoRegistro nuevoEstado = activo ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO;
        usuario.setEstado(nuevoEstado);

        if (docenteVinculado != null) {
            docenteVinculado.setEstado(nuevoEstado);
            docenteRepositorio.save(docenteVinculado);
        }

        return mapear(usuarioRepositorio.save(usuario));
    }

    private UsuarioGestionRespuestaDto mapear(Usuario usuario) {
        UsuarioGestionRespuestaDto dto = new UsuarioGestionRespuestaDto();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.getEstado() != null ? usuario.getEstado().name() : EstadoRegistro.ACTIVO.name());
        dto.setDebeCambiarPassword(Boolean.TRUE.equals(usuario.getDebeCambiarPassword()));
        dto.setRoles(usuario.getRoles().stream().map(Rol::getNombre).sorted().toList());

        Docente docente = docenteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);
        if (docente != null) {
            dto.setDocenteId(docente.getId());
            dto.setDocenteNombreCompleto(docente.getApellidos() + ", " + docente.getNombres());
        }

        return dto;
    }

    private Rol obtenerRol(String nombreRol) {
        return rolRepositorio.findByNombre(nombreRol)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no valido: " + nombreRol));
    }

    private boolean tieneRol(Usuario usuario, String nombreRol) {
        return usuario.getRoles().stream().anyMatch(rol -> nombreRol.equalsIgnoreCase(rol.getNombre()));
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarCorreo(String valor) {
        return limpiar(valor).toLowerCase(Locale.ROOT);
    }
}

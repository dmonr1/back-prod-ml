package com.tp1.proyecto.seguridad.servicio.impl;

import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.seguridad.dto.CambiarPasswordInicialSolicitudDto;
import com.tp1.proyecto.seguridad.dto.LoginRespuestaDto;
import com.tp1.proyecto.seguridad.dto.LoginSolicitudDto;
import com.tp1.proyecto.seguridad.dto.UsuarioSesionDto;
import com.tp1.proyecto.seguridad.servicio.AuthServicio;
import com.tp1.proyecto.seguridad.servicio.JwtServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import com.tp1.proyecto.usuario.entidad.Usuario;
import com.tp1.proyecto.usuario.repositorio.UsuarioRepositorio;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthServicioImpl implements AuthServicio {

    private final AuthenticationManager authenticationManager;
    private final JwtServicio jwtServicio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final DocenteRepositorio docenteRepositorio;
    private final TutoriaRepositorio tutoriaRepositorio;
    private final PasswordEncoder passwordEncoder;

    public AuthServicioImpl(
        AuthenticationManager authenticationManager,
        JwtServicio jwtServicio,
        UsuarioRepositorio usuarioRepositorio,
        DocenteRepositorio docenteRepositorio,
        TutoriaRepositorio tutoriaRepositorio,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtServicio = jwtServicio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.tutoriaRepositorio = tutoriaRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginRespuestaDto login(LoginSolicitudDto solicitud) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(solicitud.getIdentificador(), solicitud.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        UsuarioAutenticado principal = (UsuarioAutenticado) authentication.getPrincipal();
        Usuario usuario = principal.getUsuario();
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepositorio.save(usuario);

        LoginRespuestaDto respuesta = new LoginRespuestaDto();
        respuesta.setToken(jwtServicio.generarToken(principal));
        respuesta.setTipoToken("Bearer");
        respuesta.setExpiracionSegundos(jwtServicio.getJwtExpiracionSegundos());
        respuesta.setUsuario(obtenerSesionActual(principal));
        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSesionDto obtenerSesionActual(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioAutenticado.getUsuario();
        Docente docente = docenteRepositorio.findByUsuarioId(usuario.getId()).orElse(null);

        UsuarioSesionDto dto = new UsuarioSesionDto();
        dto.setUsuarioId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setCorreo(usuario.getCorreo());
        dto.setRoles(usuarioAutenticado.getRoles());
        dto.setDebeCambiarPassword(Boolean.TRUE.equals(usuario.getDebeCambiarPassword()));
        dto.setPermisos(usuarioAutenticado.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .toList());

        if (docente != null) {
            dto.setDocenteId(docente.getId());
            dto.setEsTutor(tutoriaRepositorio.existsByDocenteId(docente.getId()));
        } else {
            dto.setEsTutor(Boolean.FALSE);
        }

        return dto;
    }

    @Override
    public UsuarioSesionDto cambiarPasswordInicial(
        UsuarioAutenticado usuarioAutenticado,
        CambiarPasswordInicialSolicitudDto solicitud
    ) {
        Usuario usuario = usuarioRepositorio.findById(usuarioAutenticado.getUsuario().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (!Boolean.TRUE.equals(usuario.getDebeCambiarPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tu usuario no tiene un cambio de password pendiente.");
        }

        String nuevaPassword = solicitud.getNuevaPassword() == null ? "" : solicitud.getNuevaPassword().trim();
        String confirmarPassword = solicitud.getConfirmarPassword() == null ? "" : solicitud.getConfirmarPassword().trim();

        if (nuevaPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva password debe tener al menos 8 caracteres.");
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La confirmacion de password no coincide.");
        }

        if (passwordEncoder.matches(nuevaPassword, usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva password debe ser distinta a la temporal.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setDebeCambiarPassword(false);
        usuarioRepositorio.save(usuario);

        return obtenerSesionActual(new UsuarioAutenticado(usuario));
    }
}

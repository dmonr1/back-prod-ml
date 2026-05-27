package com.tp1.proyecto.seguridad.servicio.impl;

import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.seguridad.dto.CambiarPasswordInicialSolicitudDto;
import com.tp1.proyecto.seguridad.dto.LoginRespuestaDto;
import com.tp1.proyecto.seguridad.dto.LoginSolicitudDto;
import com.tp1.proyecto.seguridad.dto.MensajeRespuestaDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionCambiarPasswordSolicitudDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionSolicitarSolicitudDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionTokenRespuestaDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionVerificarSolicitudDto;
import com.tp1.proyecto.seguridad.dto.UsuarioSesionDto;
import com.tp1.proyecto.seguridad.entidad.RecuperacionPasswordToken;
import com.tp1.proyecto.seguridad.repositorio.RecuperacionPasswordTokenRepositorio;
import com.tp1.proyecto.seguridad.servicio.AuthServicio;
import com.tp1.proyecto.seguridad.servicio.CorreoServicio;
import com.tp1.proyecto.seguridad.servicio.JwtServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import com.tp1.proyecto.usuario.entidad.Usuario;
import com.tp1.proyecto.usuario.repositorio.UsuarioRepositorio;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
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
    private final RecuperacionPasswordTokenRepositorio recuperacionPasswordTokenRepositorio;
    private final CorreoServicio correoServicio;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.password-reset.expiration-minutes:15}")
    private Integer recuperacionExpiracionMinutos;

    @Value("${app.security.password-reset.max-attempts:5}")
    private Integer recuperacionMaxIntentos;

    public AuthServicioImpl(
        AuthenticationManager authenticationManager,
        JwtServicio jwtServicio,
        UsuarioRepositorio usuarioRepositorio,
        DocenteRepositorio docenteRepositorio,
        TutoriaRepositorio tutoriaRepositorio,
        RecuperacionPasswordTokenRepositorio recuperacionPasswordTokenRepositorio,
        CorreoServicio correoServicio,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtServicio = jwtServicio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.tutoriaRepositorio = tutoriaRepositorio;
        this.recuperacionPasswordTokenRepositorio = recuperacionPasswordTokenRepositorio;
        this.correoServicio = correoServicio;
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

    @Override
    public MensajeRespuestaDto solicitarRecuperacion(RecuperacionSolicitarSolicitudDto solicitud) {
        String identificador = valorSeguro(solicitud.getIdentificador());
        String correo = valorSeguro(solicitud.getCorreo());

        Usuario usuario = usuarioRepositorio.findByUsernameOrCorreo(identificador, identificador)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (!usuario.getCorreo().equalsIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no coincide con el usuario registrado.");
        }

        invalidarTokensActivos(usuario.getId());

        RecuperacionPasswordToken token = new RecuperacionPasswordToken();
        token.setUsuario(usuario);
        token.setCodigo(generarCodigoRecuperacion());
        token.setExpiracion(LocalDateTime.now().plusMinutes(recuperacionExpiracionMinutos.longValue()));
        token.setUsado(Boolean.FALSE);
        token.setIntentos(0);
        token.setCodigoVerificado(Boolean.FALSE);
        token.setFechaCreacion(LocalDateTime.now());
        recuperacionPasswordTokenRepositorio.save(token);

        correoServicio.enviarCodigoRecuperacion(usuario.getCorreo(), token.getCodigo(), usuario.getUsername());

        return new MensajeRespuestaDto("Se envio un codigo de verificacion al correo registrado.");
    }

    @Override
    public RecuperacionTokenRespuestaDto verificarRecuperacion(RecuperacionVerificarSolicitudDto solicitud) {
        String identificador = valorSeguro(solicitud.getIdentificador());
        String codigo = valorSeguro(solicitud.getCodigo());

        Usuario usuario = usuarioRepositorio.findByUsernameOrCorreo(identificador, identificador)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        RecuperacionPasswordToken token = obtenerTokenActivo(usuario.getId());
        validarExpiracion(token);

        if (token.getIntentos() >= recuperacionMaxIntentos) {
            token.setUsado(Boolean.TRUE);
            recuperacionPasswordTokenRepositorio.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se excedio el numero maximo de intentos.");
        }

        if (!token.getCodigo().equals(codigo)) {
            token.setIntentos(token.getIntentos() + 1);
            recuperacionPasswordTokenRepositorio.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo de verificacion es invalido.");
        }

        token.setCodigoVerificado(Boolean.TRUE);
        token.setTokenRecuperacion(UUID.randomUUID().toString());
        recuperacionPasswordTokenRepositorio.save(token);

        RecuperacionTokenRespuestaDto respuesta = new RecuperacionTokenRespuestaDto();
        respuesta.setMensaje("Codigo verificado correctamente.");
        respuesta.setTokenRecuperacion(token.getTokenRecuperacion());
        return respuesta;
    }

    @Override
    public MensajeRespuestaDto cambiarPasswordRecuperacion(RecuperacionCambiarPasswordSolicitudDto solicitud) {
        String tokenRecuperacion = valorSeguro(solicitud.getTokenRecuperacion());
        String nuevaPassword = valorSeguro(solicitud.getNuevaPassword());
        String confirmarPassword = valorSeguro(solicitud.getConfirmarPassword());

        RecuperacionPasswordToken token = recuperacionPasswordTokenRepositorio
            .findByTokenRecuperacionAndUsadoFalse(tokenRecuperacion)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token de recuperacion es invalido."));

        validarExpiracion(token);

        if (!Boolean.TRUE.equals(token.getCodigoVerificado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primero debes validar el codigo de recuperacion.");
        }

        if (nuevaPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contrasena debe tener al menos 8 caracteres.");
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La confirmacion de contrasena no coincide.");
        }

        Usuario usuario = usuarioRepositorio.findById(token.getUsuario().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        if (passwordEncoder.matches(nuevaPassword, usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contrasena debe ser distinta a la actual.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setDebeCambiarPassword(Boolean.FALSE);
        usuarioRepositorio.save(usuario);

        token.setUsado(Boolean.TRUE);
        recuperacionPasswordTokenRepositorio.save(token);

        return new MensajeRespuestaDto("Contrasena actualizada correctamente.");
    }

    private RecuperacionPasswordToken obtenerTokenActivo(Long usuarioId) {
        return recuperacionPasswordTokenRepositorio.findTopByUsuarioIdAndUsadoFalseOrderByFechaCreacionDesc(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe una solicitud activa de recuperacion."));
    }

    private void invalidarTokensActivos(Long usuarioId) {
        recuperacionPasswordTokenRepositorio.findByUsuarioIdAndUsadoFalse(usuarioId)
            .forEach(token -> token.setUsado(Boolean.TRUE));
    }

    private void validarExpiracion(RecuperacionPasswordToken token) {
        if (token.getExpiracion().isBefore(LocalDateTime.now())) {
            token.setUsado(Boolean.TRUE);
            recuperacionPasswordTokenRepositorio.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo de recuperacion ha expirado.");
        }
    }

    private String generarCodigoRecuperacion() {
        int numero = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(numero);
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }
}

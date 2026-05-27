package com.tp1.proyecto.seguridad.servicio;

import com.tp1.proyecto.seguridad.dto.CambiarPasswordInicialSolicitudDto;
import com.tp1.proyecto.seguridad.dto.LoginRespuestaDto;
import com.tp1.proyecto.seguridad.dto.LoginSolicitudDto;
import com.tp1.proyecto.seguridad.dto.MensajeRespuestaDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionCambiarPasswordSolicitudDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionSolicitarSolicitudDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionTokenRespuestaDto;
import com.tp1.proyecto.seguridad.dto.RecuperacionVerificarSolicitudDto;
import com.tp1.proyecto.seguridad.dto.UsuarioSesionDto;

public interface AuthServicio {

    LoginRespuestaDto login(LoginSolicitudDto solicitud);

    UsuarioSesionDto obtenerSesionActual(UsuarioAutenticado usuarioAutenticado);

    UsuarioSesionDto cambiarPasswordInicial(
        UsuarioAutenticado usuarioAutenticado,
        CambiarPasswordInicialSolicitudDto solicitud
    );

    MensajeRespuestaDto solicitarRecuperacion(RecuperacionSolicitarSolicitudDto solicitud);

    RecuperacionTokenRespuestaDto verificarRecuperacion(RecuperacionVerificarSolicitudDto solicitud);

    MensajeRespuestaDto cambiarPasswordRecuperacion(RecuperacionCambiarPasswordSolicitudDto solicitud);
}

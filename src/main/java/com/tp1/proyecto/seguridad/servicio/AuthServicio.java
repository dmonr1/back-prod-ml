package com.tp1.proyecto.seguridad.servicio;

import com.tp1.proyecto.seguridad.dto.LoginRespuestaDto;
import com.tp1.proyecto.seguridad.dto.LoginSolicitudDto;
import com.tp1.proyecto.seguridad.dto.UsuarioSesionDto;

public interface AuthServicio {

    LoginRespuestaDto login(LoginSolicitudDto solicitud);

    UsuarioSesionDto obtenerSesionActual(UsuarioAutenticado usuarioAutenticado);
}

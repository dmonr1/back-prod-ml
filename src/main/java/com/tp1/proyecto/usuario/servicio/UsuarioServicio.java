package com.tp1.proyecto.usuario.servicio;

import com.tp1.proyecto.usuario.dto.UsuarioActualizacionSolicitudDto;
import com.tp1.proyecto.usuario.dto.UsuarioGestionRespuestaDto;
import java.util.List;

public interface UsuarioServicio {

    List<UsuarioGestionRespuestaDto> listar();

    UsuarioGestionRespuestaDto actualizar(Long usuarioId, UsuarioActualizacionSolicitudDto solicitud);

    UsuarioGestionRespuestaDto actualizarEstado(Long usuarioId, boolean activo);
}

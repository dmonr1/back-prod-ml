package com.tp1.proyecto.alerta.servicio;

import com.tp1.proyecto.alerta.dto.AlertaAcademicaRespuestaDto;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.util.List;

public interface AlertaAcademicaServicio {
    List<AlertaAcademicaRespuestaDto> listar(String estado, UsuarioAutenticado actor);
    long contarPendientes(UsuarioAutenticado actor);
    void detectarPendientes();
}

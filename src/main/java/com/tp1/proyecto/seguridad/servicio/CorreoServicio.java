package com.tp1.proyecto.seguridad.servicio;

public interface CorreoServicio {

    void enviarCodigoRecuperacion(String destino, String codigo, String username);
}

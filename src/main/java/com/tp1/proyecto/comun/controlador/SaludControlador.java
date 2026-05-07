package com.tp1.proyecto.comun.controlador;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SaludControlador {

    @GetMapping("/salud")
    public Map<String, Object> obtenerSalud() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("estado", "OK");
        respuesta.put("servicio", "rendimiento-academico-backend");
        respuesta.put("fecha", LocalDateTime.now().toString());
        return respuesta;
    }
}

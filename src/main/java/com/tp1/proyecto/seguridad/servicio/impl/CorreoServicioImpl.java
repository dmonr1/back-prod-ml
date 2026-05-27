package com.tp1.proyecto.seguridad.servicio.impl;

import com.tp1.proyecto.seguridad.servicio.CorreoServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoServicioImpl implements CorreoServicio {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from:}")
    private String remitente;

    public CorreoServicioImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void enviarCodigoRecuperacion(String destino, String codigo, String username) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (remitente != null && !remitente.isBlank()) {
            mensaje.setFrom(remitente);
        }
        mensaje.setTo(destino);
        mensaje.setSubject("Codigo de recuperacion de contrasena");
        mensaje.setText(
            "Hola " + username + ",\n\n"
                + "Tu codigo de recuperacion es: " + codigo + "\n"
                + "Este codigo vencera en pocos minutos.\n\n"
                + "Si no solicitaste este cambio, ignora este mensaje."
        );
        javaMailSender.send(mensaje);
    }
}

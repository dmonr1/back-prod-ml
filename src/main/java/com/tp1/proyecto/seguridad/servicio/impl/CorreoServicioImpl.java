package com.tp1.proyecto.seguridad.servicio.impl;

import com.tp1.proyecto.seguridad.servicio.CorreoServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoServicioImpl implements CorreoServicio {

    private static final Logger log = LoggerFactory.getLogger(CorreoServicioImpl.class);

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String remitente;

    public CorreoServicioImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void enviarCodigoRecuperacion(String destino, String codigo, String username) {
        if (!mailEnabled) {
            log.info("Envio de correo desactivado. Codigo de recuperacion para {}: {}", username, codigo);
            return;
        }

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
        try {
            javaMailSender.send(mensaje);
        } catch (MailException ex) {
            log.error("No se pudo enviar el codigo de recuperacion a {}", destino, ex);
            throw ex;
        }
    }
}

package com.tp1.proyecto.seguridad.servicio.impl;

import com.tp1.proyecto.seguridad.servicio.CorreoServicio;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CorreoServicioImpl implements CorreoServicio {

    private static final Logger log = LoggerFactory.getLogger(CorreoServicioImpl.class);
    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    private final JavaMailSender javaMailSender;
    private final WebClient webClient;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String remitente;

    @Value("${SENDGRID_API_KEY:}")
    private String sendGridApiKey;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public CorreoServicioImpl(JavaMailSender javaMailSender, WebClient.Builder webClientBuilder) {
        this.javaMailSender = javaMailSender;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void enviarCodigoRecuperacion(String destino, String codigo, String username) {
        if (!mailEnabled) {
            log.info("Envio de correo desactivado. Codigo de recuperacion para {}: {}", username, codigo);
            return;
        }

        String texto = "Hola " + username + ",\n\n"
            + "Tu codigo de recuperacion es: " + codigo + "\n"
            + "Este codigo vencera en pocos minutos.\n\n"
            + "Si no solicitaste este cambio, ignora este mensaje.";

        String apiKey = obtenerSendGridApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            enviarConSendGridApi(destino, username, texto, apiKey);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (remitente != null && !remitente.isBlank()) {
            mensaje.setFrom(remitente);
        }
        mensaje.setTo(destino);
        mensaje.setSubject("Codigo de recuperacion de contrasena");
        mensaje.setText(texto);
        try {
            javaMailSender.send(mensaje);
        } catch (MailException ex) {
            log.error("No se pudo enviar el codigo de recuperacion a {}", destino, ex);
            throw ex;
        }
    }

    private String obtenerSendGridApiKey() {
        if (sendGridApiKey != null && !sendGridApiKey.isBlank()) {
            return sendGridApiKey;
        }
        if ("smtp.sendgrid.net".equalsIgnoreCase(mailHost) && mailPassword != null && mailPassword.startsWith("SG.")) {
            return mailPassword;
        }
        return "";
    }

    private void enviarConSendGridApi(String destino, String username, String texto, String apiKey) {
        if (remitente == null || remitente.isBlank()) {
            throw new IllegalStateException("MAIL_FROM es obligatorio para enviar correo con SendGrid.");
        }

        Map<String, Object> payload = Map.of(
            "personalizations", List.of(Map.of(
                "to", List.of(Map.of("email", destino, "name", username))
            )),
            "from", Map.of("email", remitente),
            "subject", "Codigo de recuperacion de contrasena",
            "content", List.of(Map.of(
                "type", "text/plain",
                "value", texto
            ))
        );

        try {
            webClient.post()
                .uri(SENDGRID_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(apiKey))
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (RuntimeException ex) {
            log.error("No se pudo enviar el codigo de recuperacion por SendGrid API a {}", destino, ex);
            throw ex;
        }
    }
}

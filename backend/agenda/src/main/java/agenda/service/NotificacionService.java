package agenda.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import agenda.client.NegocioConsultaClient;
import agenda.model.ReservaEntity;
import agenda.model.ServicioEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String from;
    private final String businessToFallback;
    private final NegocioConsultaClient negocioConsultaClient;
    private final String resendApiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public NotificacionService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${slotone.mail.enabled:false}") boolean mailEnabled,
            @Value("${slotone.mail.from:no-reply@slotone.local}") String from,
            @Value("${slotone.mail.business-to:}") String businessToFallback,
            @Value("${slotone.mail.resend.api-key:}") String resendApiKey,
            ObjectMapper objectMapper,
            NegocioConsultaClient negocioConsultaClient) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailEnabled = mailEnabled;
        this.from = from;
        this.businessToFallback = businessToFallback;
        this.resendApiKey = resendApiKey != null ? resendApiKey.trim() : "";
        this.objectMapper = objectMapper;
        this.negocioConsultaClient = negocioConsultaClient;
    }

    @Async
    public void notificarReservaCreada(ReservaEntity reserva, ServicioEntity servicio) {
        String asuntoCliente = "Tu reserva fue confirmada - " + reserva.getCodigoReserva();
        String textoCliente = """
                Hola %s,

                Tu reserva fue confirmada.

                Código: %s
                Servicio: %s
                Fecha: %s
                Hora: %s - %s

                Gracias por usar SlotOne.
                """.formatted(
                reserva.getClienteNombre(),
                reserva.getCodigoReserva(),
                servicio.getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin());

        String htmlCliente = plantillaHtml(
                "Reserva confirmada",
                "Hola " + escapeHtml(reserva.getClienteNombre()) + ", tu cita quedó agendada correctamente.",
                reserva.getCodigoReserva(),
                """
                        <tr><td>Servicio</td><td>%s</td></tr>
                        <tr><td>Fecha</td><td>%s</td></tr>
                        <tr><td>Hora</td><td>%s - %s</td></tr>
                        <tr><td>Estado</td><td><span class="badge">Confirmada</span></td></tr>
                        """.formatted(
                        escapeHtml(servicio.getNombre()),
                        escapeHtml(String.valueOf(reserva.getFecha())),
                        escapeHtml(reserva.getHoraInicio()),
                        escapeHtml(reserva.getHoraFin())),
                "Puedes consultar tus reservas desde la plataforma SlotOne.");

        enviarCorreo(reserva.getClienteEmail(), asuntoCliente, textoCliente, htmlCliente);

        String asuntoNegocio = "Nueva reserva en SlotOne " + reserva.getCodigoReserva();
        String textoNegocio = """
                Se registró una nueva reserva para el negocio #%s.

                Código: %s
                Cliente: %s
                Email: %s
                Teléfono: %s
                Servicio: %s
                Fecha: %s
                Hora: %s - %s
                Notas: %s
                """.formatted(
                reserva.getNegocioId(),
                reserva.getCodigoReserva(),
                reserva.getClienteNombre(),
                reserva.getClienteEmail(),
                reserva.getClienteTelefono(),
                servicio.getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getNotas() == null || reserva.getNotas().isBlank() ? "Sin notas" : reserva.getNotas());

        String htmlNegocio = plantillaHtml(
                "Nueva reserva recibida",
                "Se registró una nueva cita para tu agenda. Revisa los detalles y prepárate para atender al cliente.",
                reserva.getCodigoReserva(),
                """
                        <tr><td>Cliente</td><td>%s</td></tr>
                        <tr><td>Email</td><td>%s</td></tr>
                        <tr><td>Teléfono</td><td>%s</td></tr>
                        <tr><td>Servicio</td><td>%s</td></tr>
                        <tr><td>Fecha</td><td>%s</td></tr>
                        <tr><td>Hora</td><td>%s - %s</td></tr>
                        <tr><td>Notas</td><td>%s</td></tr>
                        """.formatted(
                        escapeHtml(reserva.getClienteNombre()),
                        escapeHtml(reserva.getClienteEmail()),
                        escapeHtml(reserva.getClienteTelefono()),
                        escapeHtml(servicio.getNombre()),
                        escapeHtml(String.valueOf(reserva.getFecha())),
                        escapeHtml(reserva.getHoraInicio()),
                        escapeHtml(reserva.getHoraFin()),
                        escapeHtml(reserva.getNotas() == null || reserva.getNotas().isBlank()
                                ? "Sin notas"
                                : reserva.getNotas())),
                "Ingresa al panel de negocio para ver esta y tus próximas reservas.");

        String correoEmpresa =
                negocioConsultaClient.correoDeNegocio(reserva.getNegocioId()).orElse("");
        String fallbackTrim = businessToFallback == null ? "" : businessToFallback.trim();
        String destinatarioNegocio = !correoEmpresa.isBlank() ? correoEmpresa : fallbackTrim;
        if (!destinatarioNegocio.isBlank()) {
            enviarCorreo(destinatarioNegocio, asuntoNegocio, textoNegocio, htmlNegocio);
        } else {
            log.warn(
                    "No se envió email al negocio: el negocio no tiene correo cargado ni SLOTONE_MAIL_BUSINESS_TO (negocioId={}).",
                    reserva.getNegocioId());
        }
    }

    private void enviarCorreo(String to, String subject, String text, String html) {
        if (to == null || to.isBlank()) {
            log.warn("No se envió email porque el destinatario está vacío. Asunto: {}", subject);
            return;
        }
        if (!mailEnabled) {
            log.info("""
                    [EMAIL DESHABILITADO]
                    Para: {}
                    Asunto: {}
                    {}
                    """, to, subject, text);
            return;
        }

        if (StringUtils.hasText(resendApiKey)) {
            enviarCorreoResend(to, subject, text, html);
            return;
        }

        if (mailSender == null) {
            log.warn("Mail habilitado pero no hay JavaMailSender (SMTP). Asunto: {}", subject);
            return;
        }

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            mailSender.send(msg);
            log.info("Email enviado a {} con asunto '{}'.", to, subject);
        } catch (MailException | MessagingException e) {
            log.error("No se pudo enviar email a {} con asunto '{}': {}", to, subject, e.getMessage());
        }
    }

    private void enviarCorreoResend(String to, String subject, String text, String html) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("from", from);
            body.putArray("to").add(to);
            body.put("subject", subject);
            body.put("text", text);
            body.put("html", html);
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email enviado (Resend) a {} con asunto '{}'.", to, subject);
            } else {
                log.error(
                        "Resend rechazó el envío a {} con asunto '{}' (HTTP {}): {}",
                        to,
                        subject,
                        response.statusCode(),
                        response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Envío Resend interrumpido a {} con asunto '{}': {}", to, subject, e.getMessage());
        } catch (Exception e) {
            log.error("No se pudo enviar email (Resend) a {} con asunto '{}': {}", to, subject, e.getMessage());
        }
    }

    private String plantillaHtml(String titulo, String intro, String codigoReserva, String filas, String cierre) {
        return """
                <!doctype html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <style>
                    body { margin: 0; padding: 0; background: #f4f6fb; font-family: Arial, sans-serif; color: #1f2937; }
                    .wrap { max-width: 620px; margin: 0 auto; padding: 28px 16px; }
                    .card { background: #ffffff; border-radius: 14px; overflow: hidden; border: 1px solid #e5e7eb; }
                    .header { background: #1e293b; color: #ffffff; padding: 24px 28px; }
                    .brand { font-size: 13px; letter-spacing: 0.12em; text-transform: uppercase; color: #cbd5e1; margin: 0 0 8px; }
                    h1 { margin: 0; font-size: 24px; line-height: 1.25; }
                    .content { padding: 26px 28px; }
                    .intro { margin: 0 0 20px; line-height: 1.55; color: #475569; }
                    .code { display: inline-block; margin-bottom: 18px; padding: 8px 12px; border-radius: 999px; background: #eef2ff; color: #3730a3; font-weight: 700; font-size: 13px; }
                    table { width: 100%%; border-collapse: collapse; margin: 8px 0 20px; }
                    td { padding: 12px 0; border-bottom: 1px solid #eef2f7; vertical-align: top; }
                    td:first-child { width: 34%%; color: #64748b; font-size: 13px; }
                    td:last-child { color: #0f172a; font-weight: 600; font-size: 14px; }
                    .badge { display: inline-block; padding: 4px 9px; border-radius: 999px; background: #dcfce7; color: #166534; font-size: 12px; }
                    .footer { padding: 18px 28px; background: #f8fafc; color: #64748b; font-size: 12px; line-height: 1.5; }
                  </style>
                </head>
                <body>
                  <div class="wrap">
                    <div class="card">
                      <div class="header">
                        <p class="brand">SlotOne</p>
                        <h1>%s</h1>
                      </div>
                      <div class="content">
                        <p class="intro">%s</p>
                        <span class="code">Código de reserva: %s</span>
                        <table>%s</table>
                        <p class="intro">%s</p>
                      </div>
                      <div class="footer">
                        Este mensaje fue enviado automáticamente por SlotOne. Si no reconoces esta reserva, comunícate con el negocio.
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(titulo),
                intro,
                escapeHtml(codigoReserva),
                filas,
                escapeHtml(cierre));
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

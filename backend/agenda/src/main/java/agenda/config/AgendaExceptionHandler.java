package agenda.config;

import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Respuestas de error en JSON simple {@code { "message": "..." }} para que el front
 * siempre muestre el motivo (el proxy/gateway o ProblemDetail a veces dejaban solo "Conflict").
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AgendaExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        String reason = ex.getReason();
        if (reason == null || reason.isBlank()) {
            reason = mensajePorCodigo(ex.getStatusCode().value());
        }
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", reason));
    }

    private static String mensajePorCodigo(int code) {
        return switch (code) {
            case 409 ->
                    "Esta acción choca con datos ya guardados. Para horarios: no puede haber dos franjas solapadas el mismo día para el mismo servicio.";
            case 404 -> "No se encontró lo solicitado.";
            case 400 -> "Los datos enviados no son válidos.";
            case 403 -> "No tienes permiso para esta acción.";
            default -> "No se pudo completar la operación.";
        };
    }
}

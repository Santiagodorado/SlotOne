package usuarios.capaControladores.controladorExcepciones;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import usuarios.capaControladores.controladorExcepciones.estructuraExcepciones.CodigoError;
import usuarios.capaControladores.controladorExcepciones.estructuraExcepciones.Error;
import usuarios.capaControladores.controladorExcepciones.estructuraExcepciones.ErrorUtils;
import usuarios.capaControladores.controladorExcepciones.excepcionesPropias.EntidadNoExisteException;
import usuarios.capaControladores.controladorExcepciones.excepcionesPropias.EntidadYaExisteException;
import usuarios.capaControladores.controladorExcepciones.excepcionesPropias.ReglaNegocioExcepcion;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestApiExceptionHandler {

        // Respuesta simple para errores de autenticación
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, Object>> handleRuntimeException(final HttpServletRequest req,
                        final RuntimeException ex) {
                String message = ex.getMessage();
                HttpStatus status;
                
                // Verificar si es un error de autenticación específico
                if (message != null && (message.contains("Correo no registrado") || 
                                      message.contains("Contraseña incorrecta") ||
                                      message.contains("credenciales"))) {
                    status = HttpStatus.UNAUTHORIZED;
                } else {
                    status = HttpStatus.BAD_REQUEST;
                }
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", message != null ? message : "Error en el servidor");
                errorResponse.put("status", status.value());
                errorResponse.put("error", status.getReasonPhrase());
                errorResponse.put("path", req.getRequestURI());
                
                return new ResponseEntity<>(errorResponse, status);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                        final Exception ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ERROR_GENERICO.getCodigo(),
                                                CodigoError.ERROR_GENERICO.getLlaveMensaje(),
                                                HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(EntidadYaExisteException.class)
        public ResponseEntity<Error> handleEntidadYaExisteException(final HttpServletRequest req,
                        final EntidadYaExisteException ex) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ENTIDAD_YA_EXISTE.getCodigo(),
                                                String.format("%s, %s", CodigoError.ENTIDAD_YA_EXISTE.getLlaveMensaje(),
                                                                ex.getMessage()),
                                                HttpStatus.NOT_ACCEPTABLE.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }

        @ExceptionHandler(ReglaNegocioExcepcion.class)
        public ResponseEntity<Error> handleReglaNegocioException(final HttpServletRequest req,
                        final ReglaNegocioExcepcion ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.VIOLACION_REGLA_DE_NEGOCIO.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(EntidadNoExisteException.class)
        public ResponseEntity<Error> handleEntidadNoExisteException(final HttpServletRequest req,
                        final EntidadNoExisteException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ENTIDAD_NO_ENCONTRADA.getCodigo(),
                                                String.format("%s, %s",
                                                                CodigoError.ENTIDAD_NO_ENCONTRADA.getLlaveMensaje(),
                                                                ex.getMessage()),
                                                HttpStatus.NOT_FOUND.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
}

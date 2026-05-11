package agenda.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;

@Component
public class NegocioConsultaClient {

    private static final Logger log = LoggerFactory.getLogger(NegocioConsultaClient.class);

    private final RestClient negociosRestClient;

    public NegocioConsultaClient(@Qualifier("negociosRestClient") RestClient negociosRestClient) {
        this.negociosRestClient = negociosRestClient;
    }

    /** Correo del negocio para notificaciones, si está cargado en el microservicio negocios. */
    public Optional<String> correoDeNegocio(Long negocioId) {
        if (negocioId == null) {
            return Optional.empty();
        }
        try {
            NegocioExternoDTO body = negociosRestClient.get()
                    .uri("/api/negocios/{id}", negocioId)
                    .retrieve()
                    .body(NegocioExternoDTO.class);
            if (body == null || body.getCorreo() == null || body.getCorreo().isBlank()) {
                return Optional.empty();
            }
            return Optional.of(body.getCorreo().trim());
        } catch (RestClientException e) {
            log.warn("No se pudo consultar correo del negocio {}: {}", negocioId, e.getMessage());
            return Optional.empty();
        }
    }
}

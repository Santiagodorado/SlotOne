package agenda.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/** Respuesta parcial del microservicio negocios ({@code GET /api/negocios/{id}}). */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NegocioExternoDTO {

    private Long id;
    private String nombre;
    /** Correo de contacto del negocio (alertas internas). */
    private String correo;
}

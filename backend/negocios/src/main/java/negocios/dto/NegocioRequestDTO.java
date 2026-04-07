package negocios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegocioRequestDTO {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotBlank
    private String direccion;

    @NotBlank
    private String telefono;

    private String logoUrl;

    /**
     * Identificador del usuario dueño del negocio.
     * En una siguiente iteración se obtendrá del JWT.
     */
    @NotNull
    private Long duenioId;
}


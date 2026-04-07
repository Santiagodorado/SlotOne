package agenda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioRequestDTO {

    @NotNull
    private Long negocioId;

    @NotBlank
    private String nombre;

    @NotNull
    @Min(1)
    private Integer duracionMinutos;

    @NotNull
    @Min(0)
    private Double precio;

    private String descripcion;
}


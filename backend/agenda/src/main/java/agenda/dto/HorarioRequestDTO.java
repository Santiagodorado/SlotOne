package agenda.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioRequestDTO {

    @NotNull
    private Long servicioId;

    @NotNull
    @Min(0)
    @Max(6)
    private Integer diaSemana;

    @NotBlank
    private String horaInicio;

    @NotBlank
    private String horaFin;
}


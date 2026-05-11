package agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioResponseDTO {

    private Long id;
    private Long negocioId;
    private Long servicioId;
    private Integer diaSemana;
    private String horaInicio;
    private String horaFin;
}


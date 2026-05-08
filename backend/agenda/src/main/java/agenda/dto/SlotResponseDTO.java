package agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponseDTO {

    private String horaInicio;
    private String horaFin;
}

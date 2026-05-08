package agenda.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponseDTO {

    private Long servicioId;
    private String fecha;
    private List<SlotResponseDTO> slots;
}

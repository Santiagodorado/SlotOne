package agenda.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorResponseDTO {

    private Long id;
    private Long negocioId;
    private String nombre;
    private String email;
    private String telefono;
    private Boolean activo;
    private List<Long> servicioIds;
}

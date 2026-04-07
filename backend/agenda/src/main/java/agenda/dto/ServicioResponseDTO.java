package agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponseDTO {

    private Long id;
    private Long negocioId;
    private String nombre;
    private Integer duracionMinutos;
    private Double precio;
    private String descripcion;
}


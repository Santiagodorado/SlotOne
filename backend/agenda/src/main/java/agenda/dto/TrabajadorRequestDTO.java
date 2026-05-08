package agenda.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorRequestDTO {

    @NotNull
    private Long negocioId;

    @NotBlank
    private String nombre;

    private String email;
    private String telefono;
    private Boolean activo;
    private List<Long> servicioIds;
}

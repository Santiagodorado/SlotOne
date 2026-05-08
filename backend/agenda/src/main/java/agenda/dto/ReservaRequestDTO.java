package agenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull
    private Long servicioId;

    private Long trabajadorId;

    private Long clienteId;

    @NotBlank
    private String clienteNombre;

    @NotBlank
    @Email
    private String clienteEmail;

    @NotBlank
    private String clienteTelefono;

    @NotBlank
    private String fecha;

    @NotBlank
    private String horaInicio;

    private String notas;
}

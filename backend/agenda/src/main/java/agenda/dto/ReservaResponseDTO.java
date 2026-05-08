package agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private String codigoReserva;
    private Long negocioId;
    private Long servicioId;
    private Long trabajadorId;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private String clienteTelefono;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private String notas;
}

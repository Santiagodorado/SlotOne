package agenda.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoReserva;
    private Long negocioId;
    private Long servicioId;
    private Long trabajadorId;
    private Long clienteId;

    private String clienteNombre;
    private String clienteEmail;
    private String clienteTelefono;

    private LocalDate fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private String notas;
}

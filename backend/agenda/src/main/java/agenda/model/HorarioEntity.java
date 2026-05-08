package agenda.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "horario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long negocioId;

    /** Servicio al que aplica este tramo (horarios por servicio, no solapados el mismo día). */
    private Long servicioId;

    /**
     * Día de la semana (0=Domingo, 1=Lunes, ..., 6=Sábado)
     */
    private Integer diaSemana;

    /**
     * Hora de inicio en formato HH:mm (ej: \"08:00\").
     */
    private String horaInicio;

    /**
     * Hora de fin en formato HH:mm (ej: \"18:00\").
     */
    private String horaFin;
}


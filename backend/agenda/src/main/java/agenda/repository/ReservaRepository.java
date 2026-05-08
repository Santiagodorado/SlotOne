package agenda.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import agenda.model.ReservaEntity;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByServicioIdAndFechaAndEstadoNot(Long servicioId, LocalDate fecha, String estado);

    List<ReservaEntity> findByTrabajadorIdAndFechaAndEstadoNot(Long trabajadorId, LocalDate fecha, String estado);

    List<ReservaEntity> findByClienteIdOrderByFechaDescHoraInicioDesc(Long clienteId);

    List<ReservaEntity> findByNegocioIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(
            Long negocioId, LocalDate desde, LocalDate hasta);
}

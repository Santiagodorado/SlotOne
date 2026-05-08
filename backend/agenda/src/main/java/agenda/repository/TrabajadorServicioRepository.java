package agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import agenda.model.TrabajadorServicioEntity;

public interface TrabajadorServicioRepository extends JpaRepository<TrabajadorServicioEntity, Long> {

    List<TrabajadorServicioEntity> findByTrabajadorId(Long trabajadorId);

    List<TrabajadorServicioEntity> findByServicioId(Long servicioId);

    boolean existsByTrabajadorIdAndServicioId(Long trabajadorId, Long servicioId);

    void deleteByTrabajadorId(Long trabajadorId);
}

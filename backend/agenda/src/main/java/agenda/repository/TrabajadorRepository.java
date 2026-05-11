package agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import agenda.model.TrabajadorEntity;

public interface TrabajadorRepository extends JpaRepository<TrabajadorEntity, Long> {

    List<TrabajadorEntity> findByNegocioId(Long negocioId);

    List<TrabajadorEntity> findByNegocioIdAndActivoTrue(Long negocioId);
}

package agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import agenda.model.ServicioEntity;

public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {

    List<ServicioEntity> findByNegocioId(Long negocioId);
}


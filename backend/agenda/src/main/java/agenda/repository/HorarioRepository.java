package agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import agenda.model.HorarioEntity;

public interface HorarioRepository extends JpaRepository<HorarioEntity, Long> {

    List<HorarioEntity> findByNegocioId(Long negocioId);

    List<HorarioEntity> findByServicioIdAndDiaSemana(Long servicioId, Integer diaSemana);

    void deleteByServicioId(Long servicioId);
}


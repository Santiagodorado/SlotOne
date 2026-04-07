package negocios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import negocios.model.NegocioEntity;

public interface NegocioRepository extends JpaRepository<NegocioEntity, Long> {

    List<NegocioEntity> findByDuenioId(Long duenioId);
}


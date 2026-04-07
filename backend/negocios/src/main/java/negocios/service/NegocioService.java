package negocios.service;

import java.util.List;

import negocios.dto.NegocioRequestDTO;
import negocios.dto.NegocioResponseDTO;

public interface NegocioService {

    List<NegocioResponseDTO> listarTodos();

    List<NegocioResponseDTO> listarPorDuenio(Long duenioId);

    NegocioResponseDTO crear(NegocioRequestDTO dto);

    NegocioResponseDTO actualizar(Long id, NegocioRequestDTO dto);
}


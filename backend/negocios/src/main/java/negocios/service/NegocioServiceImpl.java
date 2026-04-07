package negocios.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import negocios.dto.NegocioRequestDTO;
import negocios.dto.NegocioResponseDTO;
import negocios.model.NegocioEntity;
import negocios.repository.NegocioRepository;

@Service
@RequiredArgsConstructor
public class NegocioServiceImpl implements NegocioService {

    private final NegocioRepository repository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NegocioResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NegocioResponseDTO> listarPorDuenio(Long duenioId) {
        return repository.findByDuenioId(duenioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NegocioResponseDTO crear(NegocioRequestDTO dto) {
        if (!repository.findByDuenioId(dto.getDuenioId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este usuario ya tiene un negocio registrado (máximo uno).");
        }
        // Entidad nueva sin ModelMapper: evita merge con id basura y StaleObjectStateException en save.
        NegocioEntity entity = new NegocioEntity();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setDuenioId(dto.getDuenioId());
        NegocioEntity guardado = repository.save(entity);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public NegocioResponseDTO actualizar(Long id, NegocioRequestDTO dto) {
        NegocioEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Negocio no encontrado."));
        if (!entity.getDuenioId().equals(dto.getDuenioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar este negocio.");
        }
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setLogoUrl(dto.getLogoUrl());
        return toResponse(repository.save(entity));
    }

    private NegocioResponseDTO toResponse(NegocioEntity entity) {
        return modelMapper.map(entity, NegocioResponseDTO.class);
    }
}


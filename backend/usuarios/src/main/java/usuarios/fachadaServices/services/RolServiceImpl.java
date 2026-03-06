package usuarios.fachadaServices.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import usuarios.capaAccesoADatos.models.RolEntity;
import usuarios.capaAccesoADatos.repositories.RolRepository;
import usuarios.fachadaServices.DTO.peticion.RolDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.RolDTORespuesta;
import usuarios.fachadaServices.interfaces.IRolService;

@Service
public class RolServiceImpl implements IRolService {

    private final RolRepository repository;
    private final ModelMapper modelMapper;

    public RolServiceImpl(RolRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RolDTORespuesta save(RolDTOPeticion dto) {
        RolEntity nuevo = modelMapper.map(dto, RolEntity.class);
        RolEntity rolGuardado = repository.save(nuevo);
        RolDTORespuesta rolDTO = modelMapper.map(rolGuardado, RolDTORespuesta.class);
        return rolDTO;
    }

    @Override
    public RolDTORespuesta findById(Integer id) {
        RolDTORespuesta rolRetornar = null;
        Optional<RolEntity> optionalRol = repository.findById(id);
        if (optionalRol.isPresent()) {
            RolEntity rol = optionalRol.get();
            rolRetornar = modelMapper.map(rol, RolDTORespuesta.class);
        }
        return rolRetornar;
    }

    @Override
    public List<RolDTORespuesta> findAll() {
        List<RolDTORespuesta> listaRetornar;
        Optional<Collection<RolEntity>> roles = repository.findAll();
        if (roles.isEmpty()) {
            listaRetornar = List.of();
        } else {
            Collection<RolEntity> rolesEntity = roles.get();
            listaRetornar = this.modelMapper.map(rolesEntity, new TypeToken<List<RolDTORespuesta>>() {
            }.getType());
        }
        return listaRetornar;
    }

    public RolDTORespuesta update(Integer id, RolDTOPeticion dto) {
        RolDTORespuesta rolActualizado = null;
        Optional<RolEntity> optionalRol = repository.findById(id);
        if (optionalRol.isPresent()) {
            RolEntity rolExistente = optionalRol.get();
            rolExistente.setNombre(dto.getNombre());
                    
            Optional<RolEntity> optionalRolActualizado = repository.update(id, rolExistente);
            rolActualizado = modelMapper.map(optionalRolActualizado.get(), RolDTORespuesta.class);
        }
        return modelMapper.map(rolActualizado, RolDTORespuesta.class);
    }

    @Override
    public boolean delete(Integer id) {
        return repository.delete(id);
    }
}

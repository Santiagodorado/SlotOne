package usuarios.fachadaServices.interfaces;

import java.util.List;

import usuarios.fachadaServices.DTO.peticion.RolDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.RolDTORespuesta;

public interface IRolService {
    List<RolDTORespuesta> findAll();

    RolDTORespuesta findById(Integer id);

    RolDTORespuesta save(RolDTOPeticion dto);

    RolDTORespuesta update(Integer id, RolDTOPeticion dto);

    boolean delete(Integer id);
}

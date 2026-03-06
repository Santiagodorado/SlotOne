package usuarios.fachadaServices.interfaces;

import java.util.List;
import java.util.Optional;

import usuarios.capaAccesoADatos.models.UsuarioEntity;
import usuarios.fachadaServices.DTO.peticion.LoginRequestDTO;
import usuarios.fachadaServices.DTO.peticion.UsuarioDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.JwtDTORespuesta;
import usuarios.fachadaServices.DTO.respuesta.UsuarioDTORespuesta;

public interface IUsuarioService {

    List<UsuarioDTORespuesta> findAll();

    UsuarioDTORespuesta findById(Integer id);

    UsuarioDTORespuesta save(UsuarioDTOPeticion dto);

    UsuarioDTORespuesta update(Integer id, UsuarioDTOPeticion dto);

    boolean delete(Integer id);

    UsuarioDTORespuesta login(String email, String password);

    Optional<UsuarioEntity> findByEmail(String email);
    
    JwtDTORespuesta autenticacionUsuario(LoginRequestDTO loginRequest);
}

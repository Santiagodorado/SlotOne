package usuarios.fachadaServices.DTO.peticion;

import usuarios.capaAccesoADatos.models.UsuarioEntity.TipoIdentificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTOPeticion {
    private String nombres;
    private String apellidos;
    private String correo;
    private String clave;
    private TipoIdentificacion tipoIdentificacion;
    private Integer numIdentificacion;
    private Integer idRol;
}
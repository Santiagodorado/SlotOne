package usuarios.fachadaServices.DTO.respuesta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usuarios.capaAccesoADatos.models.UsuarioEntity.TipoIdentificacion;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTORespuesta {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String correo;
    private TipoIdentificacion tipoIdentificacion;
    private Integer numIdentificacion;
    private RolDTORespuesta rol;
}

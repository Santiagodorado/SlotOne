package usuarios.capaAccesoADatos.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String clave;
    private TipoIdentificacion tipoIdentificacion;
    private Integer numIdentificacion;

    private RolEntity rol;

    public enum TipoIdentificacion {
        CC,
        CE
    }
}

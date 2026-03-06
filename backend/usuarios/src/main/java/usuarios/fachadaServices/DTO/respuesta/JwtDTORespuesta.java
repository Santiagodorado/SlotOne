package usuarios.fachadaServices.DTO.respuesta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTORespuesta {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String tipoIdentificacion;
    private String numIdentificacion;
    private String rol;

    public JwtDTORespuesta(String token, Integer id, String nombres, String apellidos, String correo,
            String tipoIdentificacion, String numIdentificacion, String rol) {
        this.token = token;
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.tipoIdentificacion = tipoIdentificacion;
        this.numIdentificacion = numIdentificacion;
        this.rol = rol;
    }
} 
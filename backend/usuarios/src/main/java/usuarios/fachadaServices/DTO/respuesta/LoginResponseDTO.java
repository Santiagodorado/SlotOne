package usuarios.fachadaServices.DTO.respuesta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String email;
    private String role;
    private Integer id;
    private String nombre;
    private String apellidos;
    private String tipoIdentificacion;
    private String numIdentificacion;
} 
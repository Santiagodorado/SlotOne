package usuarios.capaAccesoADatos.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolEntity {
    private Integer id;
    private String nombre;
}

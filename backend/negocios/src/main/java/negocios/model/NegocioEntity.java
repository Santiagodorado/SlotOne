package negocios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "negocio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegocioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String direccion;
    private String telefono;

    /** Data URL o URL externa; LOB para caber base64 sin recortar. */
    @Lob
    @Column(name = "logo_url")
    private String logoUrl;

    private Long duenioId;
}


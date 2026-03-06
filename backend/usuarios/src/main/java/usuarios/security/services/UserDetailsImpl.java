package usuarios.security.services;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import usuarios.capaAccesoADatos.models.UsuarioEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String nombres;
  private String apellidos;
  private String correo;
  private String tipoIdentificacion;
  private String numIdentificacion;

  @JsonIgnore
  private String clave;

  private Collection<? extends GrantedAuthority> authorities;

  public static UserDetailsImpl build(UsuarioEntity user) {
    List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(user.getRol().getNombre())
    );

    return new UserDetailsImpl(
        user.getId(),
        user.getNombres(),
        user.getApellidos(),
        user.getCorreo(),
        user.getTipoIdentificacion().toString(),
        user.getNumIdentificacion().toString(),
        user.getClave(),
        authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return clave;
  }

  @Override
  public String getUsername() {
    return correo;
  }
}

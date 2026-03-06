package usuarios.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usuarios.capaAccesoADatos.models.UsuarioEntity;
import usuarios.capaAccesoADatos.repositories.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UsuarioRepository usuarioRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UsuarioEntity user = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

    return UserDetailsImpl.build(user);
  }

}

package usuarios.fachadaServices.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import usuarios.capaAccesoADatos.models.UsuarioEntity;
import usuarios.capaAccesoADatos.repositories.RolRepository;
import usuarios.capaAccesoADatos.repositories.UsuarioRepository;
import usuarios.fachadaServices.DTO.peticion.LoginRequestDTO;
import usuarios.fachadaServices.DTO.peticion.UsuarioDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.JwtDTORespuesta;
import usuarios.fachadaServices.DTO.respuesta.UsuarioDTORespuesta;
import usuarios.fachadaServices.interfaces.IUsuarioService;
import usuarios.security.services.UserDetailsImpl;
import usuarios.security.jwt.JwtUtils;


@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    UsuarioRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioDTORespuesta> findAll() {
        List<UsuarioEntity> usuarios = repository.findAll()
            .map(collection -> new ArrayList<>(collection))
            .orElse(new ArrayList<>());
        return usuarios.stream()
            .map(u -> modelMapper.map(u, UsuarioDTORespuesta.class))
            .toList();
    }

    @Override
    public UsuarioDTORespuesta findById(Integer id) {
        UsuarioEntity encontrado = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return modelMapper.map(encontrado, UsuarioDTORespuesta.class);
    }

    @Override
    public UsuarioDTORespuesta save(UsuarioDTOPeticion dto) {
        UsuarioEntity nuevo = modelMapper.map(dto, UsuarioEntity.class);
        
        // Encriptar la contraseña antes de guardar
        String claveEncriptada = passwordEncoder.encode(dto.getClave());
        nuevo.setClave(claveEncriptada);
        
        nuevo.setRol(rolRepository.findById(dto.getIdRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        UsuarioEntity usuarioGuardado = repository.save(nuevo);
        UsuarioDTORespuesta usuarioDTO = modelMapper.map(usuarioGuardado, UsuarioDTORespuesta.class);
        return usuarioDTO;
    }

    @Override
    public UsuarioDTORespuesta update(Integer id, UsuarioDTOPeticion dto) {
        UsuarioEntity usuarioActualizado = null;
        Optional<UsuarioEntity> usuarioExistente = repository.findById(id);
        if (usuarioExistente.isPresent()) {
            UsuarioEntity objUsuarioDatosNuevos = usuarioExistente.get();
            objUsuarioDatosNuevos.setNombres(dto.getNombres());
            objUsuarioDatosNuevos.setApellidos(dto.getApellidos());
            objUsuarioDatosNuevos.setCorreo(dto.getCorreo());
            objUsuarioDatosNuevos.setClave(dto.getClave());
            objUsuarioDatosNuevos.setTipoIdentificacion(dto.getTipoIdentificacion());
            objUsuarioDatosNuevos.setNumIdentificacion(dto.getNumIdentificacion());
            objUsuarioDatosNuevos.setRol(rolRepository.findById(dto.getIdRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado")));

            Optional<UsuarioEntity> optionalUsuario = this.repository.update(id, objUsuarioDatosNuevos);
            usuarioActualizado = optionalUsuario.get();
        } 
        return this.modelMapper.map(usuarioActualizado, UsuarioDTORespuesta.class);
    }

    @Override
    public boolean delete(Integer id) {
        return this.repository.delete(id);
    }

    @Override
    public UsuarioDTORespuesta login(String email, String password) {
        Optional<UsuarioEntity> usuario = repository.findByEmailAndPassword(email, password);
        if (usuario.isPresent()) {
            return modelMapper.map(usuario.get(), UsuarioDTORespuesta.class);
        }
        return null;
    }

    @Override
    public JwtDTORespuesta autenticacionUsuario(LoginRequestDTO loginRequest) {

        // 1. Buscar usuario por correo
        Optional<UsuarioEntity> usuario = repository.findByEmail(loginRequest.getEmail());
        
        // 2. Validar si el correo existe
        if (!usuario.isPresent()) {
            throw new RuntimeException("Correo no registrado");
        }
        
        // 3. Validar la contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.get().getClave())) {   
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 4. Si las validaciones pasan, proceder con la autenticación
        Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return new JwtDTORespuesta(jwt,
        userDetails.getId(),
        userDetails.getNombres(),
        userDetails.getApellidos(),
        userDetails.getCorreo(),
        userDetails.getTipoIdentificacion(),
        userDetails.getNumIdentificacion(),
        roles.get(0));
    }

    @Override
    public Optional<UsuarioEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}

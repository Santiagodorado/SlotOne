package usuarios.capaControladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usuarios.fachadaServices.DTO.peticion.LoginRequestDTO;
import usuarios.fachadaServices.DTO.peticion.UsuarioDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.JwtDTORespuesta;
import usuarios.fachadaServices.DTO.respuesta.UsuarioDTORespuesta;
import usuarios.fachadaServices.interfaces.IUsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {    
        JwtDTORespuesta token = this.usuarioService.autenticacionUsuario(loginRequest);
        return ResponseEntity.ok(token);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioDTORespuesta>> listarUsuarios() {
        List<UsuarioDTORespuesta> usuarios = usuarioService.findAll();
        ResponseEntity<List<UsuarioDTORespuesta>> response = new ResponseEntity<List<UsuarioDTORespuesta>>(usuarios, HttpStatus.OK);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTORespuesta> consultarUsuario(@PathVariable Integer id) {
        UsuarioDTORespuesta usuario = null;
        usuario = usuarioService.findById(id);
        ResponseEntity<UsuarioDTORespuesta> response = new ResponseEntity<UsuarioDTORespuesta>(usuario, HttpStatus.OK);
        return response;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTORespuesta> crearUsuario(@RequestBody UsuarioDTOPeticion usuario) {
        UsuarioDTORespuesta nuevoUsuario = usuarioService.save(usuario);
        ResponseEntity<UsuarioDTORespuesta> response = new ResponseEntity<UsuarioDTORespuesta>(nuevoUsuario, HttpStatus.CREATED);
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTORespuesta> actualizarUsuario(@PathVariable Integer id,
            @RequestBody UsuarioDTOPeticion usuario) {
        UsuarioDTORespuesta usuarioActualizado = usuarioService.update(id, usuario);
        ResponseEntity<UsuarioDTORespuesta> response = new ResponseEntity<UsuarioDTORespuesta>(usuarioActualizado, HttpStatus.OK);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarUsuario(@PathVariable Integer id) {
        Boolean bandera = usuarioService.delete(id);
        ResponseEntity<Boolean> response = new ResponseEntity<Boolean>(bandera, HttpStatus.NO_CONTENT);
        return response;
    }
}

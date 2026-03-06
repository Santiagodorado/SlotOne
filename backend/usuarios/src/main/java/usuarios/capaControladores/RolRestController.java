package usuarios.capaControladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import usuarios.fachadaServices.DTO.peticion.RolDTOPeticion;
import usuarios.fachadaServices.DTO.respuesta.RolDTORespuesta;
import usuarios.fachadaServices.interfaces.IRolService;

@RestController
@RequestMapping("/api/roles")
public class RolRestController {

    @Autowired
    private IRolService rolService;

    @PostMapping
    public ResponseEntity<RolDTORespuesta> save(@RequestBody RolDTOPeticion dto) {
        RolDTORespuesta rolCreado = rolService.save(dto);
        return new ResponseEntity<>(rolCreado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTORespuesta> findById(@PathVariable Integer id) {
        RolDTORespuesta rol = rolService.findById(id);
        return new ResponseEntity<>(rol, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RolDTORespuesta>> findAll() {
        List<RolDTORespuesta> roles = rolService.findAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<RolDTORespuesta> update(@PathVariable Integer id, @RequestBody RolDTOPeticion dto) {
        RolDTORespuesta rolActualizado = rolService.update(id, dto);
        return new ResponseEntity<>(rolActualizado, HttpStatus.OK);
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        boolean eliminado = rolService.delete(id);
        return new ResponseEntity<>(eliminado, HttpStatus.OK);
    }
}   

package usuarios.capaAccesoADatos.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UsuarioEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        RolEntity rol = new RolEntity(1, "NEGOCIO");
        UsuarioEntity entity = new UsuarioEntity(1, "Juan", "Perez", "juan@email.com", "3001234567", "clave123",
                UsuarioEntity.TipoIdentificacion.CC, 123456789, rol);

        assertEquals(1, entity.getId());
        assertEquals("Juan", entity.getNombres());
        assertEquals("Perez", entity.getApellidos());
        assertEquals("juan@email.com", entity.getCorreo());
        assertEquals("3001234567", entity.getTelefono());
        assertEquals("clave123", entity.getClave());
        assertEquals(UsuarioEntity.TipoIdentificacion.CC, entity.getTipoIdentificacion());
        assertEquals(123456789, entity.getNumIdentificacion());
        assertEquals(rol, entity.getRol());
    }

    @Test
    void shouldSetAndGetFields() {
        RolEntity rol = new RolEntity(2, "CLIENTE");
        UsuarioEntity entity = new UsuarioEntity();

        entity.setId(20);
        entity.setNombres("Ana");
        entity.setApellidos("Gomez");
        entity.setCorreo("ana@email.com");
        entity.setTelefono("3110000000");
        entity.setClave("secreta");
        entity.setTipoIdentificacion(UsuarioEntity.TipoIdentificacion.CE);
        entity.setNumIdentificacion(99887766);
        entity.setRol(rol);

        assertEquals(20, entity.getId());
        assertEquals("Ana", entity.getNombres());
        assertEquals("Gomez", entity.getApellidos());
        assertEquals("ana@email.com", entity.getCorreo());
        assertEquals("3110000000", entity.getTelefono());
        assertEquals("secreta", entity.getClave());
        assertEquals(UsuarioEntity.TipoIdentificacion.CE, entity.getTipoIdentificacion());
        assertEquals(99887766, entity.getNumIdentificacion());
        assertEquals(rol, entity.getRol());
    }
}

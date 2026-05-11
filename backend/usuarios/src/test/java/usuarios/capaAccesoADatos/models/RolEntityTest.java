package usuarios.capaAccesoADatos.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RolEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        RolEntity entity = new RolEntity(1, "ADMIN");

        assertEquals(1, entity.getId());
        assertEquals("ADMIN", entity.getNombre());
    }

    @Test
    void shouldSetAndGetFields() {
        RolEntity entity = new RolEntity();

        entity.setId(2);
        entity.setNombre("CLIENTE");

        assertEquals(2, entity.getId());
        assertEquals("CLIENTE", entity.getNombre());
    }
}

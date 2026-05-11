package agenda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TrabajadorEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        TrabajadorEntity entity = new TrabajadorEntity(1L, 2L, "Ana", "ana@email.com", "3001234567", true);

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getNegocioId());
        assertEquals("Ana", entity.getNombre());
        assertEquals("ana@email.com", entity.getEmail());
        assertEquals("3001234567", entity.getTelefono());
        assertEquals(true, entity.getActivo());
    }

    @Test
    void shouldSetAndGetFields() {
        TrabajadorEntity entity = new TrabajadorEntity();

        entity.setId(10L);
        entity.setNegocioId(20L);
        entity.setNombre("Luis");
        entity.setEmail("luis@email.com");
        entity.setTelefono("3110000000");
        entity.setActivo(false);

        assertEquals(10L, entity.getId());
        assertEquals(20L, entity.getNegocioId());
        assertEquals("Luis", entity.getNombre());
        assertEquals("luis@email.com", entity.getEmail());
        assertEquals("3110000000", entity.getTelefono());
        assertEquals(false, entity.getActivo());
    }
}

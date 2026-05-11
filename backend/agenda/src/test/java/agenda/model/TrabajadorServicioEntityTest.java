package agenda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TrabajadorServicioEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        TrabajadorServicioEntity entity = new TrabajadorServicioEntity(1L, 2L, 3L);

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getTrabajadorId());
        assertEquals(3L, entity.getServicioId());
    }

    @Test
    void shouldSetAndGetFields() {
        TrabajadorServicioEntity entity = new TrabajadorServicioEntity();

        entity.setId(10L);
        entity.setTrabajadorId(20L);
        entity.setServicioId(30L);

        assertEquals(10L, entity.getId());
        assertEquals(20L, entity.getTrabajadorId());
        assertEquals(30L, entity.getServicioId());
    }
}

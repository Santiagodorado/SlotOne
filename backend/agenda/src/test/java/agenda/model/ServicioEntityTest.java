package agenda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ServicioEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        ServicioEntity entity = new ServicioEntity(1L, 2L, "Corte", 45, 30000.0, "Servicio basico");

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getNegocioId());
        assertEquals("Corte", entity.getNombre());
        assertEquals(45, entity.getDuracionMinutos());
        assertEquals(30000.0, entity.getPrecio());
        assertEquals("Servicio basico", entity.getDescripcion());
    }

    @Test
    void shouldSetAndGetFields() {
        ServicioEntity entity = new ServicioEntity();

        entity.setId(10L);
        entity.setNegocioId(20L);
        entity.setNombre("Barberia");
        entity.setDuracionMinutos(30);
        entity.setPrecio(15000.0);
        entity.setDescripcion("Detalle");

        assertEquals(10L, entity.getId());
        assertEquals(20L, entity.getNegocioId());
        assertEquals("Barberia", entity.getNombre());
        assertEquals(30, entity.getDuracionMinutos());
        assertEquals(15000.0, entity.getPrecio());
        assertEquals("Detalle", entity.getDescripcion());
    }
}

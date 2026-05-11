package negocios.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NegocioEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        NegocioEntity entity = new NegocioEntity(1L, "Spa One", "Servicios de spa", "Calle 1", "3009998888",
                "spa@ejemplo.local", "data:image/png;base64,abc", 10L);

        assertEquals(1L, entity.getId());
        assertEquals("Spa One", entity.getNombre());
        assertEquals("Servicios de spa", entity.getDescripcion());
        assertEquals("Calle 1", entity.getDireccion());
        assertEquals("3009998888", entity.getTelefono());
        assertEquals("spa@ejemplo.local", entity.getCorreo());
        assertEquals("data:image/png;base64,abc", entity.getLogoUrl());
        assertEquals(10L, entity.getDuenioId());
    }

    @Test
    void shouldSetAndGetFields() {
        NegocioEntity entity = new NegocioEntity();

        entity.setId(20L);
        entity.setNombre("Barber");
        entity.setDescripcion("Servicios para caballero");
        entity.setDireccion("Carrera 5");
        entity.setTelefono("3011231234");
        entity.setCorreo("barber@test.com");
        entity.setLogoUrl("https://cdn/logo.png");
        entity.setDuenioId(99L);

        assertEquals(20L, entity.getId());
        assertEquals("Barber", entity.getNombre());
        assertEquals("Servicios para caballero", entity.getDescripcion());
        assertEquals("Carrera 5", entity.getDireccion());
        assertEquals("3011231234", entity.getTelefono());
        assertEquals("barber@test.com", entity.getCorreo());
        assertEquals("https://cdn/logo.png", entity.getLogoUrl());
        assertEquals(99L, entity.getDuenioId());
    }
}

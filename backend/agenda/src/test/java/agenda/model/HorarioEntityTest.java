package agenda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HorarioEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        HorarioEntity entity = new HorarioEntity(1L, 2L, 3L, 1, "08:00", "12:00");

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getNegocioId());
        assertEquals(3L, entity.getServicioId());
        assertEquals(1, entity.getDiaSemana());
        assertEquals("08:00", entity.getHoraInicio());
        assertEquals("12:00", entity.getHoraFin());
    }

    @Test
    void shouldSetAndGetFields() {
        HorarioEntity entity = new HorarioEntity();

        entity.setId(10L);
        entity.setNegocioId(20L);
        entity.setServicioId(30L);
        entity.setDiaSemana(5);
        entity.setHoraInicio("14:00");
        entity.setHoraFin("18:00");

        assertEquals(10L, entity.getId());
        assertEquals(20L, entity.getNegocioId());
        assertEquals(30L, entity.getServicioId());
        assertEquals(5, entity.getDiaSemana());
        assertEquals("14:00", entity.getHoraInicio());
        assertEquals("18:00", entity.getHoraFin());
    }
}

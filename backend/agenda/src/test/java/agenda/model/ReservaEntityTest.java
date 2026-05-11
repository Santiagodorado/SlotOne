package agenda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ReservaEntityTest {

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {
        LocalDate fecha = LocalDate.of(2026, 4, 6);
        ReservaEntity entity = new ReservaEntity(1L, "RES-001", 2L, 3L, 4L, 5L, "Carlos", "c@email.com",
                "3001112233", fecha, "09:00", "09:30", "PENDIENTE", "Sin notas");

        assertEquals(1L, entity.getId());
        assertEquals("RES-001", entity.getCodigoReserva());
        assertEquals(2L, entity.getNegocioId());
        assertEquals(3L, entity.getServicioId());
        assertEquals(4L, entity.getTrabajadorId());
        assertEquals(5L, entity.getClienteId());
        assertEquals("Carlos", entity.getClienteNombre());
        assertEquals("c@email.com", entity.getClienteEmail());
        assertEquals("3001112233", entity.getClienteTelefono());
        assertEquals(fecha, entity.getFecha());
        assertEquals("09:00", entity.getHoraInicio());
        assertEquals("09:30", entity.getHoraFin());
        assertEquals("PENDIENTE", entity.getEstado());
        assertEquals("Sin notas", entity.getNotas());
    }

    @Test
    void shouldSetAndGetFields() {
        LocalDate fecha = LocalDate.of(2026, 5, 1);
        ReservaEntity entity = new ReservaEntity();

        entity.setId(10L);
        entity.setCodigoReserva("RES-XYZ");
        entity.setNegocioId(20L);
        entity.setServicioId(30L);
        entity.setTrabajadorId(40L);
        entity.setClienteId(50L);
        entity.setClienteNombre("Laura");
        entity.setClienteEmail("laura@email.com");
        entity.setClienteTelefono("3112223344");
        entity.setFecha(fecha);
        entity.setHoraInicio("10:00");
        entity.setHoraFin("10:45");
        entity.setEstado("CONFIRMADA");
        entity.setNotas("Con anticipacion");

        assertEquals(10L, entity.getId());
        assertEquals("RES-XYZ", entity.getCodigoReserva());
        assertEquals(20L, entity.getNegocioId());
        assertEquals(30L, entity.getServicioId());
        assertEquals(40L, entity.getTrabajadorId());
        assertEquals(50L, entity.getClienteId());
        assertEquals("Laura", entity.getClienteNombre());
        assertEquals("laura@email.com", entity.getClienteEmail());
        assertEquals("3112223344", entity.getClienteTelefono());
        assertEquals(fecha, entity.getFecha());
        assertEquals("10:00", entity.getHoraInicio());
        assertEquals("10:45", entity.getHoraFin());
        assertEquals("CONFIRMADA", entity.getEstado());
        assertEquals("Con anticipacion", entity.getNotas());
    }
}

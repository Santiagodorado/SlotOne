package agenda.service;

import java.util.List;

import agenda.dto.HorarioRequestDTO;
import agenda.dto.HorarioResponseDTO;
import agenda.dto.ServicioRequestDTO;
import agenda.dto.ServicioResponseDTO;

public interface AgendaService {

    List<ServicioResponseDTO> listarServiciosPorNegocio(Long negocioId);

    ServicioResponseDTO crearServicio(ServicioRequestDTO dto);

    ServicioResponseDTO actualizarServicio(Long id, ServicioRequestDTO dto);

    void eliminarServicio(Long id, Long negocioId);

    List<HorarioResponseDTO> listarHorariosPorNegocio(Long negocioId);

    HorarioResponseDTO crearHorario(HorarioRequestDTO dto);

    HorarioResponseDTO actualizarHorario(Long id, HorarioRequestDTO dto);

    void eliminarHorario(Long id, Long negocioId);

    /**
     * Indica si una hora del día cae dentro de algún rango de atención configurado
     * (base para validar reservas en el mismo sprint conceptual).
     */
    boolean horaDentroDelHorarioDeAtencion(Long servicioId, int diaSemana, String horaHHmm);
}


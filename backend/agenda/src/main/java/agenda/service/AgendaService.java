package agenda.service;

import java.util.List;

import agenda.dto.DisponibilidadResponseDTO;
import agenda.dto.HorarioRequestDTO;
import agenda.dto.HorarioResponseDTO;
import agenda.dto.ReservaRequestDTO;
import agenda.dto.ReservaResponseDTO;
import agenda.dto.ServicioRequestDTO;
import agenda.dto.ServicioResponseDTO;
import agenda.dto.TrabajadorRequestDTO;
import agenda.dto.TrabajadorResponseDTO;

public interface AgendaService {

    List<ServicioResponseDTO> listarServiciosPorNegocio(Long negocioId);

    ServicioResponseDTO crearServicio(ServicioRequestDTO dto);

    ServicioResponseDTO actualizarServicio(Long id, ServicioRequestDTO dto);

    void eliminarServicio(Long id, Long negocioId);

    List<HorarioResponseDTO> listarHorariosPorNegocio(Long negocioId);

    HorarioResponseDTO crearHorario(HorarioRequestDTO dto);

    HorarioResponseDTO actualizarHorario(Long id, HorarioRequestDTO dto);

    void eliminarHorario(Long id, Long negocioId);

    List<TrabajadorResponseDTO> listarTrabajadoresPorNegocio(Long negocioId);

    List<TrabajadorResponseDTO> listarTrabajadoresPorServicio(Long servicioId);

    TrabajadorResponseDTO crearTrabajador(TrabajadorRequestDTO dto);

    TrabajadorResponseDTO actualizarTrabajador(Long id, TrabajadorRequestDTO dto);

    void eliminarTrabajador(Long id, Long negocioId);

    DisponibilidadResponseDTO consultarDisponibilidad(Long servicioId, String fecha, Long trabajadorId);

    ReservaResponseDTO crearReserva(ReservaRequestDTO dto);

    List<ReservaResponseDTO> listarReservasPorCliente(Long clienteId);

    List<ReservaResponseDTO> listarReservasPorNegocio(Long negocioId, String desde, String hasta);

    /**
     * Indica si una hora del día cae dentro de algún rango de atención configurado
     * (base para validar reservas en el mismo sprint conceptual).
     */
    boolean horaDentroDelHorarioDeAtencion(Long servicioId, int diaSemana, String horaHHmm);
}


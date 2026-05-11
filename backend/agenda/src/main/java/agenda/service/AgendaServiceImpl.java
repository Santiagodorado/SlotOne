package agenda.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import agenda.dto.DisponibilidadResponseDTO;
import agenda.dto.HorarioRequestDTO;
import agenda.dto.HorarioResponseDTO;
import agenda.dto.ReservaRequestDTO;
import agenda.dto.ReservaResponseDTO;
import agenda.dto.ServicioRequestDTO;
import agenda.dto.ServicioResponseDTO;
import agenda.dto.SlotResponseDTO;
import agenda.dto.TrabajadorRequestDTO;
import agenda.dto.TrabajadorResponseDTO;
import agenda.model.HorarioEntity;
import agenda.model.ReservaEntity;
import agenda.model.ServicioEntity;
import agenda.model.TrabajadorEntity;
import agenda.model.TrabajadorServicioEntity;
import agenda.repository.HorarioRepository;
import agenda.repository.ReservaRepository;
import agenda.repository.ServicioRepository;
import agenda.repository.TrabajadorRepository;
import agenda.repository.TrabajadorServicioRepository;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {

    private final ServicioRepository servicioRepository;
    private final HorarioRepository horarioRepository;
    private final ReservaRepository reservaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final TrabajadorServicioRepository trabajadorServicioRepository;
    private final ModelMapper modelMapper;
    private final NotificacionService notificacionService;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listarServiciosPorNegocio(Long negocioId) {
        return servicioRepository.findByNegocioId(negocioId)
                .stream()
                .map(s -> modelMapper.map(s, ServicioResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public ServicioResponseDTO crearServicio(ServicioRequestDTO dto) {
        ServicioEntity entity = new ServicioEntity();
        entity.setNegocioId(dto.getNegocioId());
        entity.setNombre(dto.getNombre());
        entity.setDuracionMinutos(dto.getDuracionMinutos());
        entity.setPrecio(dto.getPrecio());
        entity.setDescripcion(dto.getDescripcion());
        ServicioEntity guardado = servicioRepository.save(entity);
        return modelMapper.map(guardado, ServicioResponseDTO.class);
    }

    @Override
    @Transactional
    public ServicioResponseDTO actualizarServicio(Long id, ServicioRequestDTO dto) {
        ServicioEntity entity = servicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        if (!entity.getNegocioId().equals(dto.getNegocioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar este servicio.");
        }
        entity.setNombre(dto.getNombre());
        entity.setDuracionMinutos(dto.getDuracionMinutos());
        entity.setPrecio(dto.getPrecio());
        entity.setDescripcion(dto.getDescripcion());
        return modelMapper.map(servicioRepository.save(entity), ServicioResponseDTO.class);
    }

    @Override
    @Transactional
    public void eliminarServicio(Long id, Long negocioId) {
        ServicioEntity entity = servicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        if (!entity.getNegocioId().equals(negocioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar este servicio.");
        }
        horarioRepository.deleteByServicioId(id);
        servicioRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarHorariosPorNegocio(Long negocioId) {
        return horarioRepository.findByNegocioId(negocioId)
                .stream()
                .map(h -> modelMapper.map(h, HorarioResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public HorarioResponseDTO crearHorario(HorarioRequestDTO dto) {
        ServicioEntity servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        LocalTime[] rango = parseYValidarRango(dto.getHoraInicio(), dto.getHoraFin());
        assertSinSolape(dto.getServicioId(), dto.getDiaSemana(), rango[0], rango[1], null);
        HorarioEntity entity = new HorarioEntity();
        entity.setServicioId(dto.getServicioId());
        entity.setNegocioId(servicio.getNegocioId());
        entity.setDiaSemana(dto.getDiaSemana());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        HorarioEntity guardado = horarioRepository.save(entity);
        return modelMapper.map(guardado, HorarioResponseDTO.class);
    }

    @Override
    @Transactional
    public HorarioResponseDTO actualizarHorario(Long id, HorarioRequestDTO dto) {
        HorarioEntity entity = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado."));
        ServicioEntity servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        if (!entity.getNegocioId().equals(servicio.getNegocioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El servicio indicado no pertenece al mismo negocio que este horario.");
        }
        LocalTime[] rango = parseYValidarRango(dto.getHoraInicio(), dto.getHoraFin());
        assertSinSolape(dto.getServicioId(), dto.getDiaSemana(), rango[0], rango[1], id);
        entity.setServicioId(dto.getServicioId());
        entity.setNegocioId(servicio.getNegocioId());
        entity.setDiaSemana(dto.getDiaSemana());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        return modelMapper.map(horarioRepository.save(entity), HorarioResponseDTO.class);
    }

    @Override
    @Transactional
    public void eliminarHorario(Long id, Long negocioId) {
        HorarioEntity entity = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado."));
        if (!entity.getNegocioId().equals(negocioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar este horario.");
        }
        horarioRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> listarTrabajadoresPorNegocio(Long negocioId) {
        return trabajadorRepository.findByNegocioId(negocioId).stream()
                .map(this::toTrabajadorResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> listarTrabajadoresPorServicio(Long servicioId) {
        return trabajadorServicioRepository.findByServicioId(servicioId).stream()
                .map(TrabajadorServicioEntity::getTrabajadorId)
                .distinct()
                .map(trabajadorRepository::findById)
                .flatMap(java.util.Optional::stream)
                .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                .map(this::toTrabajadorResponse)
                .toList();
    }

    @Override
    @Transactional
    public TrabajadorResponseDTO crearTrabajador(TrabajadorRequestDTO dto) {
        TrabajadorEntity entity = new TrabajadorEntity();
        entity.setNegocioId(dto.getNegocioId());
        entity.setNombre(dto.getNombre().trim());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setActivo(dto.getActivo() == null ? true : dto.getActivo());
        TrabajadorEntity guardado = trabajadorRepository.save(entity);
        reemplazarServiciosTrabajador(guardado.getId(), guardado.getNegocioId(), dto.getServicioIds());
        return toTrabajadorResponse(guardado);
    }

    @Override
    @Transactional
    public TrabajadorResponseDTO actualizarTrabajador(Long id, TrabajadorRequestDTO dto) {
        TrabajadorEntity entity = trabajadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado."));
        if (!entity.getNegocioId().equals(dto.getNegocioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar este trabajador.");
        }
        entity.setNombre(dto.getNombre().trim());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setActivo(dto.getActivo() == null ? true : dto.getActivo());
        TrabajadorEntity guardado = trabajadorRepository.save(entity);
        reemplazarServiciosTrabajador(guardado.getId(), guardado.getNegocioId(), dto.getServicioIds());
        return toTrabajadorResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminarTrabajador(Long id, Long negocioId) {
        TrabajadorEntity entity = trabajadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado."));
        if (!entity.getNegocioId().equals(negocioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar este trabajador.");
        }
        trabajadorServicioRepository.deleteByTrabajadorId(id);
        trabajadorRepository.delete(entity);
    }

    private LocalTime[] parseYValidarRango(String horaInicio, String horaFin) {
        LocalTime inicioNuevo;
        LocalTime finNuevo;
        try {
            inicioNuevo = LocalTime.parse(normalizarHora(horaInicio));
            finNuevo = LocalTime.parse(normalizarHora(horaFin));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de hora inválido (use HH:mm).");
        }
        if (!inicioNuevo.isBefore(finNuevo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La hora de inicio debe ser anterior a la hora de fin.");
        }
        return new LocalTime[] { inicioNuevo, finNuevo };
    }

    private void assertSinSolape(Long servicioId, Integer diaSemana, LocalTime inicioNuevo, LocalTime finNuevo,
            Long excluirHorarioId) {
        List<HorarioEntity> existentes = horarioRepository.findByServicioIdAndDiaSemana(servicioId, diaSemana);
        for (HorarioEntity existente : existentes) {
            if (excluirHorarioId != null && excluirHorarioId.equals(existente.getId())) {
                continue;
            }
            try {
                LocalTime eInicio = LocalTime.parse(normalizarHora(existente.getHoraInicio()));
                LocalTime eFin = LocalTime.parse(normalizarHora(existente.getHoraFin()));
                if (rangosSeSolapan(inicioNuevo, finNuevo, eInicio, eFin)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ya existe un horario para ese servicio y día que se solapa con el rango indicado.");
                }
            } catch (DateTimeParseException ignored) {
                // ignorar franja mal formada en BD
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadResponseDTO consultarDisponibilidad(Long servicioId, String fecha, Long trabajadorId) {
        ServicioEntity servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        LocalDate dia = parseFecha(fecha);
        return new DisponibilidadResponseDTO(servicio.getId(), dia.toString(),
                calcularSlotsDisponibles(servicio, dia, trabajadorId));
    }

    @Override
    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        ServicioEntity servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado."));
        LocalDate fecha = parseFecha(dto.getFecha());
        if (fecha.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes reservar una fecha pasada.");
        }
        LocalTime inicio = parseHora(dto.getHoraInicio());
        LocalTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());
        Long trabajadorId = resolverTrabajadorDisponible(servicio, fecha, inicio, fin, dto.getTrabajadorId());

        ReservaEntity entity = new ReservaEntity();
        entity.setCodigoReserva("RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        entity.setNegocioId(servicio.getNegocioId());
        entity.setServicioId(servicio.getId());
        entity.setTrabajadorId(trabajadorId);
        entity.setClienteId(dto.getClienteId());
        entity.setClienteNombre(dto.getClienteNombre().trim());
        entity.setClienteEmail(dto.getClienteEmail().trim());
        entity.setClienteTelefono(dto.getClienteTelefono().trim());
        entity.setFecha(fecha);
        entity.setHoraInicio(formatearHora(inicio));
        entity.setHoraFin(formatearHora(fin));
        entity.setEstado("CONFIRMED");
        entity.setNotas(dto.getNotas());
        ReservaEntity guardada = reservaRepository.save(entity);
        notificacionService.notificarReservaCreada(guardada, servicio);
        return toReservaResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasPorCliente(Long clienteId) {
        return reservaRepository.findByClienteIdOrderByFechaDescHoraInicioDesc(clienteId)
                .stream()
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasPorNegocio(Long negocioId, String desde, String hasta) {
        LocalDate inicio = (desde == null || desde.isBlank()) ? LocalDate.now() : parseFecha(desde);
        LocalDate fin = (hasta == null || hasta.isBlank()) ? inicio.plusMonths(1) : parseFecha(hasta);
        if (fin.isBefore(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de fechas es inválido.");
        }
        return reservaRepository.findByNegocioIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(negocioId, inicio, fin)
                .stream()
                .map(this::toReservaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean horaDentroDelHorarioDeAtencion(Long servicioId, int diaSemana, String horaHHmm) {
        LocalTime hora;
        try {
            hora = LocalTime.parse(normalizarHora(horaHHmm));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de hora inválido (use HH:mm).");
        }
        List<HorarioEntity> delDia = horarioRepository.findByServicioIdAndDiaSemana(servicioId, diaSemana);
        for (HorarioEntity h : delDia) {
            try {
                LocalTime inicio = LocalTime.parse(normalizarHora(h.getHoraInicio()));
                LocalTime fin = LocalTime.parse(normalizarHora(h.getHoraFin()));
                if (!hora.isBefore(inicio) && hora.isBefore(fin)) {
                    return true;
                }
            } catch (DateTimeParseException ignored) {
                // ignorar franja mal formada
            }
        }
        return false;
    }

    private List<SlotResponseDTO> calcularSlotsDisponibles(ServicioEntity servicio, LocalDate fecha, Long trabajadorId) {
        int diaSemana = fecha.getDayOfWeek().getValue() % 7; // Java: domingo=7; app: domingo=0.
        List<HorarioEntity> horarios = horarioRepository.findByServicioIdAndDiaSemana(servicio.getId(), diaSemana);
        List<TrabajadorEntity> candidatos = trabajadoresCandidatos(servicio, trabajadorId);
        List<SlotResponseDTO> slots = new ArrayList<>();
        for (HorarioEntity h : horarios) {
            LocalTime inicioHorario = parseHora(h.getHoraInicio());
            LocalTime finHorario = parseHora(h.getHoraFin());
            LocalTime cursor = inicioHorario;
            while (!cursor.plusMinutes(servicio.getDuracionMinutos()).isAfter(finHorario)) {
                LocalTime finSlot = cursor.plusMinutes(servicio.getDuracionMinutos());
                if (fecha.isAfter(LocalDate.now()) || cursor.isAfter(LocalTime.now())) {
                    LocalTime inicioSlot = cursor;
                    if (candidatos.stream().anyMatch(t -> slotSinReservaTrabajador(t.getId(), fecha, inicioSlot, finSlot))) {
                        slots.add(new SlotResponseDTO(formatearHora(cursor), formatearHora(finSlot)));
                    }
                }
                cursor = finSlot;
            }
        }
        return slots;
    }

    private Long resolverTrabajadorDisponible(ServicioEntity servicio, LocalDate fecha, LocalTime inicio, LocalTime fin,
            Long trabajadorSolicitadoId) {
        assertDentroDeHorario(servicio, fecha, inicio, fin);
        List<TrabajadorEntity> candidatos = trabajadoresCandidatos(servicio, trabajadorSolicitadoId);
        return candidatos.stream()
                .filter(t -> slotSinReservaTrabajador(t.getId(), fecha, inicio, fin))
                .findFirst()
                .map(TrabajadorEntity::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No hay trabajador disponible para ese servicio en el horario seleccionado."));
    }

    private void assertDentroDeHorario(ServicioEntity servicio, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        int diaSemana = fecha.getDayOfWeek().getValue() % 7;
        boolean cubiertoPorHorario = horarioRepository.findByServicioIdAndDiaSemana(servicio.getId(), diaSemana)
                .stream()
                .anyMatch(h -> {
                    LocalTime hInicio = parseHora(h.getHoraInicio());
                    LocalTime hFin = parseHora(h.getHoraFin());
                    return !inicio.isBefore(hInicio) && !fin.isAfter(hFin);
                });
        if (!cubiertoPorHorario) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La reserva debe estar dentro de un horario configurado para este servicio.");
        }
    }

    private List<TrabajadorEntity> trabajadoresCandidatos(ServicioEntity servicio, Long trabajadorId) {
        if (trabajadorId != null) {
            TrabajadorEntity trabajador = trabajadorRepository.findById(trabajadorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado."));
            if (!trabajador.getNegocioId().equals(servicio.getNegocioId())
                    || !Boolean.TRUE.equals(trabajador.getActivo())
                    || !trabajadorServicioRepository.existsByTrabajadorIdAndServicioId(trabajadorId, servicio.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El trabajador no está disponible para este servicio.");
            }
            return List.of(trabajador);
        }
        List<TrabajadorEntity> trabajadores = trabajadorServicioRepository.findByServicioId(servicio.getId()).stream()
                .map(TrabajadorServicioEntity::getTrabajadorId)
                .distinct()
                .map(trabajadorRepository::findById)
                .flatMap(java.util.Optional::stream)
                .filter(t -> Objects.equals(t.getNegocioId(), servicio.getNegocioId()))
                .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                .toList();
        if (trabajadores.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Este servicio no tiene trabajadores activos asignados.");
        }
        return trabajadores;
    }

    private boolean slotSinReservaTrabajador(Long trabajadorId, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<ReservaEntity> reservas = reservaRepository.findByTrabajadorIdAndFechaAndEstadoNot(
                trabajadorId, fecha, "CANCELLED");
        for (ReservaEntity r : reservas) {
            LocalTime rInicio = parseHora(r.getHoraInicio());
            LocalTime rFin = parseHora(r.getHoraFin());
            if (rangosSeSolapan(inicio, fin, rInicio, rFin)) {
                return false;
            }
        }
        return true;
    }

    private void reemplazarServiciosTrabajador(Long trabajadorId, Long negocioId, List<Long> servicioIds) {
        trabajadorServicioRepository.deleteByTrabajadorId(trabajadorId);
        if (servicioIds == null) {
            return;
        }
        servicioIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(servicioId -> {
                    ServicioEntity servicio = servicioRepository.findById(servicioId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Servicio asignado no encontrado."));
                    if (!servicio.getNegocioId().equals(negocioId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Todos los servicios asignados deben pertenecer al mismo negocio.");
                    }
                    TrabajadorServicioEntity rel = new TrabajadorServicioEntity();
                    rel.setTrabajadorId(trabajadorId);
                    rel.setServicioId(servicioId);
                    trabajadorServicioRepository.save(rel);
                });
    }

    private TrabajadorResponseDTO toTrabajadorResponse(TrabajadorEntity t) {
        List<Long> servicioIds = trabajadorServicioRepository.findByTrabajadorId(t.getId())
                .stream()
                .map(TrabajadorServicioEntity::getServicioId)
                .toList();
        return new TrabajadorResponseDTO(
                t.getId(),
                t.getNegocioId(),
                t.getNombre(),
                t.getEmail(),
                t.getTelefono(),
                t.getActivo(),
                servicioIds);
    }

    private static LocalDate parseFecha(String fecha) {
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido (use yyyy-MM-dd).");
        }
    }

    private static LocalTime parseHora(String hora) {
        try {
            return LocalTime.parse(normalizarHora(hora));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de hora inválido (use HH:mm).");
        }
    }

    private ReservaResponseDTO toReservaResponse(ReservaEntity r) {
        return new ReservaResponseDTO(
                r.getId(),
                r.getCodigoReserva(),
                r.getNegocioId(),
                r.getServicioId(),
                r.getTrabajadorId(),
                r.getClienteId(),
                r.getClienteNombre(),
                r.getClienteEmail(),
                r.getClienteTelefono(),
                r.getFecha() != null ? r.getFecha().toString() : null,
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getEstado(),
                r.getNotas());
    }

    /** Trunca a HH:mm si viene con segundos (input type="time"). */
    private static String normalizarHora(String hora) {
        if (hora == null) {
            return "";
        }
        String t = hora.trim();
        if (t.length() >= 5) {
            return t.substring(0, 5);
        }
        return t;
    }

    private static String formatearHora(LocalTime hora) {
        return hora.toString();
    }

    /** Rangos [inicio, fin) semiabiertos: no se solapan si un fin es exactamente el inicio del otro. */
    private static boolean rangosSeSolapan(LocalTime aInicio, LocalTime aFin, LocalTime bInicio, LocalTime bFin) {
        return aInicio.isBefore(bFin) && bInicio.isBefore(aFin);
    }
}


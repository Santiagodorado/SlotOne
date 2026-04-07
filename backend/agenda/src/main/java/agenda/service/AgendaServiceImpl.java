package agenda.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import agenda.dto.HorarioRequestDTO;
import agenda.dto.HorarioResponseDTO;
import agenda.dto.ServicioRequestDTO;
import agenda.dto.ServicioResponseDTO;
import agenda.model.HorarioEntity;
import agenda.model.ServicioEntity;
import agenda.repository.HorarioRepository;
import agenda.repository.ServicioRepository;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {

    private final ServicioRepository servicioRepository;
    private final HorarioRepository horarioRepository;
    private final ModelMapper modelMapper;

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

    /** Rangos [inicio, fin) semiabiertos: no se solapan si un fin es exactamente el inicio del otro. */
    private static boolean rangosSeSolapan(LocalTime aInicio, LocalTime aFin, LocalTime bInicio, LocalTime bFin) {
        return aInicio.isBefore(bFin) && bInicio.isBefore(aFin);
    }
}


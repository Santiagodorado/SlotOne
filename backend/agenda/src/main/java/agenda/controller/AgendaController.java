package agenda.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import agenda.dto.HorarioRequestDTO;
import agenda.dto.HorarioResponseDTO;
import agenda.dto.ServicioRequestDTO;
import agenda.dto.ServicioResponseDTO;
import agenda.service.AgendaService;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
@Validated
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios(
            @RequestParam("negocioId") Long negocioId) {
        return ResponseEntity.ok(agendaService.listarServiciosPorNegocio(negocioId));
    }

    @PostMapping("/servicios")
    public ResponseEntity<ServicioResponseDTO> crearServicio(
            @Valid @RequestBody ServicioRequestDTO dto) {
        ServicioResponseDTO creado = agendaService.crearServicio(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/servicios/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizarServicio(@PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(agendaService.actualizarServicio(id, dto));
    }

    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id,
            @RequestParam("negocioId") Long negocioId) {
        agendaService.eliminarServicio(id, negocioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioResponseDTO>> listarHorarios(
            @RequestParam("negocioId") Long negocioId) {
        return ResponseEntity.ok(agendaService.listarHorariosPorNegocio(negocioId));
    }

    @PostMapping("/horarios")
    public ResponseEntity<HorarioResponseDTO> crearHorario(
            @Valid @RequestBody HorarioRequestDTO dto) {
        HorarioResponseDTO creado = agendaService.crearHorario(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/horarios/{id}")
    public ResponseEntity<HorarioResponseDTO> actualizarHorario(@PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO dto) {
        return ResponseEntity.ok(agendaService.actualizarHorario(id, dto));
    }

    @DeleteMapping("/horarios/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id,
            @RequestParam("negocioId") Long negocioId) {
        agendaService.eliminarHorario(id, negocioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Valida si una hora dada está dentro de los rangos de atención del día (uso futuro: reservas).
     */
    @GetMapping("/horarios/cubre")
    public ResponseEntity<Map<String, Boolean>> horarioCubre(
            @RequestParam("servicioId") Long servicioId,
            @RequestParam("diaSemana") int diaSemana,
            @RequestParam("hora") String hora) {
        boolean cubre = agendaService.horaDentroDelHorarioDeAtencion(servicioId, diaSemana, hora);
        return ResponseEntity.ok(Map.of("cubre", cubre));
    }
}


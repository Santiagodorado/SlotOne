package negocios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import negocios.dto.NegocioRequestDTO;
import negocios.dto.NegocioResponseDTO;
import negocios.service.NegocioService;

@RestController
@RequestMapping("/api/negocios")
@RequiredArgsConstructor
@Validated
public class NegocioController {

    private final NegocioService negocioService;

    @GetMapping
    public ResponseEntity<List<NegocioResponseDTO>> listar(
            @RequestParam(name = "duenioId", required = false) Long duenioId) {
        List<NegocioResponseDTO> resultado = (duenioId == null)
                ? negocioService.listarTodos()
                : negocioService.listarPorDuenio(duenioId);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<NegocioResponseDTO> crear(@Valid @RequestBody NegocioRequestDTO dto) {
        NegocioResponseDTO creado = negocioService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NegocioResponseDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody NegocioRequestDTO dto) {
        return ResponseEntity.ok(negocioService.actualizar(id, dto));
    }
}


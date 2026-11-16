package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.PreferenciasDto;
import br.com.fiap.citizentech.security.UsuarioLogado;
import br.com.fiap.citizentech.service.PreferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferencias")
@Tag(name = "Preferências", description = "Preferências de acessibilidade do usuário")
public class PreferenciaController {

    private final PreferenciaService preferenciaService;
    private final UsuarioLogado usuarioLogado;

    public PreferenciaController(PreferenciaService preferenciaService, UsuarioLogado usuarioLogado) {
        this.preferenciaService = preferenciaService;
        this.usuarioLogado = usuarioLogado;
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Consulta preferências", description = "403 se não forem as suas (exceto admin).")
    public PreferenciasDto buscar(@PathVariable String usuarioId) {
        return preferenciaService.buscar(usuarioLogado.atual(), usuarioId);
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Salva preferências", description = "403 se não forem as suas (exceto admin).")
    public PreferenciasDto salvar(@PathVariable String usuarioId, @Valid @RequestBody PreferenciasDto request) {
        return preferenciaService.salvar(usuarioLogado.atual(), usuarioId, request);
    }
}

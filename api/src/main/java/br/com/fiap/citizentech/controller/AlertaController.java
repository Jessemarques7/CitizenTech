package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Alertas gerados pelas leituras simuladas dos sensores")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @Operation(summary = "Lista alertas", description = "Ativos por padrão; incluirEncerrados=true traz o histórico.")
    public List<AlertaResponse> listar(@RequestParam(defaultValue = "false") boolean incluirEncerrados) {
        return alertaService.listar(incluirEncerrados);
    }

    @PutMapping("/{id}/encerrar")
    @Operation(summary = "Encerra alerta manualmente (ADMIN)", description = "Aceita 'AL-205' ou '205'.")
    public AlertaResponse encerrar(@PathVariable String id) {
        return alertaService.encerrar(id);
    }
}

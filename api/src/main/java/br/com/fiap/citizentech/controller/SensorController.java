package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.SensorDtos.LeituraRequest;
import br.com.fiap.citizentech.dto.SensorDtos.LeituraResponse;
import br.com.fiap.citizentech.dto.SensorDtos.LeiturasResponse;
import br.com.fiap.citizentech.dto.SensorDtos.SensorResponse;
import br.com.fiap.citizentech.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensores")
@Tag(name = "Sensores IoT", description = "Sensores simulados de ocupação e chuva")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/leituras")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "deviceKey")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registra leituras (dispositivo ou ADMIN)",
            description = "Aceita um array. Leitura >= limite do sensor gera alerta; abaixo do limite encerra o alerta ativo.")
    public LeiturasResponse registrar(@RequestBody List<@Valid LeituraRequest> leituras) {
        return sensorService.registrar(leituras);
    }

    @GetMapping
    @Operation(summary = "Painel de sensores (ADMIN)", description = "Última leitura de cada sensor e se está desatualizada.")
    public List<SensorResponse> listar() {
        return sensorService.listar();
    }

    @GetMapping("/{codigo}/leituras")
    @Operation(summary = "Histórico de leituras de um sensor (ADMIN)")
    public List<LeituraResponse> leituras(@PathVariable String codigo,
                                          @RequestParam(defaultValue = "20") int limite) {
        return sensorService.leiturasDo(codigo, limite);
    }
}

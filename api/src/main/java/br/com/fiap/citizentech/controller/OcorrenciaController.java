package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.OcorrenciaDtos.OcorrenciaRequest;
import br.com.fiap.citizentech.dto.OcorrenciaDtos.OcorrenciaResponse;
import br.com.fiap.citizentech.dto.OcorrenciaDtos.StatusRequest;
import br.com.fiap.citizentech.security.UsuarioLogado;
import br.com.fiap.citizentech.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrências", description = "Relatos de barreiras feitos pelos cidadãos")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;
    private final UsuarioLogado usuarioLogado;

    public OcorrenciaController(OcorrenciaService ocorrenciaService, UsuarioLogado usuarioLogado) {
        this.ocorrenciaService = ocorrenciaService;
        this.usuarioLogado = usuarioLogado;
    }

    @GetMapping
    @Operation(summary = "Lista relatos", description = "Cidadão: só os próprios (403 se pedir de outro usuarioId). Admin: todos.")
    public List<OcorrenciaResponse> listar(@RequestParam(required = false) String usuarioId,
                                           @RequestParam(required = false) String status) {
        return ocorrenciaService.listar(usuarioLogado.atual(), usuarioId, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe do relato", description = "Aceita 'CT-1024' ou '1024'.")
    public OcorrenciaResponse buscar(@PathVariable String id) {
        return ocorrenciaService.buscar(usuarioLogado.atual(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra barreira", description = "201 com status inicial 'Recebida'; 400 se local/categoria/descrição inválidos.")
    public OcorrenciaResponse criar(@Valid @RequestBody OcorrenciaRequest request) {
        return ocorrenciaService.criar(usuarioLogado.atual(), request);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualiza andamento (ADMIN)", description = "409 se o relato já estiver resolvido.")
    public OcorrenciaResponse atualizarStatus(@PathVariable String id, @Valid @RequestBody StatusRequest request) {
        return ocorrenciaService.atualizarStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui/cancela relato", description = "Admin: qualquer um. Autor: só enquanto 'Recebida' (senão 409).")
    public void excluir(@PathVariable String id) {
        ocorrenciaService.excluir(usuarioLogado.atual(), id);
    }
}

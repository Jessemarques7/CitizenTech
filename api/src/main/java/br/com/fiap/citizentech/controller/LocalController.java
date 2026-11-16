package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.LocalDtos.LocalRequest;
import br.com.fiap.citizentech.dto.LocalDtos.LocalResponse;
import br.com.fiap.citizentech.service.LocalService;
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
@RequestMapping("/api/locais")
@Tag(name = "Locais", description = "Locais públicos, recursos de acessibilidade e ocupação")
public class LocalController {

    private final LocalService localService;

    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    @GetMapping
    @Operation(summary = "Lista locais", description = "Filtros opcionais: status (Acessível, Parcial, Inacessível) e busca por nome/endereço.")
    public List<LocalResponse> listar(@RequestParam(required = false) String status,
                                      @RequestParam(required = false) String busca) {
        return localService.listar(status, busca);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe do local", description = "404 se o local não existir.")
    public LocalResponse buscar(@PathVariable String id) {
        return localService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra local (ADMIN)", description = "201 criado; 400 inválido; 403 sem perfil ADMIN; 409 id duplicado.")
    public LocalResponse criar(@Valid @RequestBody LocalRequest request) {
        return localService.criar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza local (ADMIN)")
    public LocalResponse atualizar(@PathVariable String id, @Valid @RequestBody LocalRequest request) {
        return localService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui local (ADMIN)", description = "204; 409 se houver relatos ou sensores vinculados.")
    public void excluir(@PathVariable String id) {
        localService.excluir(id);
    }
}

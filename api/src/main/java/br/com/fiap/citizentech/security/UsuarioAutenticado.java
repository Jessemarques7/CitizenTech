package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.model.Perfil;

/** Quem fez a requisição. Para dispositivos IoT, {@code id} é nulo e o perfil é {@code null}. */
public record UsuarioAutenticado(Long id, String email, Perfil perfil) {

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }
}

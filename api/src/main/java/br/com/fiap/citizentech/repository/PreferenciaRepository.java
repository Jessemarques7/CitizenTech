package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaRepository extends JpaRepository<Preferencia, Long> {
}

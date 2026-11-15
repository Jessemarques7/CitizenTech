package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Local;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocalRepository extends JpaRepository<Local, String> {

    @EntityGraph(attributePaths = "itens")
    @Query("select distinct l from Local l order by l.nome")
    List<Local> listarComItens();

    @EntityGraph(attributePaths = "itens")
    @Query("select l from Local l where l.id = :id")
    Optional<Local> buscarComItens(String id);
}

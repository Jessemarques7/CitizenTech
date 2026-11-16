package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Ocorrencia;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    /** Filtros opcionais (null = não filtra). Parâmetros vinculados, sem concatenação de SQL. */
    @Query("""
            select o from Ocorrencia o
              join fetch o.local
              join fetch o.usuario u
             where (:usuarioId is null or u.id = :usuarioId)
               and (:status is null or o.status = :status)
             order by o.dataCriacao desc
            """)
    List<Ocorrencia> buscar(Long usuarioId, StatusOcorrencia status);

    boolean existsByLocalId(String localId);
}

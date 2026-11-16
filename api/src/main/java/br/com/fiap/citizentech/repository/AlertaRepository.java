package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.StatusAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @Query("""
            select a from Alerta a
              join fetch a.leitura l
              join fetch l.sensor s
              join fetch s.local
             where (:status is null or a.status = :status)
             order by a.dataCriacao desc
            """)
    List<Alerta> buscar(StatusAlerta status);

    @Query("select a from Alerta a where a.status = 'ATIVO' and a.leitura.sensor.id = :sensorId")
    List<Alerta> ativosDoSensor(Long sensorId);
}

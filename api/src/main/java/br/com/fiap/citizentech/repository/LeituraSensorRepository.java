package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.LeituraSensor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {

    @Query("select l from LeituraSensor l where l.sensor.id = :sensorId order by l.dataLeitura desc, l.id desc")
    List<LeituraSensor> ultimasDoSensor(Long sensorId, Pageable pagina);

    default Optional<LeituraSensor> ultimaDoSensor(Long sensorId) {
        return ultimasDoSensor(sensorId, Pageable.ofSize(1)).stream().findFirst();
    }
}

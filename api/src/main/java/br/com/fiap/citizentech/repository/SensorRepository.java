package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Sensor;
import br.com.fiap.citizentech.model.TipoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByCodigoIgnoreCase(String codigo);

    Optional<Sensor> findByTipoAndLocalId(TipoSensor tipo, String localId);

    @Query("select s from Sensor s join fetch s.local order by s.codigo")
    List<Sensor> listarComLocal();

    List<Sensor> findByLocalId(String localId);

    boolean existsByLocalId(String localId);
}

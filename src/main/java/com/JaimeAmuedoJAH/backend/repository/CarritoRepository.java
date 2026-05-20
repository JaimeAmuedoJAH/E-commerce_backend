package com.JaimeAmuedoJAH.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.JaimeAmuedoJAH.backend.entity.CarritoEntity;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {

    List<CarritoEntity> findByClientePublicId(String clientePublicId);
}

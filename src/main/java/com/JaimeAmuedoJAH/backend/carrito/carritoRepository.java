package com.JaimeAmuedoJAH.backend.carrito;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {

    List<CarritoEntity> findByClienteId(Long clienteId);
}

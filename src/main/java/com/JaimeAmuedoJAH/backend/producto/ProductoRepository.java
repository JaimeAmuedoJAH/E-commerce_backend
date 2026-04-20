package com.JaimeAmuedoJAH.backend.producto;

import com.mapeo_backend.mapeo_backend.entity.ItinerarioEnt;
import com.mapeo_backend.mapeo_backend.entity.ViajeEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
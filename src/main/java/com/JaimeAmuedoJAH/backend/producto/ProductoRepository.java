package com.JaimeAmuedoJAH.backend.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    
    List<ProductoEntity> findByCategoriaId(Long categoriaId);
}
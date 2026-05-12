package com.JaimeAmuedoJAH.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JaimeAmuedoJAH.backend.entity.ProductoEntity;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    
    List<ProductoEntity> findByCategoriaId(Long categoriaId);
}

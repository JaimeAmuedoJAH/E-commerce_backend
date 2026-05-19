package com.JaimeAmuedoJAH.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JaimeAmuedoJAH.backend.entity.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    
    List<ProductoEntity> findByCategoriaId(Long categoriaId);

    // Sistema de paginación para productos por categoría
    Page<ProductoEntity> findByCategoriaId(Long categoriaId, Pageable pageable);
}

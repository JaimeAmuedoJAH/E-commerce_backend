package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    
}
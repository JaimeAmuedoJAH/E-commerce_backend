package com.JaimeAmuedoJAH.backend.categoria;

import com.JaimeAmuedoJAH.backend.producto.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    
}
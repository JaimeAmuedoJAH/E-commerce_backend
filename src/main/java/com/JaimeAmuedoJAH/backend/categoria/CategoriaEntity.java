package com.JaimeAmuedoJAH.backend.categoria;

import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@Builder
@AllArgsConstructor
public class CategoriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<ProductoEntity> productos;

    public CategoriaEntity() {
    }
}
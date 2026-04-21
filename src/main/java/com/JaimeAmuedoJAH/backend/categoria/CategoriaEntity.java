package com.JaimeAmuedoJAH.backend.categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "categoria")
@Data
public class CategoriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToMany(mappedBy = "categorias")
    @JoinColumn(name = "producto_id")
    private List<ProductoEntity> productos;
}
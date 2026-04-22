package com.JaimeAmuedoJAH.backend.producto;

import com.JaimeAmuedoJAH.backend.categoria.CategoriaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "producto")
@Data
@Builder
@AllArgsConstructor
public class ProductoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String talla;

    private String color;

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    private String imagen;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    public ProductoEntity() {
    }
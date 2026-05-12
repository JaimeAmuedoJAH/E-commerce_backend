package com.JaimeAmuedoJAH.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
    private List<ProductoEntity> productos;
}
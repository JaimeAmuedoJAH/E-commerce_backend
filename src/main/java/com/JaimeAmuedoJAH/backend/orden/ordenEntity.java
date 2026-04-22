package com.JaimeAmuedoJAH.backend.orden;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "orden")
@Data
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Usuario cliente;
    private String direccion;
    private Double total;
    private String estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<OrdenItemEntity> items;
}
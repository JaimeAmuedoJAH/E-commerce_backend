package com.JaimeAmuedoJAH.backend.orden;

import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orden_item")
@Data
public class OrdenItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private OrdenEntity orden;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
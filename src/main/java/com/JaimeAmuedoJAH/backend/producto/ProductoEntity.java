package com.JaimeAmuedoJAH.backend.producto;

@Entity
@Table(name = "producto")
@Data
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String talla;
    private String color;
    private String descripcion;
    private Double precio;
    private String imagen;
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;
}
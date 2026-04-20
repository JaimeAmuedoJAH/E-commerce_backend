package com.JaimeAmuedoJAH.backend.producto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final productoRepository productoRepository;

    public ProductoResponseDTO actualizarProducto(
    Long id, 
    ProductoRequestDTO productoDetails) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto not found with id " + id));

        if (productoDetails.getPrecio() != null && productoDetails.getPrecio() <= 0) {
            throw new BadRequestException("Precio inválido");
        }

        if (productoDetails.getStock() != null && productoDetails.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }

        // 🔧 aplicar cambios desde DTO hacia Entity
        producto.setNombre(productoDetails.getNombre());
        producto.setTalla(productoDetails.getTalla());
        producto.setColor(productoDetails.getColor());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setImagen(productoDetails.getImagen());
        producto.setStock(productoDetails.getStock());

        ProductoEntity updated = productoRepository.save(producto);

        // 🔄 mapear Entity → ResponseDTO
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(updated.getId());
        response.setNombre(updated.getNombre());
        response.setTalla(updated.getTalla());
        response.setColor(updated.getColor());
        response.setDescripcion(updated.getDescripcion());
        response.setPrecio(updated.getPrecio());
        response.setImagen(updated.getImagen());
        response.setStock(updated.getStock());

        return response;
    }

    public ProductoResponseDTO getProductoById(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto not found with id " + id));

        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setTalla(producto.getTalla());
        response.setColor(producto.getColor());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setImagen(producto.getImagen());
        response.setStock(producto.getStock());

        return response;
    }

    public List<ProductoResponseDTO> getAllProductos() {
        List<ProductoEntity> productos = productoRepository.findAll();

        return productos.stream().map(producto -> {
            ProductoResponseDTO dto = new ProductoResponseDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setTalla(producto.getTalla());
            dto.setColor(producto.getColor());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setImagen(producto.getImagen());
            dto.setStock(producto.getStock());
            return dto;
        }).collect(Collectors.toList());
    }

    public ProductoResponseDTO createProducto(ProductoRequestDTO productoDetails) {

        if (productoDetails.getPrecio() <= 0) {
        throw new BadRequestException("El precio debe ser mayor que 0");
        }

        if (productoDetails.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre(productoDetails.getNombre());
        producto.setTalla(productoDetails.getTalla());
        producto.setColor(productoDetails.getColor());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setImagen(productoDetails.getImagen());
        producto.setStock(productoDetails.getStock());

        ProductoEntity saved = productoRepository.save(producto);

        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(saved.getId());
        response.setNombre(saved.getNombre());
        response.setTalla(saved.getTalla());
        response.setColor(saved.getColor());
        response.setDescripcion(saved.getDescripcion());
        response.setPrecio(saved.getPrecio());
        response.setImagen(saved.getImagen());
        response.setStock(saved.getStock());

        return response;
    }
}
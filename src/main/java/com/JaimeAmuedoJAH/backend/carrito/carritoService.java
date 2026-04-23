package com.JaimeAmuedoJAH.backend.carrito;

import com.JaimeAmuedoJAH.backend.exceptions.BadRequestException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import com.JaimeAmuedoJAH.backend.producto.ProductoMapping;
import com.JaimeAmuedoJAH.backend.producto.ProductoRepository;
import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CarritoResponseDTO> obtenerTodosLosCarritos() {
        List<CarritoEntity> carritos = carritoRepository.findAll();
        return carritos.stream()
                .map(this::mapearCarritoConProductos)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarritoResponseDTO obtenerCarritoPorId(Long id) {
        CarritoEntity carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito not found with id " + id));
        return mapearCarritoConProductos(carrito);
    }

    @Transactional(readOnly = true)
    public List<CarritoResponseDTO> obtenerCarritosPorCliente(Long clienteId) {
        List<CarritoEntity> carritos = carritoRepository.findByClienteId(clienteId);
        return carritos.stream()
                .map(this::mapearCarritoConProductos)
                .collect(Collectors.toList());
    }

    public CarritoResponseDTO crearCarrito(CarritoRequestDTO request) {
        validarRequest(request);

        CarritoEntity carrito = CarritoMapping.toEntity(request);
        CarritoEntity saved = carritoRepository.save(carrito);
        return mapearCarritoConProductos(saved);
    }

    public CarritoResponseDTO actualizarCarrito(Long id, CarritoRequestDTO request) {
        validarRequest(request);

        CarritoEntity carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito not found with id " + id));

        carrito.setClienteId(request.getClienteId());
        carrito.getItems().clear();
        carrito.setItems(request.getItems().stream()
                .map(CarritoMapping::toItemEntity)
                .collect(Collectors.toList()));
        carrito.getItems().forEach(item -> item.setCarrito(carrito));

        CarritoEntity updated = carritoRepository.save(carrito);
        return mapearCarritoConProductos(updated);
    }

    public void eliminarCarrito(Long id) {
        CarritoEntity carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito not found with id " + id));
        carritoRepository.delete(carrito);
    }

    private void validarRequest(CarritoRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request de carrito no puede ser nulo");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("El carrito debe contener al menos un ítem");
        }
    }

    private CarritoResponseDTO mapearCarritoConProductos(CarritoEntity carrito) {
        List<Long> productoIds = carrito.getItems().stream()
                .map(CarritoItemEntity::getProductoId)
                .collect(Collectors.toList());

        List<ProductoEntity> productos = productoRepository.findAllById(productoIds);

        Map<Long, ProductoResponseDTO> productosMap = productos.stream()
                .collect(Collectors.toMap(ProductoEntity::getId, ProductoMapping::toResponseDTO));

        return CarritoMapping.toResponseDTO(carrito, productosMap);
    }
}

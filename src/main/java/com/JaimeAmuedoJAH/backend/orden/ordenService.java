package com.JaimeAmuedoJAH.backend.orden;

import com.JaimeAmuedoJAH.backend.exceptions.BadRequestException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import com.JaimeAmuedoJAH.backend.orden.dto.OrdenItemRequestDTO;
import com.JaimeAmuedoJAH.backend.producto.ProductoEntity;
import com.JaimeAmuedoJAH.backend.producto.ProductoRepository;
import com.JaimeAmuedoJAH.backend.usuario.usuarioEntity;
import com.JaimeAmuedoJAH.backend.usuario.usuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final usuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    /**
     * Obtener todas las órdenes
     */
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerTodasLasOrdenes() {
        return ordenRepository.findAll().stream()
                .map(OrdenMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una orden por ID
     */
    @Transactional(readOnly = true)
    public OrdenResponseDTO obtenerOrdenPorId(Long id) {
        OrdenEntity orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden not found with id " + id));
        return OrdenMapping.toResponseDTO(orden);
    }

    /**
     * Obtener órdenes por cliente
     */
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerOrdenesPorCliente(Long clienteId) {
        // Validar que el cliente existe
        usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente not found with id " + clienteId));

        return ordenRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId)
                .stream()
                .map(OrdenMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener órdenes por estado
     */
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerOrdenesPorEstado(OrdenEntity.EstadoOrden estado) {
        return ordenRepository.findByEstadoOrderByFechaCreacionDesc(estado)
                .stream()
                .map(OrdenMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear una nueva orden
     */
    public OrdenResponseDTO crearOrden(OrdenRequestDTO ordenRequest) {
        // Validar cliente
        usuarioEntity cliente = usuarioRepository.findById(ordenRequest.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente not found with id " + ordenRequest.getClienteId()));

        // Validar productos y obtener entidades
        List<Long> productoIds = ordenRequest.getItems().stream()
                .map(item -> item.getProductoId())
                .collect(Collectors.toList());

        List<ProductoEntity> productos = productoRepository.findAllById(productoIds);

        if (productos.size() != productoIds.size()) {
            throw new BadRequestException("Uno o más productos no existen");
        }

        // Validar stock disponible
        for (OrdenItemRequestDTO item : ordenRequest.getItems()) {
            ProductoEntity producto = productos.stream()
                    .filter(p -> p.getId().equals(item.getProductoId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "Producto not found with id " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException(
                        "Stock insuficiente para el producto: " + producto.getNombre());
            }
        }

        // Crear la orden
        OrdenEntity orden = OrdenMapping.toEntity(ordenRequest, cliente, productos);

        // Asignar la orden a cada ítem
        orden.getItems().forEach(item -> item.setOrden(orden));

        // Reducir stock de productos
        for (OrdenItemEntity item : orden.getItems()) {
            ProductoEntity producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        // Guardar la orden
        OrdenEntity savedOrden = ordenRepository.save(orden);

        return OrdenMapping.toResponseDTO(savedOrden);
    }

    /**
     * Actualizar el estado de una orden
     */
    public OrdenResponseDTO actualizarEstadoOrden(Long id, OrdenEntity.EstadoOrden nuevoEstado) {
        OrdenEntity orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden not found with id " + id));

        // Validar transiciones de estado válidas
        validarTransicionEstado(orden.getEstado(), nuevoEstado);

        orden.setEstado(nuevoEstado);
        OrdenEntity updatedOrden = ordenRepository.save(orden);

        return OrdenMapping.toResponseDTO(updatedOrden);
    }

    /**
     * Cancelar una orden
     */
    public OrdenResponseDTO cancelarOrden(Long id) {
        OrdenEntity orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden not found with id " + id));

        if (orden.getEstado() == OrdenEntity.EstadoOrden.CANCELADA) {
            throw new BadRequestException("La orden ya está cancelada");
        }

        if (orden.getEstado() == OrdenEntity.EstadoOrden.ENTREGADA) {
            throw new BadRequestException("No se puede cancelar una orden entregada");
        }

        // Restaurar stock de productos
        for (OrdenItemEntity item : orden.getItems()) {
            ProductoEntity producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }

        orden.setEstado(OrdenEntity.EstadoOrden.CANCELADA);
        OrdenEntity cancelledOrden = ordenRepository.save(orden);

        return OrdenMapping.toResponseDTO(cancelledOrden);
    }

    /**
     * Eliminar una orden (solo si está cancelada)
     */
    public void eliminarOrden(Long id) {
        OrdenEntity orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden not found with id " + id));

        if (orden.getEstado() != OrdenEntity.EstadoOrden.CANCELADA) {
            throw new BadRequestException("Solo se pueden eliminar órdenes canceladas");
        }

        ordenRepository.delete(orden);
    }

    /**
     * Validar transiciones de estado válidas
     */
    private void validarTransicionEstado(OrdenEntity.EstadoOrden estadoActual,
                                       OrdenEntity.EstadoOrden nuevoEstado) {
        switch (estadoActual) {
            case PENDIENTE:
                if (nuevoEstado != OrdenEntity.EstadoOrden.CONFIRMADA &&
                    nuevoEstado != OrdenEntity.EstadoOrden.CANCELADA) {
                    throw new BadRequestException(
                            "Desde PENDIENTE solo se puede pasar a CONFIRMADA o CANCELADA");
                }
                break;
            case CONFIRMADA:
                if (nuevoEstado != OrdenEntity.EstadoOrden.ENVIADA &&
                    nuevoEstado != OrdenEntity.EstadoOrden.CANCELADA) {
                    throw new BadRequestException(
                            "Desde CONFIRMADA solo se puede pasar a ENVIADA o CANCELADA");
                }
                break;
            case ENVIADA:
                if (nuevoEstado != OrdenEntity.EstadoOrden.ENTREGADA) {
                    throw new BadRequestException(
                            "Desde ENVIADA solo se puede pasar a ENTREGADA");
                }
                break;
            case ENTREGADA:
            case CANCELADA:
                throw new BadRequestException(
                        "No se puede cambiar el estado de una orden " + estadoActual);
        }
    }
}
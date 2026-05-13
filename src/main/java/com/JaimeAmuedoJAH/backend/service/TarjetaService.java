package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.entity.UsuarioEntity;
import com.JaimeAmuedoJAH.backend.repository.UsuarioRepository;
import com.JaimeAmuedoJAH.backend.repository.TarjetaRepository;
import com.JaimeAmuedoJAH.backend.entity.TarjetaEntity;
import com.JaimeAmuedoJAH.backend.dto.TarjetaRequestDTO;
import com.JaimeAmuedoJAH.backend.dto.TarjetaResponseDTO;
import com.JaimeAmuedoJAH.backend.mapping.TarjetaMapping;
import com.JaimeAmuedoJAH.backend.exceptions.ConflictException;
import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarjetaResponseDTO crearTarjeta(TarjetaRequestDTO request) {
        tarjetaRepository.findByNumeroTarjeta(request.getNumeroTarjeta())
                .ifPresent(t -> { throw new ConflictException("Ya existe una tarjeta con ese número"); });

        UsuarioEntity usuario = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + request.getClienteId()));

        TarjetaEntity tarjeta = TarjetaMapping.toEntity(request, usuario);
        return TarjetaMapping.toResponseDTO(tarjetaRepository.save(tarjeta));
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasPorCliente(Long clienteId) {
        return tarjetaRepository.findByUsuarioId(clienteId)
                .stream()
                .map(TarjetaMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarjetaResponseDTO obtenerTarjetaPorId(Long id) {
        return TarjetaMapping.toResponseDTO(
                tarjetaRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada con id " + id))
        );
    }

    public void eliminarTarjeta(Long id) {
        tarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada con id " + id));
        tarjetaRepository.deleteById(id);
    }

    public TarjetaEntity obtenerEntidadPorNumero(String numeroTarjeta) {
        return tarjetaRepository.findByNumeroTarjeta(numeroTarjeta)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
    }

    public void actualizarSaldo(TarjetaEntity tarjeta) {
        tarjetaRepository.save(tarjeta);
    }
}

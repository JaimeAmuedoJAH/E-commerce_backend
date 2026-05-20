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
import com.JaimeAmuedoJAH.backend.security.EncryptionUtil;
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
    private final EncryptionUtil encryptionUtil; // ✅ Añadido

    public TarjetaResponseDTO crearTarjeta(TarjetaRequestDTO request) {
        String hash = encryptionUtil.hashForSearch(request.getNumeroTarjeta());
        tarjetaRepository.findByNumeroHash(hash)
                .ifPresent(t -> { throw new ConflictException("Ya existe una tarjeta con ese número"); });

        UsuarioEntity usuario = usuarioRepository.findByPublicId(request.getClientePublicId())  
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.getClientePublicId()));

        TarjetaEntity tarjeta = TarjetaMapping.toEntity(request, usuario);
        tarjeta.setNumeroHash(hash);
        return TarjetaMapping.toResponseDTO(tarjetaRepository.save(tarjeta));
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetasPorCliente(String clientePublicId) {  
        UsuarioEntity usuario = usuarioRepository.findByPublicId(clientePublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + clientePublicId));
        return tarjetaRepository.findByUsuarioId(usuario.getId())
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
        // ✅ Buscar por hash en lugar de por número plano
        String hash = encryptionUtil.hashForSearch(numeroTarjeta);
        return tarjetaRepository.findByNumeroHash(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
    }

    public void actualizarSaldo(TarjetaEntity tarjeta) {
        tarjetaRepository.save(tarjeta);
    }
}
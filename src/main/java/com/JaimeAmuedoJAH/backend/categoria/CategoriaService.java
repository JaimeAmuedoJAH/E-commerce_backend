package com.JaimeAmuedoJAH.backend.categoria;

import com.JaimeAmuedoJAH.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Obtener todas las categorías con sus productos
     */
    public List<CategoriaResponseDTO> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaMapping::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una categoría por ID con sus productos
     */
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria not found with id " + id));
        return CategoriaMapping.toResponseDTO(categoria);
    }

    /**
     * Crear una nueva categoría
     */
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaDetails) {
        CategoriaEntity categoria = CategoriaMapping.toEntity(categoriaDetails);
        CategoriaEntity saved = categoriaRepository.save(categoria);
        return CategoriaMapping.toResponseDTO(saved);
    }

    /**
     * Actualizar una categoría existente
     */
    public CategoriaResponseDTO actualizarCategoria(
            Long id,
            CategoriaRequestDTO categoriaDetails) {

        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria not found with id " + id));

        // Aplicar cambios desde DTO hacia Entity
        CategoriaMapping.updateEntity(categoriaDetails, categoria);

        // Guardar los cambios
        CategoriaEntity updated = categoriaRepository.save(categoria);

        // Mapear Entity → ResponseDTO
        return CategoriaMapping.toResponseDTO(updated);
    }

    /**
     * Eliminar una categoría por ID
     */
    public void eliminarCategoria(Long id) {
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria not found with id " + id));
        categoriaRepository.delete(categoria);
    }
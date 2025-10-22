package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Productos;
import com.Arquitecturaservicios.gestioninventarios.Repository.productosRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.ProductoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;



import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final productosRepository productosRepository;
    private final BuildObjectMapper modelMapper;

    public ProductoService(BuildObjectMapper modelMapper, com.Arquitecturaservicios.gestioninventarios.Repository.productosRepository productosRepository) {
        this.modelMapper = modelMapper;
        this.productosRepository = productosRepository;
    }
    @Transactional
    public ProductoDto save(ProductoDto dto) {
        boolean existe = productosRepository.findByCodigo(dto.getCodigo()).isPresent();

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un producto con el código: " + dto.getCodigo()
            );
        }
        Productos nuevoProducto = BuildObjectMapper.converterTo(dto, Productos.class);
        Productos guardado = productosRepository.save(nuevoProducto);
        return BuildObjectMapper.converterTo(guardado, ProductoDto.class);
    }
    @Transactional
    public ProductoDto updateByCodigo(Integer codigo, Map<String, Object> dataUpdate) {
        return productosRepository.findByCodigo(codigo).map(mapper -> {
            Productos objectForUpdate = BuildObjectMapper.getInstance()
                    .getObjectForUpdate(mapper, dataUpdate);

            if (!Objects.equals(mapper.getCodigo(), objectForUpdate.getCodigo())) {
                this.validateCodigo(objectForUpdate);
            }

            if (objectForUpdate.getNombre() != null) {
                objectForUpdate.setNombre(objectForUpdate.getNombre().trim());
            }

            Productos updated = productosRepository.save(objectForUpdate);


            return BuildObjectMapper.converterTo(updated, ProductoDto.class);
        }).orElseThrow(() ->
                new RuntimeException("Producto con código " + codigo + " no encontrado"));
    }

    @Transactional
    public Page<ProductoDto> findAll(Pageable pageable) {
        return productosRepository.findAll(pageable)
                .map(producto -> {
                    ProductoDto dto = BuildObjectMapper.converterTo(producto, ProductoDto.class);
                    // Asegurar que el ID se mapee correctamente
                    if (dto.getId() == null && producto.getId() != null) {
                        dto.setId(producto.getId());
                    }
                    return dto;
                });
    }
    @Transactional
    public List<ProductoDto> findByMarcaContaining(String marca) {
        if (!StringUtils.hasText(marca)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El parámetro 'marca' es requerido");
        }

        return productosRepository.findByMarcaContainingIgnoreCase(marca.trim())
                .stream()
                .map(producto -> BuildObjectMapper.converterTo(producto, ProductoDto.class))
                .collect(Collectors.toList());
    }
    @Transactional
    public ProductoDto findById(Long id) {
        return productosRepository.findById(id)
                .map(producto -> BuildObjectMapper.converterTo(producto, ProductoDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto con ID " + id + " no encontrado"));
    }
    @Transactional
    public ProductoDto findByCodigo(Integer codigo) {
        return productosRepository.findByCodigo(codigo)
                .map(producto -> BuildObjectMapper.converterTo(producto, ProductoDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto con ID " + codigo + " no encontrado"));
    }



    public void deleteByCodigo(Integer codigo) {
        Productos producto = productosRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto con código " + codigo + " no encontrado"));

        productosRepository.delete(producto);
    }
    @Transactional
    public void deleteById(Long id) {
        if (!productosRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Producto con ID " + id + " no encontrado");
        }
        productosRepository.deleteById(id);
    }



    private void validateCodigo(Productos productos){
        Integer codigo= productos.getCodigo();

        if(StringUtils.hasText(String.valueOf(codigo))){

            boolean exists= productosRepository.existsByCodigo(codigo);
            if(exists){
                throw new IllegalArgumentException("El código ya existe en la base de datos");
            }
        }
    }



}

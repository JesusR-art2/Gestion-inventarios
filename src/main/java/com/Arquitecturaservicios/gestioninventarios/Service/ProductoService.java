package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Productos;
import com.Arquitecturaservicios.gestioninventarios.Repository.productosRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.ProductoDto;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

}

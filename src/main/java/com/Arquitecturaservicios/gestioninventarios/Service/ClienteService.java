package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Cliente;
import com.Arquitecturaservicios.gestioninventarios.Repository.ClienteRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.ClienteDto;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final BuildObjectMapper modelMapper;

    public ClienteService(BuildObjectMapper modelMapper, ClienteRepository clienteRepository) {
        this.modelMapper = modelMapper;
        this.clienteRepository = clienteRepository;
    }

    public ClienteDto save(ClienteDto dto) {
        // Validar DNI único
        if (clienteRepository.findByDocumento(dto.getDocumento()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cliente con el DNI: " + dto.getDocumento());
        }

        validarCliente(dto);

        Map<String, Object> context = new HashMap<>();
        Cliente cliente = modelMapper.createEntityFromDto(dto, Cliente.class, context);

        Cliente guardado = clienteRepository.save(cliente);
        return modelMapper.converterTo(guardado, ClienteDto.class);
    }

    @Transactional
    public ClienteDto update(Long id, Map<String, Object> dataUpdate) {
        return clienteRepository.findById(id).map(clienteExistente -> {
            Cliente clienteActualizado = modelMapper.getObjectForUpdate(clienteExistente, dataUpdate);

            // Validar si se cambió el DNI
            if (dataUpdate.containsKey("dni") &&
                    !clienteExistente.getDocumento().equals(clienteActualizado.getDocumento())) {
                validarDniUnico(clienteActualizado.getDocumento());
            }

            Cliente updated = clienteRepository.save(clienteActualizado);
            return modelMapper.converterTo(updated, ClienteDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente con ID " + id + " no encontrado"));
    }

    @Transactional
    public ClienteDto findById(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> BuildObjectMapper.converterTo(cliente, ClienteDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"));
    }

    @Transactional
    public ClienteDto findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento.trim().toUpperCase())
                .map(cliente -> BuildObjectMapper.converterTo(cliente, ClienteDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente con DNI " + documento + " no encontrado"));
    }

    @Transactional
    public List<ClienteDto> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(cliente -> BuildObjectMapper.converterTo(cliente, ClienteDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ClienteDto> findByNombreContaining(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(cliente -> BuildObjectMapper.converterTo(cliente, ClienteDto.class))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }

    private void validarCliente(ClienteDto dto) {
        if (!StringUtils.hasText(dto.getDocumento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DNI es requerido");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre es requerido");
        }
    }

    private void validarDniUnico(String dni) {
        if (clienteRepository.existsByDocumento(dni)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El DNI " + dni + " ya existe en la base de datos");
        }
    }


}

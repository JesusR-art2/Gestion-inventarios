package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Rol;
import com.Arquitecturaservicios.gestioninventarios.Repository.RolRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.RolDto;
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
public class RolService {
    private final RolRepository rolRepository;
    private final BuildObjectMapper modelMapper;

    public RolService(BuildObjectMapper modelMapper, RolRepository rolRepository) {
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
    }

    public RolDto save(RolDto dto) {
        if (rolRepository.findByName(dto.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un rol con el nombre: " + dto.getName());
        }

        validarRol(dto);

        Map<String, Object> context = new HashMap<>();
        Rol rol = modelMapper.createEntityFromDto(dto, Rol.class, context);

        Rol guardado = rolRepository.save(rol);
        return modelMapper.converterTo(guardado, RolDto.class);
    }

    @Transactional
    public RolDto update(Long id, Map<String, Object> dataUpdate) {
        return rolRepository.findById(id).map(rolExistente -> {
            Rol rolActualizado = modelMapper.getObjectForUpdate(rolExistente, dataUpdate);

            if (dataUpdate.containsKey("name") &&
                    !rolExistente.getName().equals(rolActualizado.getName())) {
                validarNombreUnico(rolActualizado.getName());
            }

            Rol updated = rolRepository.save(rolActualizado);
            return modelMapper.converterTo(updated, RolDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Rol con ID " + id + " no encontrado"));
    }

    @Transactional
    public RolDto findById(Long id) {
        return rolRepository.findById(id)
                .map(rol -> BuildObjectMapper.converterTo(rol, RolDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol no encontrado"));
    }

    @Transactional
    public RolDto findByName(String name) {
        return rolRepository.findByName(name)
                .map(rol -> BuildObjectMapper.converterTo(rol, RolDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol con nombre " + name + " no encontrado"));
    }

    @Transactional
    public List<RolDto> findAll() {
        return rolRepository.findAll()
                .stream()
                .map(rol -> BuildObjectMapper.converterTo(rol, RolDto.class))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }
        rolRepository.deleteById(id);
    }

    private void validarRol(RolDto dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre es requerido");
        }
        if (!StringUtils.hasText(dto.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descripción es requerida");
        }
    }

    private void validarNombreUnico(String name) {
        if (rolRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El nombre " + name + " ya existe en la base de datos");
        }
    }
}

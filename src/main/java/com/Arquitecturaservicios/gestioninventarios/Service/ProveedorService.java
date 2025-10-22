package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Proveedor;
import com.Arquitecturaservicios.gestioninventarios.Repository.ProveedorRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.ProveedorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final BuildObjectMapper modelMapper;

    public ProveedorService(BuildObjectMapper modelMapper, ProveedorRepository proveedorRepository) {
        this.modelMapper = modelMapper;
        this.proveedorRepository = proveedorRepository;
    }

    public ProveedorDto save(ProveedorDto dto) {
        try {
            System.out.println("=== CREANDO PROVEEDOR ===");
            System.out.println("RUC: " + dto.getRuc());
            System.out.println("Nombre: " + dto.getNombre());
            System.out.println("Contacto: " + dto.getContacto());

            // Validar RUC único
            if (proveedorRepository.findByRuc(dto.getRuc()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe un proveedor con el RUC: " + dto.getRuc());
            }

            validarProveedor(dto);

            // Mapeo automático con valores por defecto
            Map<String, Object> context = Map.of();
            Proveedor proveedor = modelMapper.createEntityFromDto(dto, Proveedor.class, context);

            Proveedor guardado = proveedorRepository.save(proveedor);
            ProveedorDto resultado = modelMapper.converterTo(guardado, ProveedorDto.class);

            System.out.println("Proveedor creado exitosamente: " + resultado.getId());
            return resultado;

        } catch (ResponseStatusException e) {
            System.err.println("Error de validación: " + e.getReason());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado al crear proveedor: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno al crear proveedor: " + e.getMessage());
        }
    }


    @Transactional
    public ProveedorDto update(Long id, Map<String, Object> dataUpdate) {
        return proveedorRepository.findById(id).map(proveedorExistente -> {
            try {
                System.out.println("=== ACTUALIZANDO PROVEEDOR ID: " + id + " ===");
                System.out.println("Datos a actualizar: " + dataUpdate);

                // Actualización parcial automática
                Proveedor proveedorActualizado = modelMapper.getObjectForUpdate(proveedorExistente, dataUpdate);

                // Validar si se cambió el RUC
                if (dataUpdate.containsKey("ruc") &&
                        !proveedorExistente.getRuc().equals(proveedorActualizado.getRuc())) {
                    validarRucUnico(proveedorActualizado.getRuc());
                }

                // Aplicar formato a campos de texto
                if (proveedorActualizado.getNombre() != null) {
                    proveedorActualizado.setNombre(proveedorActualizado.getNombre().trim());
                }
                if (proveedorActualizado.getContacto() != null) {
                    proveedorActualizado.setContacto(proveedorActualizado.getContacto().trim());
                }
                if (proveedorActualizado.getEmail() != null) {
                    proveedorActualizado.setEmail(proveedorActualizado.getEmail().trim().toLowerCase());
                }

                Proveedor updated = proveedorRepository.save(proveedorActualizado);
                ProveedorDto resultado = modelMapper.converterTo(updated, ProveedorDto.class);

                System.out.println("Proveedor actualizado exitosamente");
                return resultado;

            } catch (Exception e) {
                System.err.println("Error al actualizar proveedor: " + e.getMessage());
                throw e;
            }
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Proveedor con ID " + id + " no encontrado"));
    }


    @Transactional(readOnly = true)
    public ProveedorDto findById(Long id) {
        return proveedorRepository.findById(id)
                .map(proveedor -> {
                    ProveedorDto dto = modelMapper.converterTo(proveedor, ProveedorDto.class);
                    System.out.println("Proveedor encontrado: " + dto.getNombre());
                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Proveedor con ID " + id + " no encontrado"));
    }


    @Transactional(readOnly = true)
    public ProveedorDto findByRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc.trim().replaceAll("\\s+", ""))
                .map(proveedor -> {
                    ProveedorDto dto = modelMapper.converterTo(proveedor, ProveedorDto.class);
                    System.out.println("Proveedor encontrado por RUC: " + dto.getNombre());
                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Proveedor con RUC " + ruc + " no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ProveedorDto> findAll() {
        List<ProveedorDto> proveedores = proveedorRepository.findAll()
                .stream()
                .map(proveedor -> modelMapper.converterTo(proveedor, ProveedorDto.class))
                .collect(Collectors.toList());

        System.out.println("Total proveedores encontrados: " + proveedores.size());
        return proveedores;
    }


    @Transactional(readOnly = true)
    public List<ProveedorDto> findByNombreContaining(String nombre) {
        List<ProveedorDto> proveedores = proveedorRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(proveedor -> modelMapper.converterTo(proveedor, ProveedorDto.class))
                .collect(Collectors.toList());

        System.out.println("Proveedores encontrados por nombre '" + nombre + "': " + proveedores.size());
        return proveedores;
    }


    @Transactional(readOnly = true)
    public List<ProveedorDto> findByContactoContaining(String contacto) {
        List<ProveedorDto> proveedores = proveedorRepository.findByContactoContainingIgnoreCase(contacto)
                .stream()
                .map(proveedor -> modelMapper.converterTo(proveedor, ProveedorDto.class))
                .collect(Collectors.toList());

        System.out.println("Proveedores encontrados por contacto '" + contacto + "': " + proveedores.size());
        return proveedores;
    }


    public void deleteById(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor con ID " + id + " no encontrado");
        }

        try {
            proveedorRepository.deleteById(id);
            System.out.println("Proveedor eliminado ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al eliminar proveedor: " + e.getMessage());
        }
    }


    private void validarProveedor(ProveedorDto dto) {
        if (!StringUtils.hasText(dto.getRuc())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUC es requerido");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre es requerido");
        }

        String ruc = dto.getRuc().trim().replaceAll("\\s+", "");
        if (!ruc.matches("\\d{10,13}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "RUC debe contener entre 10 y 13 dígitos");
        }

        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }

        System.out.println("Proveedor validado correctamente");
    }

    private void validarRucUnico(String ruc) {
        if (proveedorRepository.existsByRuc(ruc)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El RUC " + ruc + " ya existe en la base de datos");
        }
    }


    @Transactional(readOnly = true)
    public boolean existsByRuc(String ruc) {
        return proveedorRepository.existsByRuc(ruc);
    }


    @Transactional(readOnly = true)
    public long count() {
        return proveedorRepository.count();
    }
}
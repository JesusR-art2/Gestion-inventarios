package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Rol;
import com.Arquitecturaservicios.gestioninventarios.Entities.Usuario;
import com.Arquitecturaservicios.gestioninventarios.Entities.UsuarioRol;
import com.Arquitecturaservicios.gestioninventarios.Repository.RolRepository;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRepository;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRolRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioRolDto;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioRolService {
    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BuildObjectMapper modelMapper;

    public UsuarioRolService(BuildObjectMapper modelMapper, UsuarioRolRepository usuarioRolRepository,
                             UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.modelMapper = modelMapper;
        this.usuarioRolRepository = usuarioRolRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public UsuarioRolDto save(UsuarioRolDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario con ID " + dto.getUsuarioId() + " no encontrado"));

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol con ID " + dto.getRolId() + " no encontrado"));

        if (usuarioRolRepository.findByUsuarioIdAndRolId(dto.getUsuarioId(), dto.getRolId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya tiene asignado este rol");
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        UsuarioRol guardado = usuarioRolRepository.save(usuarioRol);
        return modelMapper.converterTo(guardado, UsuarioRolDto.class);
    }

    @Transactional
    public UsuarioRolDto findById(Long id) {
        return usuarioRolRepository.findById(id)
                .map(usuarioRol -> {
                    UsuarioRolDto dto = modelMapper.converterTo(usuarioRol, UsuarioRolDto.class);
                    dto.setUsuarioId(usuarioRol.getUsuario().getId());
                    dto.setRolId(usuarioRol.getRol().getId());
                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "UsuarioRol no encontrado"));
    }

    @Transactional
    public List<UsuarioRolDto> findByUsuarioId(Long usuarioId) {
        return usuarioRolRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(usuarioRol -> {
                    UsuarioRolDto dto = modelMapper.converterTo(usuarioRol, UsuarioRolDto.class);
                    dto.setUsuarioId(usuarioRol.getUsuario().getId());
                    dto.setRolId(usuarioRol.getRol().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UsuarioRolDto> findAll() {
        return usuarioRolRepository.findAll()
                .stream()
                .map(usuarioRol -> {
                    UsuarioRolDto dto = modelMapper.converterTo(usuarioRol, UsuarioRolDto.class);
                    dto.setUsuarioId(usuarioRol.getUsuario().getId());
                    dto.setRolId(usuarioRol.getRol().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!usuarioRolRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UsuarioRol no encontrado");
        }
        usuarioRolRepository.deleteById(id);
    }

    public void deleteByUsuarioIdAndRolId(Long usuarioId, Long rolId) {
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuarioIdAndRolId(usuarioId, rolId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Asignación de rol no encontrada"));
        usuarioRolRepository.deleteById(usuarioRol.getId());
    }
}

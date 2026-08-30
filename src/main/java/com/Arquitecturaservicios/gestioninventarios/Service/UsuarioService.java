package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Usuario;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioDto;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BuildObjectMapper modelMapper;

    public UsuarioService(BuildObjectMapper modelMapper, UsuarioRepository usuarioRepository) {
        this.modelMapper = modelMapper;
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDto save(UsuarioDto dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con el email: " + dto.getEmail());
        }

        validarUsuario(dto);

        Map<String, Object> context = new HashMap<>();
        Usuario usuario = modelMapper.createEntityFromDto(dto, Usuario.class, context);

        Usuario guardado = usuarioRepository.save(usuario);
        return modelMapper.converterTo(guardado, UsuarioDto.class);
    }

    @Transactional
    public UsuarioDto update(Long id, Map<String, Object> dataUpdate) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            Usuario usuarioActualizado = modelMapper.getObjectForUpdate(usuarioExistente, dataUpdate);

            if (dataUpdate.containsKey("email") &&
                    !usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
                validarEmailUnico(usuarioActualizado.getEmail());
            }

            Usuario updated = usuarioRepository.save(usuarioActualizado);
            return modelMapper.converterTo(updated, UsuarioDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Usuario con ID " + id + " no encontrado"));
    }

    @Transactional
    public UsuarioDto findById(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> BuildObjectMapper.converterTo(usuario, UsuarioDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));
    }

    @Transactional
    public UsuarioDto findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> BuildObjectMapper.converterTo(usuario, UsuarioDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario con email " + email + " no encontrado"));
    }

    @Transactional
    public List<UsuarioDto> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> BuildObjectMapper.converterTo(usuario, UsuarioDto.class))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public UsuarioDto updateLastLogin(Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setLastLogin(LocalDateTime.now());
            Usuario updated = usuarioRepository.save(usuario);
            return modelMapper.converterTo(updated, UsuarioDto.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Usuario con ID " + id + " no encontrado"));
    }

    private void validarUsuario(UsuarioDto dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email es requerido");
        }
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password es requerido");
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El email " + email + " ya existe en la base de datos");
        }
    }
}

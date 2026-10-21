package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Rol;
import com.Arquitecturaservicios.gestioninventarios.Entities.Usuario;
import com.Arquitecturaservicios.gestioninventarios.Entities.UsuarioRol;
import com.Arquitecturaservicios.gestioninventarios.Repository.RolRepository;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRepository;
import com.Arquitecturaservicios.gestioninventarios.Repository.UsuarioRolRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioDto;
import com.Arquitecturaservicios.gestioninventarios.dto.usuarioResponseDto;
import com.Arquitecturaservicios.gestioninventarios.exchange.UserRole;
import com.Arquitecturaservicios.gestioninventarios.exchange.mappers.usuarioMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BuildObjectMapper modelMapper;
    private final UsuarioRolRepository usuarioRolRepository;
    private final usuarioMapper usuariomapper;

    public UsuarioService(BuildObjectMapper modelMapper, UsuarioRepository usuarioRepository, RolRepository rolRepository, UsuarioRolRepository usuarioRolRepository, PasswordEncoder passwordEncoder, usuarioMapper usuariomapper) {
        this.modelMapper = modelMapper;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.usuariomapper = usuariomapper;
    }

    @Transactional
    public usuarioResponseDto save(UsuarioDto dto, Authentication auth) {
        validarPermisoSobreRol(dto.rol(), auth);
        validarEmailUnico(dto.email());
        Rol rol = buscarRol(dto.rol());

        Usuario usuario = usuariomapper.toEntity(dto);
        usuario.getRoles().add(UsuarioRol.builder().usuario(usuario).rol(rol).build());

        return usuariomapper.toResponse(usuarioRepository.save(usuario));
    }
    private usuarioResponseDto toResponse(Usuario u, String rol) {
        return new usuarioResponseDto(u.getId(), u.getEmail(), u.getActive(),
                List.of(rol), u.getLastLogin(), u.getDateCreate(), u.getDateUpdate());
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



    @Transactional(readOnly = true)
    public UserRole obtenerRolPrincipal(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return UserRole.valueOf(u.getRoles().get(0).getRol().getName());
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
        if (!StringUtils.hasText(dto.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email es requerido");
        }
        if (!StringUtils.hasText(dto.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password es requerido");
        }
    }


    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El email " + email + " ya existe en la base de datos");
        }
    }
    private void validarPermisoSobreRol(String rol, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !Set.of("AUX_BODEGA", "VENDEDOR").contains(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para asignar el rol: " + rol);
        }
    }
    private Rol buscarRol(String nombre) {
        return rolRepository.findByName(nombre)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El rol no existe: " + nombre));
    }
}

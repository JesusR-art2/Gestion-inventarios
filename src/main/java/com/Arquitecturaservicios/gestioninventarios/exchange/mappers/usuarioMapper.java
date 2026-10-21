package com.Arquitecturaservicios.gestioninventarios.exchange.mappers;

import com.Arquitecturaservicios.gestioninventarios.Entities.Usuario;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioDto;
import com.Arquitecturaservicios.gestioninventarios.dto.usuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(componentModel = "spring")
public abstract class usuarioMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "dateCreate", ignore = true)
    @Mapping(target = "dateUpdate", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.password()))")
    public abstract Usuario toEntity(UsuarioDto dto);

    @Mapping(target = "rol",
            expression = "java(usuario.getRoles().stream().map(ur -> ur.getRol().getName()).toList())")
    public abstract usuarioResponseDto toResponse(Usuario usuario);
}

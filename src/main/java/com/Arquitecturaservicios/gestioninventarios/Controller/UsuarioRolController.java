package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.UsuarioRolService;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioRolDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario-roles")
public class UsuarioRolController {
    private final UsuarioRolService usuarioRolService;

    public UsuarioRolController(UsuarioRolService usuarioRolService) {
        this.usuarioRolService = usuarioRolService;
    }

    @PostMapping
    public ResponseEntity<UsuarioRolDto> create(@Valid @RequestBody UsuarioRolDto usuarioRolDto) {
        UsuarioRolDto saved = usuarioRolService.save(usuarioRolDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRolDto>> findAll() {
        List<UsuarioRolDto> usuarioRoles = usuarioRolService.findAll();
        return ResponseEntity.ok(usuarioRoles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRolDto> findById(@PathVariable Long id) {
        UsuarioRolDto usuarioRol = usuarioRolService.findById(id);
        return ResponseEntity.ok(usuarioRol);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioRolDto>> findByUsuarioId(@PathVariable Long usuarioId) {
        List<UsuarioRolDto> usuarioRoles = usuarioRolService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(usuarioRoles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioRolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}/rol/{rolId}")
    public ResponseEntity<Void> deleteByUsuarioIdAndRolId(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        usuarioRolService.deleteByUsuarioIdAndRolId(usuarioId, rolId);
        return ResponseEntity.noContent().build();
    }
}

package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.UsuarioService;
import com.Arquitecturaservicios.gestioninventarios.dto.UsuarioDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> create(@Valid @RequestBody UsuarioDto usuarioDto) {
        UsuarioDto saved = usuarioService.save(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        UsuarioDto updated = usuarioService.update(id, updates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> findAll() {
        List<UsuarioDto> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> findById(@PathVariable Long id) {
        UsuarioDto usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDto> findByEmail(@PathVariable String email) {
        UsuarioDto usuario = usuarioService.findByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}/last-login")
    public ResponseEntity<UsuarioDto> updateLastLogin(@PathVariable Long id) {
        UsuarioDto usuario = usuarioService.updateLastLogin(id);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.RolService;
import com.Arquitecturaservicios.gestioninventarios.dto.RolDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<RolDto> create(@Valid @RequestBody RolDto rolDto) {
        RolDto saved = rolService.save(rolDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RolDto> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        RolDto updated = rolService.update(id, updates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<RolDto>> findAll() {
        List<RolDto> roles = rolService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDto> findById(@PathVariable Long id) {
        RolDto rol = rolService.findById(id);
        return ResponseEntity.ok(rol);
    }

    @GetMapping("/nombre/{name}")
    public ResponseEntity<RolDto> findByName(@PathVariable String name) {
        RolDto rol = rolService.findByName(name);
        return ResponseEntity.ok(rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

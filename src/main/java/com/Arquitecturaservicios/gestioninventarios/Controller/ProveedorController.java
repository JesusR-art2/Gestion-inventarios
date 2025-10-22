package com.Arquitecturaservicios.gestioninventarios.Controller;



import com.Arquitecturaservicios.gestioninventarios.Service.ProveedorService;
import com.Arquitecturaservicios.gestioninventarios.dto.ProveedorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String mostrarProveedores(Model model) {
        List<ProveedorDto> proveedores = proveedorService.findAll();
        model.addAttribute("proveedor", proveedores);
        return "proveedor";
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ProveedorDto> create(@RequestBody ProveedorDto proveedorDto) {
        try {
            ProveedorDto saved = proveedorService.save(proveedorDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProveedorDto> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            ProveedorDto updated = proveedorService.update(id, updates);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ProveedorDto>> findAll() {
        List<ProveedorDto> proveedores = proveedorService.findAll();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProveedorDto> findById(@PathVariable Long id) {
        try {
            ProveedorDto proveedor = proveedorService.findById(id);
            return ResponseEntity.ok(proveedor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/ruc/{ruc}")
    @ResponseBody
    public ResponseEntity<ProveedorDto> findByRuc(@PathVariable String ruc) {
        try {
            ProveedorDto proveedor = proveedorService.findByRuc(ruc);
            return ResponseEntity.ok(proveedor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<ProveedorDto>> findByNombre(@RequestParam String nombre) {
        List<ProveedorDto> proveedores = proveedorService.findByNombreContaining(nombre);
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/api/buscar/contacto")
    @ResponseBody
    public ResponseEntity<List<ProveedorDto>> findByContacto(@RequestParam String contacto) {
        List<ProveedorDto> proveedores = proveedorService.findByContactoContaining(contacto);
        return ResponseEntity.ok(proveedores);
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            proveedorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
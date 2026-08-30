package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.ClienteService;
import com.Arquitecturaservicios.gestioninventarios.dto.ClienteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    @PostMapping
    @ResponseBody
    public ResponseEntity<ClienteDto> create(@RequestBody ClienteDto clienteDto) {
        ClienteDto saved = clienteService.save(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ClienteDto> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        ClienteDto updated = clienteService.update(id, updates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ClienteDto>> findAll() {
        List<ClienteDto> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ClienteDto> findById(@PathVariable Long id) {
        ClienteDto cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/documento/{documento}")
    @ResponseBody
    public ResponseEntity<ClienteDto> findByDocumento(@PathVariable String documento) {
        ClienteDto cliente = clienteService.findByDocumento(documento);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<ClienteDto>> findByNombre(@RequestParam String nombre) {
        List<ClienteDto> clientes = clienteService.findByNombreContaining(nombre);
        return ResponseEntity.ok(clientes);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
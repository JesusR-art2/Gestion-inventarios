package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.VentaService;
import com.Arquitecturaservicios.gestioninventarios.dto.VentaDto;
import com.Arquitecturaservicios.gestioninventarios.Entities.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ventas")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String mostrarVentas(Model model) {
        List<VentaDto> ventas = ventaService.findAll();
        model.addAttribute("ventas", ventas);
        return "venta";
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<VentaDto> create(@RequestBody VentaDto ventaDto) {
        try {
            VentaDto saved = ventaService.save(ventaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<VentaDto> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            VentaDto updated = ventaService.update(id, updates);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/api/{id}/estado")
    @ResponseBody
    public ResponseEntity<VentaDto> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            EstadoVenta nuevoEstado = EstadoVenta.valueOf(request.get("estado"));
            VentaDto updated = ventaService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<VentaDto>> findAll() {
        List<VentaDto> ventas = ventaService.findAll();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<VentaDto> findById(@PathVariable Long id) {
        try {
            VentaDto venta = ventaService.findById(id);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/factura/{numeroFactura}")
    @ResponseBody
    public ResponseEntity<VentaDto> findByNumeroFactura(@PathVariable String numeroFactura) {
        try {
            VentaDto venta = ventaService.findByNumeroFactura(numeroFactura);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<List<VentaDto>> findByClienteId(@PathVariable Long clienteId) {
        try {
            List<VentaDto> ventas = ventaService.findByClienteId(clienteId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/estado/{estado}")
    @ResponseBody
    public ResponseEntity<List<VentaDto>> findByEstado(@PathVariable EstadoVenta estado) {
        try {
            List<VentaDto> ventas = ventaService.findByEstado(estado);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/api/rango-fechas")
    @ResponseBody
    public ResponseEntity<List<VentaDto>> findByFechaVentaBetween(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            List<VentaDto> ventas = ventaService.findByFechaVentaBetween(inicio, fin);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/api/total-periodo")
    @ResponseBody
    public ResponseEntity<Double> getTotalVentasPorPeriodo(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            Double total = ventaService.getTotalVentasPorPeriodo(inicio, fin);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ventaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/api/existe-factura/{numeroFactura}")
    @ResponseBody
    public ResponseEntity<Boolean> existsByNumeroFactura(@PathVariable String numeroFactura) {
        try {
            boolean existe = ventaService.existsByNumeroFactura(numeroFactura);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/api/count")
    @ResponseBody
    public ResponseEntity<Long> count() {
        try {
            long cantidad = ventaService.count();
            return ResponseEntity.ok(cantidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }
}
package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.ProductoService;
import com.Arquitecturaservicios.gestioninventarios.dto.ProductoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductosController {
    private final ProductoService productoService;

    public ProductosController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/save")
    public ResponseEntity<ProductoDto> save( @RequestBody ProductoDto productoDto) {
        ProductoDto productoGuardado = productoService.save( productoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
    }

    @PutMapping("/{codigo}")
    public ProductoDto updateByCodigo(@PathVariable("codigo") Integer codigo,
                                      @RequestBody Map<String,Object> mapDataUpdate){
        return productoService.updateByCodigo(codigo, mapDataUpdate);
    }
    @GetMapping("/paginados")
    public ResponseEntity<Page<ProductoDto>> getProductosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductoDto> productos = productoService.findAll(pageable);
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/buscar/marca")
    public ResponseEntity<List<ProductoDto>> searchProductosByNombre(
            @RequestParam("marca") String marca) {
        List<ProductoDto> productos = productoService.findByMarcaContaining(marca);
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProductoDto> getProductoById(
            @PathVariable("id") Long id) {
        ProductoDto producto = productoService.findById(id);
        return ResponseEntity.ok(producto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoById(@PathVariable("id") Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

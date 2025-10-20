package com.Arquitecturaservicios.gestioninventarios.Controller;

import com.Arquitecturaservicios.gestioninventarios.Service.ProductoService;
import com.Arquitecturaservicios.gestioninventarios.dto.ProductoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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



}

package com.Arquitecturaservicios.gestioninventarios.Service;

import com.Arquitecturaservicios.gestioninventarios.Config.BuildObjectMapper;
import com.Arquitecturaservicios.gestioninventarios.Entities.Venta;
import com.Arquitecturaservicios.gestioninventarios.Entities.DetalleVenta;
import com.Arquitecturaservicios.gestioninventarios.Entities.Cliente;
import com.Arquitecturaservicios.gestioninventarios.exchange.EstadoVenta;
import com.Arquitecturaservicios.gestioninventarios.Repository.VentaRepository;
import com.Arquitecturaservicios.gestioninventarios.Repository.ClienteRepository;
import com.Arquitecturaservicios.gestioninventarios.dto.VentaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaService {
    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BuildObjectMapper modelMapper;

    public VentaService(BuildObjectMapper modelMapper, VentaRepository ventaRepository, ClienteRepository clienteRepository) {
        this.modelMapper = modelMapper;
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
    }

    public VentaDto save(VentaDto dto) {
        logger.info("Creando venta - Factura: {}, Cliente ID: {}, Total: {}", 
                dto.getNumeroFactura(), dto.getClienteId(), dto.getTotal());

        if (ventaRepository.findByNumeroFactura(dto.getNumeroFactura()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una venta con el número de factura: " + dto.getNumeroFactura());
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente con ID " + dto.getClienteId() + " no encontrado"));

        validarVenta(dto);

        Map<String, Object> context = Map.of();
        Venta venta = modelMapper.createEntityFromDto(dto, Venta.class, context);

        venta.setCliente(cliente);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(EstadoVenta.PENDIENTE);

        if (dto.getDetalles() != null) {
            List<DetalleVenta> detalles = dto.getDetalles().stream()
                    .map(detalleDto -> {
                        DetalleVenta detalle = modelMapper.createEntityFromDto(detalleDto, DetalleVenta.class, context);
                        detalle.setVenta(venta);
                        if (detalle.getSubtotal() == null) {
                            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                        }
                        return detalle;
                    })
                    .collect(Collectors.toList());
            venta.setDetalles(detalles);
        }

        if (venta.getDetalles() != null) {
            double totalCalculado = venta.getDetalles().stream()
                    .mapToDouble(DetalleVenta::getSubtotal)
                    .sum();

            if (Math.abs(totalCalculado - venta.getTotal()) > 0.01) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El total de la venta no coincide con la suma de los detalles");
            }
        }

        Venta guardada = ventaRepository.save(venta);
        VentaDto resultado = modelMapper.converterTo(guardada, VentaDto.class);

        logger.info("Venta creada exitosamente: ID {}", resultado.getId());
        return resultado;
    }

    @Transactional
    public VentaDto update(Long id, Map<String, Object> dataUpdate) {
        return ventaRepository.findById(id).map(ventaExistente -> {
            logger.info("Actualizando venta ID: {}, datos: {}", id, dataUpdate);

            if (ventaExistente.getEstado() == EstadoVenta.COMPLETADA ||
                    ventaExistente.getEstado() == EstadoVenta.CANCELADA) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se puede modificar una venta en estado: " + ventaExistente.getEstado());
            }

            Venta ventaActualizada = modelMapper.getObjectForUpdate(ventaExistente, dataUpdate);

            if (dataUpdate.containsKey("numeroFactura") &&
                    !ventaExistente.getNumeroFactura().equals(ventaActualizada.getNumeroFactura())) {
                validarNumeroFacturaUnico(ventaActualizada.getNumeroFactura());
            }

            if (dataUpdate.containsKey("clienteId")) {
                Long nuevoClienteId = Long.valueOf(dataUpdate.get("clienteId").toString());
                Cliente cliente = clienteRepository.findById(nuevoClienteId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente con ID " + nuevoClienteId + " no encontrado"));
                ventaActualizada.setCliente(cliente);
            }

            if (ventaActualizada.getNumeroFactura() != null) {
                ventaActualizada.setNumeroFactura(ventaActualizada.getNumeroFactura().trim().toUpperCase());
            }

            Venta updated = ventaRepository.save(ventaActualizada);
            VentaDto resultado = modelMapper.converterTo(updated, VentaDto.class);

            logger.info("Venta actualizada exitosamente: ID {}", id);
            return resultado;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Venta con ID " + id + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public VentaDto findById(Long id) {
        return ventaRepository.findById(id)
                .map(venta -> {
                    VentaDto dto = modelMapper.converterTo(venta, VentaDto.class);
                    logger.debug("Venta encontrada: {}", dto.getNumeroFactura());
                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Venta con ID " + id + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public VentaDto findByNumeroFactura(String numeroFactura) {
        return ventaRepository.findByNumeroFactura(numeroFactura.trim().toUpperCase())
                .map(venta -> {
                    VentaDto dto = modelMapper.converterTo(venta, VentaDto.class);
                    logger.debug("Venta encontrada por número de factura: {}", dto.getNumeroFactura());
                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Venta con número de factura " + numeroFactura + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<VentaDto> findAll() {
        List<VentaDto> ventas = ventaRepository.findAll()
                .stream()
                .map(venta -> modelMapper.converterTo(venta, VentaDto.class))
                .collect(Collectors.toList());

        logger.debug("Total ventas encontradas: {}", ventas.size());
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<VentaDto> findByClienteId(Long clienteId) {
        List<VentaDto> ventas = ventaRepository.findByClienteId(clienteId)
                .stream()
                .map(venta -> modelMapper.converterTo(venta, VentaDto.class))
                .collect(Collectors.toList());

        logger.debug("Ventas encontradas para cliente ID '{}': {}", clienteId, ventas.size());
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<VentaDto> findByEstado(EstadoVenta estado) {
        List<VentaDto> ventas = ventaRepository.findByEstado(estado)
                .stream()
                .map(venta -> BuildObjectMapper.converterTo(venta, VentaDto.class))
                .collect(Collectors.toList());

        logger.debug("Ventas encontradas con estado '{}': {}", estado, ventas.size());
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<VentaDto> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<VentaDto> ventas = ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin)
                .stream()
                .map(venta -> modelMapper.converterTo(venta, VentaDto.class))
                .collect(Collectors.toList());

        logger.debug("Ventas encontradas entre {} y {}: {}", fechaInicio, fechaFin, ventas.size());
        return ventas;
    }

    public void deleteById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Venta con ID " + id + " no encontrada"));

        if (venta.getEstado() == EstadoVenta.COMPLETADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar una venta completada");
        }

        ventaRepository.deleteById(id);
        logger.info("Venta eliminada ID: {}", id);
    }

    @Transactional
    public VentaDto cambiarEstado(Long id, EstadoVenta nuevoEstado) {
        return ventaRepository.findById(id).map(venta -> {
            logger.info("Cambiando estado venta ID: {} - Estado actual: {} -> Nuevo estado: {}", 
                    id, venta.getEstado(), nuevoEstado);

            validarTransicionEstado(venta.getEstado(), nuevoEstado);

            venta.setEstado(nuevoEstado);
            Venta actualizada = ventaRepository.save(venta);
            VentaDto resultado = modelMapper.converterTo(actualizada, VentaDto.class);

            logger.info("Estado de venta actualizado exitosamente: ID {}", id);
            return resultado;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Venta con ID " + id + " no encontrada"));
    }

    private void validarVenta(VentaDto dto) {
        if (!StringUtils.hasText(dto.getNumeroFactura())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de factura es requerido");
        }
        if (dto.getClienteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente es requerido");
        }
        if (dto.getTotal() == null || dto.getTotal() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total debe ser mayor a 0");
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La venta debe tener al menos un detalle");
        }

        // Validar formato del número de factura
        String numeroFactura = dto.getNumeroFactura().trim().toUpperCase();
        if (!numeroFactura.matches("[A-Z0-9-]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Número de factura solo puede contener letras, números y guiones");
        }

        logger.debug("Venta validada correctamente");
    }

    private void validarNumeroFacturaUnico(String numeroFactura) {
        if (ventaRepository.existsByNumeroFactura(numeroFactura)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El número de factura " + numeroFactura + " ya existe en la base de datos");
        }
    }

    private void validarTransicionEstado(EstadoVenta estadoActual, EstadoVenta nuevoEstado) {
        // Validar transiciones permitidas
        if (estadoActual == EstadoVenta.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede modificar una venta cancelada");
        }
        if (estadoActual == EstadoVenta.COMPLETADA && nuevoEstado != EstadoVenta.COMPLETADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el estado de una venta completada");
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByNumeroFactura(String numeroFactura) {
        return ventaRepository.existsByNumeroFactura(numeroFactura);
    }

    @Transactional(readOnly = true)
    public long count() {
        return ventaRepository.count();
    }

    @Transactional(readOnly = true)
    public Double getTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.sumTotalByFechaVentaBetweenAndEstado(
                fechaInicio, fechaFin, EstadoVenta.COMPLETADA);
    }
}
package com.Arquitecturaservicios.gestioninventarios.Config;
import com.Arquitecturaservicios.gestioninventarios.dto.VentaDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class BuildObjectMapper {
    private static final BuildObjectMapper INSTANCE = new BuildObjectMapper();
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        return objectMapper;
    }

    @SuppressWarnings("unchecked")
    public static <T> T converterTo(Object reference, Class<T> toConverter) {
        Map<String, Object> baseMap = getObjectMapper().convertValue(reference, Map.class);
        return getObjectMapper().convertValue(baseMap, toConverter);
    }


    public <T> T getObjectForUpdate(T baseObject, Map<String, Object> dataUpdate) {
        Map<String, Object> baseMap = getObjectMapper().convertValue(baseObject, Map.class);
        Set<String> allKeys = new HashSet<>(baseMap.keySet());
        allKeys.addAll(dataUpdate.keySet());

        for (String key : allKeys) {
            if (dataUpdate.containsKey(key) && dataUpdate.get(key) != null) {
                baseMap.put(key, dataUpdate.get(key));
            }
        }

        T updatedObject = (T) getObjectMapper().convertValue(baseMap, baseObject.getClass());
        return preserveJpaRelations(baseObject, updatedObject, dataUpdate);
    }

    private <T> T preserveJpaRelations(T originalObject, T updatedObject, Map<String, Object> dataUpdate) {
        try {
            Field[] fields = originalObject.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (dataUpdate.containsKey(field.getName())) {
                    continue;
                }

                boolean isJpaRelation = field.isAnnotationPresent(ManyToOne.class) ||
                        field.isAnnotationPresent(OneToOne.class) ||
                        field.isAnnotationPresent(OneToMany.class) ||
                        field.isAnnotationPresent(ManyToMany.class) ||
                        field.isAnnotationPresent(JoinColumn.class);

                boolean isTimestamp = field.isAnnotationPresent(CreationTimestamp.class) ||
                        field.isAnnotationPresent(UpdateTimestamp.class);

                if (isJpaRelation || isTimestamp) {
                    field.setAccessible(true);
                    Object originalValue = field.get(originalObject);
                    if (originalValue != null) {
                        field.set(updatedObject, originalValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("Warning: Could not preserve JPA relations: " + e.getMessage());
        }
        return updatedObject;
    }

    // ✅ MÉTODO GENERAL PARA CREACIÓN DE ENTIDADES
    public <T, S> T createEntityFromDto(S dto, Class<T> entityClass, Map<String, Object> context) {
        try {
            // Mapeo base del DTO a la entidad
            T entity = converterTo(dto, entityClass);

            // Aplicar lógica específica según el tipo de entidad
            applyEntitySpecificLogic(entity, context);

            return entity;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error en mapeo de entidad: " + e.getMessage());
        }
    }

    // ✅ LÓGICA ESPECÍFICA POR TIPO DE ENTIDAD
    private <T> void applyEntitySpecificLogic(T entity, Map<String, Object> context)
            throws IllegalAccessException, NoSuchFieldException {

        String entityType = entity.getClass().getSimpleName();

        switch (entityType) {
            case "Venta":
                applyVentaLogic(entity, context);
                break;
            case "Cliente":
                applyClienteLogic(entity, context);
                break;
            case "Proveedor":
                applyProveedorLogic(entity, context);
                break;
            case "Producto":
                applyProductoLogic(entity, context);
                break;
            default:
                applyDefaultEntityLogic(entity);
        }
    }

    // ✅ LÓGICA ESPECÍFICA PARA CLIENTE
    private <T> void applyClienteLogic(T entity, Map<String, Object> context)
            throws IllegalAccessException {

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Validar y formatear DNI
            if (field.getName().equals("dni") && field.get(entity) != null) {
                String dni = (String) field.get(entity);
                field.set(entity, dni.trim().toUpperCase());
            }

            // Formatear nombre y apellido
            if ((field.getName().equals("nombre") || field.getName().equals("apellido")) &&
                    field.get(entity) != null) {
                String texto = (String) field.get(entity);
                field.set(entity, capitalizarTexto(texto));
            }

            // Formatear email
            if (field.getName().equals("email") && field.get(entity) != null) {
                String email = (String) field.get(entity);
                field.set(entity, email.trim().toLowerCase());
            }

            // Establecer fecha de registro
            if (field.getName().equals("fechaRegistro") && field.get(entity) == null) {
                field.set(entity, LocalDateTime.now());
            }
        }
    }

    // ✅ LÓGICA ESPECÍFICA PARA PROVEEDOR
    private <T> void applyProveedorLogic(T entity, Map<String, Object> context)
            throws IllegalAccessException {

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Validar y formatear RUC
            if (field.getName().equals("ruc") && field.get(entity) != null) {
                String ruc = (String) field.get(entity);
                field.set(entity, ruc.trim().replaceAll("\\s+", ""));
            }

            // Formatear nombre
            if (field.getName().equals("nombre") && field.get(entity) != null) {
                String nombre = (String) field.get(entity);
                field.set(entity, capitalizarTexto(nombre));
            }

            // Formatear email
            if (field.getName().equals("email") && field.get(entity) != null) {
                String email = (String) field.get(entity);
                field.set(entity, email.trim().toLowerCase());
            }

            // Formatear contacto
            if (field.getName().equals("contacto") && field.get(entity) != null) {
                String contacto = (String) field.get(entity);
                field.set(entity, capitalizarTexto(contacto));
            }

            // Establecer fecha de registro
            if (field.getName().equals("fechaRegistro") && field.get(entity) == null) {
                field.set(entity, LocalDateTime.now());
            }
        }
    }

    // ✅ LÓGICA ESPECÍFICA PARA PRODUCTO
    private <T> void applyProductoLogic(T entity, Map<String, Object> context)
            throws IllegalAccessException {

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Formatear nombre
            if (field.getName().equals("nombre") && field.get(entity) != null) {
                String nombre = (String) field.get(entity);
                field.set(entity, capitalizarTexto(nombre));
            }

            // Formatear categoría
            if (field.getName().equals("categoria") && field.get(entity) != null) {
                String categoria = (String) field.get(entity);
                field.set(entity, capitalizarTexto(categoria));
            }

            // Validar stock mínimo
            if (field.getName().equals("stock") && field.get(entity) != null) {
                Integer stock = (Integer) field.get(entity);
                if (stock < 0) {
                    field.set(entity, 0);
                }
            }

            // Validar precio mínimo
            if (field.getName().equals("precio") && field.get(entity) != null) {
                Double precio = (Double) field.get(entity);
                if (precio < 0) {
                    field.set(entity, 0.0);
                }
            }

            // Establecer fecha de creación
            if (field.getName().equals("fechaCreacion") && field.get(entity) == null) {
                field.set(entity, LocalDateTime.now());
            }
        }
    }

    // ✅ LÓGICA ESPECÍFICA PARA VENTA (mantenida del anterior)
    private <T> void applyVentaLogic(T entity, Map<String, Object> context)
            throws IllegalAccessException, NoSuchFieldException {

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Establecer cliente
            if (field.getName().equals("cliente") && field.get(entity) == null &&
                    context.containsKey("cliente")) {
                field.set(entity, context.get("cliente"));
            }

            // Generar número de factura
            if (field.getName().equals("numeroFactura") && field.get(entity) == null) {
                field.set(entity, "FAC-" + System.currentTimeMillis() + "-" +
                        ThreadLocalRandom.current().nextInt(1000, 9999));
            }

            // Establecer fecha de venta
            if (field.getName().equals("fechaVenta") && field.get(entity) == null) {
                field.set(entity, LocalDateTime.now());
            }

            // Establecer estado
            if (field.getName().equals("estado") && field.get(entity) == null) {
                field.set(entity, getEnumValue(field.getType(), "COMPLETADA"));
            }

            // Procesar detalles de venta
            if (field.getName().equals("detalles") && field.get(entity) == null &&
                    context.containsKey("productosInfo")) {
                @SuppressWarnings("unchecked")
                List<Object> productosInfo = (List<Object>) context.get("productosInfo");
                List<Object> detalles = createDetallesVenta(productosInfo, entity);
                field.set(entity, detalles);
            }

            // Calcular total
            if (field.getName().equals("total") && field.get(entity) == null) {
                field.set(entity, calcularTotalVenta(entity));
            }
        }
    }

    // ✅ LÓGICA POR DEFECTO PARA ENTIDADES
    private <T> void applyDefaultEntityLogic(T entity) throws IllegalAccessException {
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Fechas de creación
            if ((field.getName().equals("fechaCreacion") ||
                    field.getName().equals("fechaRegistro")) &&
                    field.get(entity) == null) {
                field.set(entity, LocalDateTime.now());
            }

            // Estados por defecto
            if (field.getName().equals("estado") && field.get(entity) == null &&
                    field.getType().isEnum()) {
                field.set(entity, getEnumValue(field.getType(), "ACTIVO"));
            }
        }
    }

    // ✅ MÉTODOS AUXILIARES

    private List<Object> createDetallesVenta(List<Object> productosInfo, Object venta)
            throws IllegalAccessException {
        List<Object> detalles = new ArrayList<>();

        for (Object productoInfo : productosInfo) {
            Map<String, Object> productoMap = getObjectMapper().convertValue(productoInfo, Map.class);
            Object detalle = createDetalleVenta(productoMap, venta);
            detalles.add(detalle);
        }

        return detalles;
    }

    private Object createDetalleVenta(Map<String, Object> productoMap, Object venta) {
        Map<String, Object> detalleMap = new HashMap<>();
        detalleMap.put("cantidad", productoMap.get("cantidad"));
        detalleMap.put("precioUnitario", productoMap.get("precioUnitario"));
        detalleMap.put("subtotal", productoMap.get("subtotal"));

        // En implementación real, esto mapearía a tu clase DetalleVenta
        return getObjectMapper().convertValue(detalleMap, Object.class);
    }

    private Double calcularTotalVenta(Object venta) throws IllegalAccessException, NoSuchFieldException {
        Field detallesField = venta.getClass().getDeclaredField("detalles");
        detallesField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> detalles = (List<Object>) detallesField.get(venta);

        if (detalles == null) return 0.0;

        double total = 0.0;
        for (Object detalle : detalles) {
            Field subtotalField = detalle.getClass().getDeclaredField("subtotal");
            subtotalField.setAccessible(true);
            Double subtotal = (Double) subtotalField.get(detalle);
            total += subtotal != null ? subtotal : 0.0;
        }

        return total;
    }

    private String capitalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }

        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    private Object getEnumValue(Class<?> enumClass, String valueName) {
        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object constant : enumConstants) {
            if (constant.toString().equals(valueName)) {
                return constant;
            }
        }
        return enumConstants.length > 0 ? enumConstants[0] : null;
    }

    public static BuildObjectMapper getInstance() {
        return INSTANCE;
    }
}
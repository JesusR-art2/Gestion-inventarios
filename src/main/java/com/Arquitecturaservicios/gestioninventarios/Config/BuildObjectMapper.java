package com.Arquitecturaservicios.gestioninventarios.Config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

@Component
public class BuildObjectMapper {
    private static final BuildObjectMapper INSTANCE = new BuildObjectMapper();

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper()
    {
        if (objectMapper == null)
        {
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
    public static <T> T converterTo(Object reference, Class<T> toConverter)
    {
        Map<String, Object> baseMap = getObjectMapper().convertValue(reference, Map.class);
        return getObjectMapper().convertValue(baseMap, toConverter);
    }

    public <T> T getObjectForUpdate(Object baseObject, Map<String, Object> dataUpdate)
    {
        Map<String, Object> baseMap = getObjectMapper().convertValue(baseObject, Map.class);
        ArrayList<String> keysList = new ArrayList<>(baseMap.keySet());
        keysList.addAll(dataUpdate.keySet());

        // Only update fields that are present in dataUpdate and are not null
        for (String keyBase : new HashSet<>(keysList))
        {
            if (dataUpdate.containsKey(keyBase) && dataUpdate.get(keyBase) != null)
            {
                baseMap.put(keyBase, dataUpdate.get(keyBase));
            }
        }

        // Convert back to object
        T updatedObject = (T) getObjectMapper().convertValue(baseMap, baseObject.getClass());

        // Preserve JPA relations that might have been lost during conversion
        return preserveJpaRelations(baseObject, updatedObject, dataUpdate);
    }
    private <T> T preserveJpaRelations(Object originalObject, T updatedObject, Map<String, Object> dataUpdate)
    {
        try {
            // Get all fields from the original object
            java.lang.reflect.Field[] fields = originalObject.getClass().getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                // Skip if this field was explicitly updated in dataUpdate
                if (dataUpdate.containsKey(field.getName())) {
                    continue;
                }

                // Check if field has JPA annotations that indicate it's a relation
                boolean isJpaRelation = field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) ||
                        field.isAnnotationPresent(jakarta.persistence.OneToOne.class) ||
                        field.isAnnotationPresent(jakarta.persistence.OneToMany.class) ||
                        field.isAnnotationPresent(jakarta.persistence.ManyToMany.class);

                // Check if field has timestamp annotations
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
            // Log warning but don't fail the operation
            System.err.println("Warning: Could not preserve JPA relations: " + e.getMessage());
        }

        return updatedObject;
    }

    public static BuildObjectMapper getInstance() {
        return INSTANCE;
    }

}

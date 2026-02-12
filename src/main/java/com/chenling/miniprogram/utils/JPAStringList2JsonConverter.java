package com.chenling.miniprogram.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JPAStringList2JsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> LIST_TYPE = new TypeReference<List<String>>() {};

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        try {
            if (strings == null) return "[]";
            return MAPPER.writeValueAsString(strings);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert List<String> to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return new ArrayList<>();
            return MAPPER.readValue(dbData, LIST_TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON to List<String>", e);
        }
    }
}

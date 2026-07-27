package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.PrescriptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA converter for prescription JSON column.
 * Handles bad data where column was written as {} (empty object) instead of [] (empty array).
 */
@Converter
public class PrescriptionJsonConverter implements AttributeConverter<List<PrescriptionItem>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(List<PrescriptionItem> prescription) {
        if (prescription == null || prescription.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(prescription);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PrescriptionItem> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
        String trimmed = dbData.trim();
        // Handle corrupted data: {} stored instead of [] or null
        if (trimmed.equals("{}") || trimmed.equals("{ }")) return new ArrayList<>();
        try {
            return MAPPER.readValue(trimmed, new TypeReference<List<PrescriptionItem>>() {});
        } catch (Exception e) {
            // Defensive: bad JSON → return empty list instead of crashing
            return new ArrayList<>();
        }
    }
}

package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles bad data in DB where prescription column was stored as {} (empty object)
 * instead of [] (empty array) or NULL.
 * Returns empty list for any non-array token to prevent deserialization crash.
 */
public class PrescriptionListDeserializer extends StdDeserializer<List<PrescriptionItem>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PrescriptionListDeserializer() {
        super(List.class);
    }

    @Override
    public List<PrescriptionItem> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Handle {} (empty object) or any non-array value → return empty list
        if (p.currentToken() != JsonToken.START_ARRAY) {
            p.skipChildren();
            return new ArrayList<>();
        }
        return MAPPER.readValue(p, new TypeReference<List<PrescriptionItem>>() {});
    }

    @Override
    public List<PrescriptionItem> getNullValue(DeserializationContext ctxt) {
        return new ArrayList<>();
    }
}

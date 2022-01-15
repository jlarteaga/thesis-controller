package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class TextCreatedEventPayload {

    public final static String PATTERN = "text-created";

    private String uuid;
    private String type;

    public static TextCreatedEventPayload fromUnparsedPayload(Map<String, Object> unparsedPayload) {
        if (Objects.isNull(unparsedPayload) ||
                Objects.isNull(unparsedPayload.get("uuid")) ||
                Objects.isNull(unparsedPayload.get("type")) ||
                !(unparsedPayload.get("uuid") instanceof String) ||
                !(unparsedPayload.get("type") instanceof String)
        ) {
            throw new IllegalArgumentException("Payload invalid for pattern " + PATTERN);
        }
        return TextCreatedEventPayload.builder()
                .uuid((String) unparsedPayload.get("uuid"))
                .type((String) unparsedPayload.get("type"))
                .build();
    }
}
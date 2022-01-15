package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class TextPatchedEventPayload {

    public final static String PATTERN = "text-patched";

    private String uuid;
    private String type;
    private Set<String> keys;

    public static TextPatchedEventPayload fromUnparsedPayload(Map<String, Object> unparsedPayload) {
        if (Objects.isNull(unparsedPayload) ||
                Objects.isNull(unparsedPayload.get("uuid")) ||
                Objects.isNull(unparsedPayload.get("type")) ||
                !(unparsedPayload.get("uuid") instanceof String) ||
                !(unparsedPayload.get("type") instanceof String) ||
                Objects.isNull(unparsedPayload.get("keys")) ||
                !(unparsedPayload.get("keys") instanceof Collection)
        ) {
            throw new IllegalArgumentException("Payload invalid for pattern " + PATTERN);
        }
        return TextPatchedEventPayload.builder()
                .uuid((String) unparsedPayload.get("uuid"))
                .type((String) unparsedPayload.get("type"))
                .keys(new HashSet<>((Collection<String>) unparsedPayload.get("keys")))
                .build();
    }
}
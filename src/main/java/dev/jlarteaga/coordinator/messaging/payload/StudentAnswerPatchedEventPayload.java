package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class StudentAnswerPatchedEventPayload {

    public final static String PATTERN = "student-answer-patched";

    private String uuid;
    private Set<String> keys;

    public static StudentAnswerPatchedEventPayload fromUnparsedPayload(Map<String, Object> unparsedPayload) {
        if (Objects.isNull(unparsedPayload) ||
                Objects.isNull(unparsedPayload.get("uuid")) ||
                !(unparsedPayload.get("uuid") instanceof String) ||
                Objects.isNull(unparsedPayload.get("keys")) ||
                !(unparsedPayload.get("keys") instanceof Collection)
        ) {
            throw new IllegalArgumentException("Payload invalid for pattern " + PATTERN);
        }
        return StudentAnswerPatchedEventPayload.builder()
                .uuid((String) unparsedPayload.get("uuid"))
                .keys(new HashSet<>((Collection<String>) unparsedPayload.get("keys")))
                .build();
    }
}
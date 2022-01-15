package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Data;

@Data
public class ProcessTextResponsePayload {
    private String textUuid;
    private String processedText;
}

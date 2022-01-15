package dev.jlarteaga.coordinator.messaging.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessTextRequestPayload {

    private String textUuid;
    private String text;

}

package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Data;

import java.util.Map;

@Data
public class DatasetEvent {
    private String pattern;
    private Map<String, Object> data;
}

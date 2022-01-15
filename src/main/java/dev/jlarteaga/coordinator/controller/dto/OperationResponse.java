package dev.jlarteaga.coordinator.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationResponse {
    private Boolean success;
    private String message;
}

package dev.jlarteaga.coordinator.utils;

import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextDetailed;

import java.util.Objects;

public class ModelValidator {

    public static boolean hasNotProcessedText(GetTextDetailed text) {
        return Objects.isNull(text) ||
                Objects.isNull(text.getProcessed()) ||
                Objects.isNull(text.getProcessingStatus()) ||
                !TextProcessingStatus.Processed.getText().equals(text.getProcessingStatus());
    }
}

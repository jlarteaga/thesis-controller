package dev.jlarteaga.coordinator.utils;

import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.webclient.dto.text.GetText;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextDetailed;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public class ModelValidator {

    public static boolean hasNotProcessedText(GetTextDetailed text) {
        return Objects.isNull(text) ||
                Objects.isNull(text.getProcessed()) ||
                Objects.isNull(text.getProcessingStatus()) ||
                !TextProcessingStatus.Processed.getText().equals(text.getProcessingStatus());
    }

    public static boolean hasValidTranslationText(GetText text) {
        return Strings.isNotBlank(text.getSent()) &&
                text.getStatus().startsWith("tr-") &&
                !"tr-auto".equals(text.getStatus());
    }
}

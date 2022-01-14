package dev.jlarteaga.coordinator.webclient.dto.text;

import lombok.Data;

@Data
public class GetTextSummarizedDTO implements GetTextSummarized {
    private String uuid;
    private String lang;
    private String raw;
    private String sent;
    private String processingStatus;
    private String status;
}

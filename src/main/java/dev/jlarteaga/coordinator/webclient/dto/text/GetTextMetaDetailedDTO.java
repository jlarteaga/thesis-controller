package dev.jlarteaga.coordinator.webclient.dto.text;

import lombok.Data;

@Data
public class GetTextMetaDetailedDTO implements GetTextMetaDetailed {
    private String uuid;
    private String lang;
    private String raw;
    private String sent;
    private String processed;
    private String processingStatus;
    private String status;
    private String parent;
    private String parentType;
}

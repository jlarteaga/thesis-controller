package dev.jlarteaga.coordinator.webclient.dto.question;

import dev.jlarteaga.coordinator.webclient.dto.text.GetTextSummarizedDTO;
import lombok.Data;

@Data
public class GetQuestionSummarizedDTO implements GetQuestionSummarized {
    private String uuid;
    private String rawStatement;
    private String sentStatement;
    private String lang;
    private String label;
    private GetTextSummarizedDTO answer;
}

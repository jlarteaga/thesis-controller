package dev.jlarteaga.coordinator.webclient.dto.question;

import dev.jlarteaga.coordinator.webclient.dto.text.GetTextDetailedDTO;
import lombok.Data;

@Data
public class GetQuestionDetailedDTO implements GetQuestionDetailed {
    private String uuid;
    private String rawStatement;
    private String sentStatement;
    private String lang;
    private String label;
    private GetTextDetailedDTO answer;
}

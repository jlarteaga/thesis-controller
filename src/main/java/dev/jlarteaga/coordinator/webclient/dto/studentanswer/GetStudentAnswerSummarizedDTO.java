package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import dev.jlarteaga.coordinator.webclient.dto.text.GetTextSummarizedDTO;
import lombok.Data;

import java.util.Map;

@Data
public class GetStudentAnswerSummarizedDTO implements GetStudentAnswerSummarized {
    private String uuid;
    private Double grade;
    private Map<String, Double> grades;
    private Integer student;
    private GetTextSummarizedDTO text;
}

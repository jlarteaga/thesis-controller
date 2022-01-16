package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import dev.jlarteaga.coordinator.webclient.dto.question.GetQuestionDetailedDTO;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextDetailedDTO;
import lombok.Data;

import java.util.Map;

@Data
public class GetStudentAnswerSingleDetailedDTO implements GetStudentAnswerSingleDetailed<GetTextDetailedDTO> {
    private String uuid;
    private Double grade;
    private Map<String, Double> grades;
    private Integer student;
    private GetTextDetailedDTO text;
    private GetQuestionDetailedDTO question;
    private SimilarityMatricesDTO similarityMatrices;
}

package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import dev.jlarteaga.coordinator.webclient.dto.text.GetTextSummarizedDTO;

import java.util.Map;

public interface GetStudentAnswerSummarized {
    String getUuid();

    void setUuid(String uuid);

    Double getGrade();

    void setGrade(Double grade);

    Map<String, Double> getGrades();

    void setGrades(Map<String, Double> grades);

    Integer getStudent();

    void setStudent(Integer student);

    GetTextSummarizedDTO getText();

    void setText(GetTextSummarizedDTO text);
}

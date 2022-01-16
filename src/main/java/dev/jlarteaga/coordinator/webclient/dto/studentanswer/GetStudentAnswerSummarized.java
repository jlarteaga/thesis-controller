package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import java.util.Map;

public interface GetStudentAnswerSummarized<T> {
    String getUuid();

    void setUuid(String uuid);

    Double getGrade();

    void setGrade(Double grade);

    Map<String, Double> getGrades();

    void setGrades(Map<String, Double> grades);

    Integer getStudent();

    void setStudent(Integer student);

    T getText();

    void setText(T text);
}

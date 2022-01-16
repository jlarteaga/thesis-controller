package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import dev.jlarteaga.coordinator.webclient.dto.question.GetQuestionDetailedDTO;

public interface GetStudentAnswerSingleDetailed<T> extends GetStudentAnswerSummarized<T> {

    GetQuestionDetailedDTO getQuestion();

    void setQuestion(GetQuestionDetailedDTO question);

    SimilarityMatricesDTO getSimilarityMatrices();

    void setSimilarityMatrices(SimilarityMatricesDTO similarityMatrices);
}

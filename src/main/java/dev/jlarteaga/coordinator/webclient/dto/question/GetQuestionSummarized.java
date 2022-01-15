package dev.jlarteaga.coordinator.webclient.dto.question;

import dev.jlarteaga.coordinator.webclient.dto.text.GetTextSummarizedDTO;

public interface GetQuestionSummarized {

    String getUuid();

    void setUuid(String uuid);

    String getRawStatement();

    void setRawStatement(String rawStatement);

    String getSentStatement();

    void setSentStatement(String sentStatement);

    String getLang();

    void setLang(String lang);

    String getLabel();

    void setLabel(String label);

    GetTextSummarizedDTO getAnswer();

    void setAnswer(GetTextSummarizedDTO answer);
}

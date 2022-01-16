package dev.jlarteaga.coordinator.webclient.dto.question;

public interface GetQuestion<TextType> {

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

    TextType getAnswer();

    void setAnswer(TextType answer);
}

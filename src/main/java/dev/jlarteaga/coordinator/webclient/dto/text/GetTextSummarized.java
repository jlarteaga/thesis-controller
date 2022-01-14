package dev.jlarteaga.coordinator.webclient.dto.text;

public interface GetTextSummarized {

    String getUuid();

    void setUuid(String uuid);

    String getLang();

    void setLang(String lang);

    String getRaw();

    void setRaw(String raw);

    String getSent();

    void setSent(String sent);

    String getProcessingStatus();

    void setProcessingStatus(String processingStatus);

    String getStatus();

    void setStatus(String status);

}

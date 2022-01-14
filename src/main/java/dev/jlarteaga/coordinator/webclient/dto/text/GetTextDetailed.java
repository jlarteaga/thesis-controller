package dev.jlarteaga.coordinator.webclient.dto.text;

public interface GetTextDetailed extends GetTextSummarized {

    String getProcessed();

    void setProcessed(String processed);
}

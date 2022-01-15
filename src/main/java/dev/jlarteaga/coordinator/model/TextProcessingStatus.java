package dev.jlarteaga.coordinator.model;

public enum TextProcessingStatus {

    NotProcessed("not-proc"),
    Processing("processing"),
    Processed("processed");

    private final String text;

    TextProcessingStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

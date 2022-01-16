package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Data;

import java.util.List;

@Data
public class SimilarityMatrixRequestPayload {
    private String text1Uuid;
    private String text2Uuid;
    private List<String> synsets1;
    private List<String> synsets2;
}

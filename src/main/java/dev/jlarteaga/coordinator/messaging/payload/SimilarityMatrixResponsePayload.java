package dev.jlarteaga.coordinator.messaging.payload;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SimilarityMatrixResponsePayload {
    private String text1Uuid;
    private String text2Uuid;
    private List<String> synsets1;
    private List<String> synsets2;
    private Map<String, double[][]> similarities;
}

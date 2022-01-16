package dev.jlarteaga.coordinator.webclient.dto.studentanswer;

import lombok.Data;

import java.util.List;

@Data
public class SimilarityMatricesDTO {

    private List<String> synsets1;
    private List<String> synsets2;
    private double[][] hso;
    private double[][] lch;
    private double[][] lesk;
    private double[][] wup;
    private double[][] res;
    private double[][] path;
    private double[][] jcn;
    private double[][] lin;

}

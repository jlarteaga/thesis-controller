package dev.jlarteaga.coordinator.model;

import lombok.Getter;

public enum RelatednessAlgorithm {
    HirstStOnge("hso"),
    LeacockChodorow("lch"),
    Lesk("lesk"),
    WuPalmer("wup"),
    Resnik("res"),
    Path("path"),
    JiangConrath("jcn"),
    Lin("lin");

    @Getter
    private final String text;

    RelatednessAlgorithm(String text) {
        this.text = text;
    }

}

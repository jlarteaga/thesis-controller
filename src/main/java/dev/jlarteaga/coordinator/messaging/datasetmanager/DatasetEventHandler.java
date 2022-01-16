package dev.jlarteaga.coordinator.messaging.datasetmanager;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class DatasetEventHandler {

    private final Set<String> acceptedPatterns;

    protected DatasetEventHandler(Set<String> acceptedPatterns) {
        this.acceptedPatterns = Collections.unmodifiableSet(acceptedPatterns);
    }

    final public boolean canHandlePattern(String pattern) {
        return this.acceptedPatterns.contains(pattern);
    }

    public abstract Mono<OperationResponse> dispatch(String pattern, Map<String, Object> unparsedPayload);
}

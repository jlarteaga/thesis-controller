package dev.jlarteaga.coordinator.messaging.datasetmanager;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.payload.StudentAnswerPatchedEventPayload;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Service
public class QuestionEventHandler extends DatasetEventHandler {

    protected QuestionEventHandler() {
        super(Set.of(StudentAnswerPatchedEventPayload.PATTERN));
    }

    @Override
    public Mono<OperationResponse> dispatch(String pattern, Map<String, Object> unparsedPayload) {
        return Mono.just(new OperationResponse(
                true,
                "Nothing to do here"
        ));
    }
}

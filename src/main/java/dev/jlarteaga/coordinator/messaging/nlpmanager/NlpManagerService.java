package dev.jlarteaga.coordinator.messaging.nlpmanager;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.payload.ProcessTextRequestPayload;
import dev.jlarteaga.coordinator.messaging.payload.SimilarityMatrixRequestPayload;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Set;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.REQUEST_KEY;

@Service
public class NlpManagerService {

    private final RabbitTemplate nlpRabbitTemplate;
    private final DirectExchange processTextExchange;
    private final DirectExchange matrixSimilarityExchange;

    public NlpManagerService(
            RabbitTemplate nlpRabbitTemplate,
            @Qualifier("processTextExchange") DirectExchange processTextExchange,
            @Qualifier("similarityMatrixExchange") DirectExchange matrixSimilarityExchange) {
        this.nlpRabbitTemplate = nlpRabbitTemplate;
        this.processTextExchange = processTextExchange;
        this.matrixSimilarityExchange = matrixSimilarityExchange;
    }

    public Mono<OperationResponse> sendProcessTextRequest(String uuid, String text) {
        return Mono.create(sink -> {
            try {
                nlpRabbitTemplate.convertAndSend(processTextExchange.getName(), REQUEST_KEY, new ProcessTextRequestPayload(uuid, text));
                sink.success(new OperationResponse(
                        true,
                        "The text was sent to the processing queue"
                ));
            } catch (Exception e) {
                sink.success(new OperationResponse(
                        false,
                        e.getMessage()
                ));
            }
        });
    }

    public Mono<OperationResponse> sendMatrixSimilarityRequest(
            String textUuid1,
            String textUuid2,
            Set<String> synsets1,
            Set<String> synsets2
    ) {
        return Mono.create(sink -> {
            SimilarityMatrixRequestPayload payload = new SimilarityMatrixRequestPayload();
            payload.setText1Uuid(textUuid1);
            payload.setText2Uuid(textUuid2);
            payload.setSynsets1(new ArrayList<>(synsets1));
            payload.setSynsets2(new ArrayList<>(synsets2));
            try {
                nlpRabbitTemplate.convertAndSend(matrixSimilarityExchange.getName(), REQUEST_KEY, payload);
                sink.success(new OperationResponse(true, "Matrix similarity request sent"));
            } catch (Exception e) {
                sink.success(new OperationResponse(false, e.getMessage()));
            }
        });
    }
}

package dev.jlarteaga.coordinator.messaging.nlpmanager;

import dev.jlarteaga.coordinator.messaging.payload.ProcessTextRequestPayload;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.REQUEST_KEY;

@Service
public class NlpManagerService {

    private final RabbitTemplate nlpRabbitTemplate;
    private final DirectExchange processTextExchange;

    public NlpManagerService(
            RabbitTemplate nlpRabbitTemplate,
            @Qualifier("processTextExchange") DirectExchange processTextExchange
    ) {
        this.nlpRabbitTemplate = nlpRabbitTemplate;
        this.processTextExchange = processTextExchange;
    }

    public Mono<Boolean> sendProcessTextRequest(String uuid, String text) {
        return Mono.create(sink -> {
            try {
                nlpRabbitTemplate.convertAndSend(processTextExchange.getName(), REQUEST_KEY, new ProcessTextRequestPayload(uuid, text));
                sink.success(true);
            } catch (Exception e) {
                sink.success(false);
            }
        });
    }
}

package dev.jlarteaga.coordinator.messaging.datasetmanager;

import com.rabbitmq.client.Channel;
import dev.jlarteaga.coordinator.messaging.nlpmanager.NlpManagerService;
import dev.jlarteaga.coordinator.messaging.payload.ProcessTextResponsePayload;
import dev.jlarteaga.coordinator.messaging.payload.TextCreatedEventPayload;
import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.PROCESS_TEXT_RESPONSES_QUEUE;

@Component
public class TextEventHandler {

    public static final Set<String> PATTERNS = Set.of(
            TextCreatedEventPayload.PATTERN,
            "text-patched");
    private final NlpManagerService nlpManagerService;
    private final DatasetManagerService datasetManagerService;
    Logger logger = LoggerFactory.getLogger(TextEventHandler.class);

    public TextEventHandler(
            NlpManagerService nlpManagerService,
            DatasetManagerService datasetManagerService
    ) {
        this.nlpManagerService = nlpManagerService;
        this.datasetManagerService = datasetManagerService;
    }

    public Mono<Boolean> dispatch(String pattern, Map<String, Object> unparsedPayload) {
        try {
            if ("text-created".equals(pattern)) {
                return this.processTextCreatedEvent(TextCreatedEventPayload.fromUnparsedPayload(unparsedPayload));
//            } else if ("text-patched".equals(pattern)) {
//                return this.processTextPatchedEvent(payload);
            } else {
                throw new IllegalArgumentException("Cannot process pattern " + pattern);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Mono.just(false);
        }
    }

//    private Mono<Boolean> processTextPatchedEvent(TextPatchedEventPayload payload) {
//    }

    private Mono<Boolean> processTextCreatedEvent(TextCreatedEventPayload payload) {
        return this.datasetManagerService.getText(payload.getUuid())
                .flatMap(text -> this.datasetManagerService.updateTextProcessingStatus(text.getUuid(), TextProcessingStatus.Processing, true)
                        .flatMap(response -> this.nlpManagerService.sendProcessTextRequest(text.getUuid(), text.getSent()))
                )
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just(false);
                });
    }

    @RabbitListener(
            queues = PROCESS_TEXT_RESPONSES_QUEUE,
            ackMode = "MANUAL"
    )
    private void processedTextHandler(
            ProcessTextResponsePayload payload,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        this.datasetManagerService.updateProcessedText(payload.getTextUuid(), payload.getProcessedText(), true)
                .doOnError(error -> {
                    try {
                        channel.basicNack(tag, false, false);
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                    }
                    logger.error("Could not handle process-text response", error);
                })
                .subscribe(response -> {
                    try {
                        channel.basicAck(tag, false);
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                    }
                });

    }
}

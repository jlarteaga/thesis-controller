package dev.jlarteaga.coordinator.messaging.datasetmanager;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.nlpmanager.NlpManagerService;
import dev.jlarteaga.coordinator.messaging.payload.TextCreatedEventPayload;
import dev.jlarteaga.coordinator.messaging.payload.TextPatchedEventPayload;
import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.utils.ModelValidator;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextMetaDetailedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
public class TextEventHandler extends DatasetEventHandler {

    private final NlpManagerService nlpManagerService;
    private final DatasetManagerService datasetManagerService;
    Logger logger = LoggerFactory.getLogger(TextEventHandler.class);

    public TextEventHandler(
            NlpManagerService nlpManagerService,
            DatasetManagerService datasetManagerService
    ) {
        super(Set.of(TextCreatedEventPayload.PATTERN, TextPatchedEventPayload.PATTERN));
        this.nlpManagerService = nlpManagerService;
        this.datasetManagerService = datasetManagerService;
    }

    @Override
    public Mono<OperationResponse> dispatch(String pattern, Map<String, Object> unparsedPayload) {
        try {
            if (TextCreatedEventPayload.PATTERN.equals(pattern)) {
                return this.processTextCreatedEvent(TextCreatedEventPayload.fromUnparsedPayload(unparsedPayload));
            } else if (TextPatchedEventPayload.PATTERN.equals(pattern)) {
                return this.processTextPatchedEvent(TextPatchedEventPayload.fromUnparsedPayload(unparsedPayload));
            } else {
                throw new IllegalArgumentException("Cannot process pattern " + pattern);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Mono.just(new OperationResponse(
                    false,
                    e.getMessage()
            ));
        }
    }

    private Mono<OperationResponse> processTextPatchedEvent(TextPatchedEventPayload payload) {
        return this.datasetManagerService.getText(payload.getUuid())
                .flatMap(text -> {
                    if (payload.getKeys().contains("sent") ||
                            ("not-proc".equals(text.getProcessingStatus()) &&
                                    ModelValidator.hasValidTranslationText(text))
                    ) {
                        return this.startProcessingText(text);
                    } else {
                        return Mono.just(new OperationResponse(
                                true,
                                "There is nothing to do with this event"
                        ));
                    }
                });
    }

    private Mono<OperationResponse> processTextCreatedEvent(TextCreatedEventPayload payload) {
        return this.datasetManagerService.getText(payload.getUuid())
                .flatMap(this::startProcessingText);
    }

    public Mono<OperationResponse> startProcessingText(GetTextMetaDetailedDTO text) {
        return this.datasetManagerService.updateTextProcessingStatus(text.getUuid(), TextProcessingStatus.Processing, true)
                .flatMap(response -> this.nlpManagerService.sendProcessTextRequest(text.getUuid(), text.getSent()));
    }

}

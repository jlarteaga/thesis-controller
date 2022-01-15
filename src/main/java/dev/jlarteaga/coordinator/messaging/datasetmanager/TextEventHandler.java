package dev.jlarteaga.coordinator.messaging.datasetmanager;

import dev.jlarteaga.coordinator.messaging.nlpmanager.NlpManagerService;
import dev.jlarteaga.coordinator.messaging.payload.TextCreatedEventPayload;
import dev.jlarteaga.coordinator.messaging.payload.TextPatchedEventPayload;
import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextMetaDetailedDTO;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
public class TextEventHandler {

    public static final Set<String> PATTERNS = Set.of(
            TextCreatedEventPayload.PATTERN,
            TextPatchedEventPayload.PATTERN);
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
            if (TextCreatedEventPayload.PATTERN.equals(pattern)) {
                return this.processTextCreatedEvent(TextCreatedEventPayload.fromUnparsedPayload(unparsedPayload));
            } else if (TextPatchedEventPayload.PATTERN.equals(pattern)) {
                return this.processTextPatchedEvent(TextPatchedEventPayload.fromUnparsedPayload(unparsedPayload));
            } else {
                throw new IllegalArgumentException("Cannot process pattern " + pattern);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Mono.just(false);
        }
    }

    private Mono<Boolean> processTextPatchedEvent(TextPatchedEventPayload payload) {
        return this.datasetManagerService.getText(payload.getUuid())
                .flatMap(text -> {
                    if (payload.getKeys().contains("sent") ||
                            ("not-proc".equals(text.getProcessingStatus()) &&
                                    this.hasValidText(text))
                    ) {
                        return this.startProcessingText(text);
                    } else {
                        return Mono.just(true);
                    }
                });
    }

    public boolean hasValidText(GetTextMetaDetailedDTO text) {
        return Strings.isNotBlank(text.getSent()) &&
                text.getStatus().startsWith("tr-") &&
                !"tr-auto".equals(text.getStatus());
    }

    private Mono<Boolean> processTextCreatedEvent(TextCreatedEventPayload payload) {
        return this.datasetManagerService.getText(payload.getUuid())
                .flatMap(this::startProcessingText);
    }

    public Mono<Boolean> startProcessingText(GetTextMetaDetailedDTO text) {
        return this.datasetManagerService.updateTextProcessingStatus(text.getUuid(), TextProcessingStatus.Processing, true)
                .flatMap(response -> this.nlpManagerService.sendProcessTextRequest(text.getUuid(), text.getSent()));
    }

}

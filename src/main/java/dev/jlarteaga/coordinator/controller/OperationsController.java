package dev.jlarteaga.coordinator.controller;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.datasetmanager.TextEventHandler;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/operations")
public class OperationsController {

    private final DatasetManagerService datasetManagerService;
    private final TextEventHandler textEventHandler;

    public OperationsController(DatasetManagerService datasetManagerService, TextEventHandler textEventHandler) {
        this.datasetManagerService = datasetManagerService;
        this.textEventHandler = textEventHandler;
    }

    @PostMapping("/process-text/texts/{uuid}")
    public Mono<OperationResponse> processTextById(
            @PathVariable("uuid") String uuid,
            @Header("x-silent") String silentHeader
    ) {
        boolean silent = Objects.isNull(silentHeader) || Strings.isNotEmpty(silentHeader);
        return this.datasetManagerService.getText(uuid)
                .flatMap(text -> {
                    if (this.textEventHandler.hasValidText(text)) {
                        return this.textEventHandler.startProcessingText(text);
                    } else {
                        return Mono.error(new IllegalArgumentException("The text is not valid for processing"));
                    }
                })
                .map(success -> new OperationResponse(true, "The text is being processed"))
                .onErrorResume(error -> Mono.just(new OperationResponse(false, error.toString())));
    }
}

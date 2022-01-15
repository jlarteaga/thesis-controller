package dev.jlarteaga.coordinator.controller;

import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.datasetmanager.TextEventHandler;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.LinkedList;
import java.util.List;

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

    @PostMapping("/process-text/questions/{uuid}")
    public Mono<OperationResponse> processQuestionTextById(
            @PathVariable("uuid") String uuid,
            @Header("x-silent") String silentHeader
    ) {
        return this.datasetManagerService.getTextByQuestion(uuid)
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

    @PostMapping("/process-text/student-answers/{uuid}")
    public Mono<OperationResponse> processStudentAnswerTextById(
            @PathVariable("uuid") String uuid,
            @Header("x-silent") String silentHeader
    ) {
        return this.datasetManagerService.getTextByStudentAnswer(uuid)
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

    @PostMapping("/process-text/questions/{uuid}/student-answers")
    public Mono<OperationResponse> processStudentAnswerTextById(
            @PathVariable("uuid") String uuid
    ) {
        return this.datasetManagerService.getStudentAnswersByQuestion(uuid)
                .filter(studentAnswer -> this.textEventHandler.hasValidText(studentAnswer.getText()))
                .flatMap(studentAnswer -> this.datasetManagerService.getTextByStudentAnswer(studentAnswer.getUuid())
                        .flatMap(this.textEventHandler::startProcessingText)
                        .map(result -> Tuples.of(studentAnswer.getUuid(), result))
                )
                .collectList()
                .map(tuples -> {
                    List<String> success = new LinkedList<>();
                    List<String> error = new LinkedList<>();
                    tuples.forEach(tuple -> {
                        if (tuple.getT2()) {
                            success.add(tuple.getT1());
                        } else {
                            error.add(tuple.getT1());
                        }
                    });
                    return new OperationResponse(
                            error.isEmpty(),
                            error.isEmpty()
                                    ? "Processed: [" + String.join(",", success) + "]"
                                    : "Failed: [" + String.join(",", error) + "]"
                    );
                });
    }
}

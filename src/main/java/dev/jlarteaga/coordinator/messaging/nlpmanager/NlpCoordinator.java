package dev.jlarteaga.coordinator.messaging.nlpmanager;

import com.rabbitmq.client.Channel;
import dev.jlarteaga.coordinator.controller.dto.OperationResponse;
import dev.jlarteaga.coordinator.messaging.payload.ProcessTextResponsePayload;
import dev.jlarteaga.coordinator.messaging.payload.SimilarityMatrixResponsePayload;
import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.utils.ModelValidator;
import dev.jlarteaga.coordinator.utils.freeling.SynsetExtractor;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextDetailedDTO;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextMetaDetailedDTO;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.PROCESS_TEXT_RESPONSES_QUEUE;
import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.SIMILARITY_MATRIX_RESPONSES_QUEUE;

@Component
public class NlpCoordinator {

    private final Logger logger = LoggerFactory.getLogger(NlpCoordinator.class);

    private final DatasetManagerService datasetManagerService;
    private final NlpManagerService nlpManagerService;

    public NlpCoordinator(DatasetManagerService datasetManagerService, NlpManagerService nlpManagerService) {
        this.datasetManagerService = datasetManagerService;
        this.nlpManagerService = nlpManagerService;
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
                .then(this.processSimilarityMatrixByText(payload.getTextUuid()))
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


    @RabbitListener(
            queues = SIMILARITY_MATRIX_RESPONSES_QUEUE,
            ackMode = "MANUAL"
    )
    private void similarityMatrixResponseHandler(
            SimilarityMatrixResponsePayload payload,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        this.datasetManagerService.updateSimilarityMatrices(
                        payload.getText2Uuid(),
                        payload.getSynsets1(),
                        payload.getSynsets2(),
                        payload.getSimilarities(),
                        false
                ).doOnError(error -> {
                    try {
                        channel.basicNack(tag, false, false);
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                    }
                    logger.error("Could not handle similarity-matrix response", error);
                })
                .subscribe(response -> {
                    try {
                        channel.basicAck(tag, false);
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                    }
                });

    }

    public Mono<OperationResponse> processSimilarityMatrixByText(String textUuid) {
        return this.datasetManagerService.getText(textUuid)
                .flatMap(text -> {
                    if (!TextProcessingStatus.Processed.getText().equals(text.getProcessingStatus())) {
                        return Mono.just(new OperationResponse(
                                false,
                                "Cannot process text[" + textUuid + "] because its text is not processed yet."
                        ));
                    }
                    if ("student-answer".equals(text.getParentType())) {
                        return this.processSimilarityMatrixForStudentAnswerText(text);
                    } else {
                        return this.processSimilarityMatrixForQuestionText(text);
                    }
                });
    }

    private Mono<OperationResponse> processSimilarityMatrixForQuestionText(GetTextMetaDetailedDTO text) {
        if (!ModelValidator.hasProcessedText(text)) {
            return Mono.just(new OperationResponse(
                    false,
                    "The reference answer has no processed text yet."
            ));
        }
        return this.datasetManagerService.getStudentAnswersByQuestion(text.getParent())
                .filter(studentAnswer -> ModelValidator.hasProcessedText(studentAnswer.getText()))
                .flatMap(studentAnswer -> this.processSimilarityMatrixByText(studentAnswer.getText().getUuid())
                        .map(operationResponse -> Tuples.of(operationResponse.getSuccess(), studentAnswer.getUuid())))
                .collectList()
                .map(tuples -> {
                    List<String> success = new LinkedList<>();
                    List<String> error = new LinkedList<>();
                    tuples.forEach(tuple -> {
                        if (tuple.getT1()) {
                            success.add(tuple.getT2());
                        } else {
                            error.add(tuple.getT2());
                        }
                    });
                    return new OperationResponse(
                            error.isEmpty(),
                            error.isEmpty()
                                    ? "Successfully sent requests for the student answers: [" + Strings.join(success.iterator(), ',') + "]"
                                    : "The following student answers could not be processed: [" + Strings.join(success.iterator(), ',') + "]"
                    );
                });
    }

    private Mono<OperationResponse> processSimilarityMatrixForStudentAnswerText(GetTextMetaDetailedDTO text) {
        return this.datasetManagerService.getStudentAnswer(text.getParent())
                .flatMap(studentAnswer -> {
                    if (!ModelValidator.hasProcessedText(studentAnswer.getText())) {
                        return Mono.just(new OperationResponse(
                                false,
                                "Could not process StudentAnswer[" + studentAnswer.getUuid() + "] because its text is not processed yet."
                        ));
                    }
                    if (Objects.isNull(studentAnswer.getQuestion())) {
                        return Mono.just(new OperationResponse(
                                false,
                                "Could not process StudentAnswer[" + studentAnswer.getUuid() + "] because it has no question (?)"
                        ));
                    }
                    if (!ModelValidator.hasProcessedText(studentAnswer.getQuestion().getAnswer())) {
                        return Mono.just(new OperationResponse(
                                false,
                                "Could not process StudentAnswer[" +
                                        studentAnswer.getUuid() +
                                        "] because its Question[" +
                                        studentAnswer.getQuestion().getUuid() +
                                        "] is not processed yet."
                        ));
                    }
                    GetTextDetailedDTO teacherAnswerText = studentAnswer.getQuestion().getAnswer();
                    GetTextDetailedDTO studentAnswerText = studentAnswer.getText();
                    return this.datasetManagerService.updateSimilarityMatricesStatus(studentAnswer.getUuid(), TextProcessingStatus.Processing, true)
                            .then(Mono.zip(
                                    SynsetExtractor.extractFromString(teacherAnswerText.getProcessed()),
                                    SynsetExtractor.extractFromString(studentAnswerText.getProcessed())
                            ).flatMap(tuple -> this.nlpManagerService.sendMatrixSimilarityRequest(
                                    studentAnswer.getQuestion().getUuid(),
                                    studentAnswer.getUuid(),
                                    tuple.getT1(),
                                    tuple.getT2()
                            )));
                });
    }
}

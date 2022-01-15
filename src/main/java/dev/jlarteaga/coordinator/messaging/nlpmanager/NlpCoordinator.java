package dev.jlarteaga.coordinator.messaging.nlpmanager;

import com.rabbitmq.client.Channel;
import dev.jlarteaga.coordinator.messaging.payload.ProcessTextResponsePayload;
import dev.jlarteaga.coordinator.webclient.DatasetManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.PROCESS_TEXT_RESPONSES_QUEUE;

@Component
public class NlpCoordinator {

    private final Logger logger = LoggerFactory.getLogger(NlpCoordinator.class);

    private final DatasetManagerService datasetManagerService;

    public NlpCoordinator(DatasetManagerService datasetManagerService) {
        this.datasetManagerService = datasetManagerService;
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

package dev.jlarteaga.coordinator.messaging.datasetmanager;

import com.rabbitmq.client.Channel;
import dev.jlarteaga.coordinator.messaging.payload.DatasetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.DATASET_EVENTS_QUEUE_NAME;

@Component
public class DatasetCoordinator {

    Logger logger = LoggerFactory.getLogger(DatasetCoordinator.class);
    private final List<DatasetEventHandler> eventHandlers;

    public DatasetCoordinator(TextEventHandler textEventHandler) {
        eventHandlers = List.of(textEventHandler);
    }

    @RabbitListener(
            queues = DATASET_EVENTS_QUEUE_NAME,
            ackMode = "MANUAL"
    )
    public void processEvent(
            DatasetEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        Flux.fromIterable(eventHandlers)
                .filter(eventHandler -> eventHandler.canHandlePattern(event.getPattern()))
                .flatMap(eventHandler -> eventHandler.dispatch(event.getPattern(), event.getData()))
                .subscribe(operationResponse -> {
                    try {
                        if (operationResponse.getSuccess()) {
                            logger.info(operationResponse.getMessage());
                            channel.basicAck(tag, false);
                        } else {
                            logger.error(operationResponse.getMessage(), event);
                            channel.basicNack(tag, false, false);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                });
    }
}

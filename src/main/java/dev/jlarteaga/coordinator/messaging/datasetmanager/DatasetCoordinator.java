package dev.jlarteaga.coordinator.messaging.datasetmanager;

import com.rabbitmq.client.Channel;
import dev.jlarteaga.coordinator.messaging.payload.DatasetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.jlarteaga.coordinator.messaging.MessagingConfiguration.DATASET_EVENTS_QUEUE_NAME;

@Component
public class DatasetCoordinator {

    private final TextEventHandler textCoordinator;
    Logger logger = LoggerFactory.getLogger(DatasetCoordinator.class);

    public DatasetCoordinator(TextEventHandler textCoordinator) {
        this.textCoordinator = textCoordinator;
    }

    @RabbitListener(
            queues = DATASET_EVENTS_QUEUE_NAME,
            ackMode = "MANUAL"
    )
    public void processEvent(
            DatasetEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        if (TextEventHandler.PATTERNS.contains(event.getPattern())) {
            textCoordinator.dispatch(event.getPattern(), event.getData())
                    .subscribe(result -> {
                        try {
                            if (result) {
                                channel.basicAck(tag, false);
                            } else {
                                logger.error("Could not process payload", event);
                                channel.basicNack(tag, false, false);
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    });
        }
    }
}

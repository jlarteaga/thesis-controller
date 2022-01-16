package dev.jlarteaga.coordinator.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

    public static final String DATASET_EVENTS_QUEUE_NAME = "dataset-events";

    public static final String PROCESS_TEXT_REQUESTS_QUEUE = "nlp.rpc.process-text.requests";
    public static final String PROCESS_TEXT_RESPONSES_QUEUE = "nlp.rpc.process-text.responses";

    public static final String SIMILARITY_MATRIX_REQUESTS_QUEUE = "nlp.rpc.similarity-matrix.requests";
    public static final String SIMILARITY_MATRIX_RESPONSES_QUEUE = "nlp.rpc.similarity-matrix.responses";

    public static final String PROCESS_TEXT_EXCHANGE_NAME = "nlp.process-text";
    public static final String SIMILARITY_MATRIX_EXCHANGE_NAME = "nlp.similarity-matrix";

    public static final String RESPONSE_KEY = "response";
    public static final String REQUEST_KEY = "request";

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue datasetEventsQueue() {
        return new Queue(DATASET_EVENTS_QUEUE_NAME);
    }

    @Bean
    public Queue processTextRequestsQueue() {
        return new Queue(PROCESS_TEXT_REQUESTS_QUEUE, true);
    }

    @Bean
    public Queue processTextResponsesQueue() {
        return new Queue(PROCESS_TEXT_RESPONSES_QUEUE, true);
    }

    @Bean
    public DirectExchange processTextExchange() {
        return new DirectExchange(PROCESS_TEXT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue matrixSimilarityRequestsQueue() {
        return new Queue(SIMILARITY_MATRIX_REQUESTS_QUEUE, true);
    }

    @Bean
    public Queue matrixSimilarityResponsesQueue() {
        return new Queue(SIMILARITY_MATRIX_RESPONSES_QUEUE, true);
    }

    @Bean
    public DirectExchange similarityMatrixExchange() {
        return new DirectExchange(SIMILARITY_MATRIX_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding processTextResponsesBinding(
            @Qualifier("processTextExchange") DirectExchange processTextExchange,
            @Qualifier("processTextResponsesQueue") Queue processTextResponsesQueue) {
        return BindingBuilder.bind(processTextResponsesQueue)
                .to(processTextExchange)
                .with(RESPONSE_KEY);
    }

    @Bean
    public Binding processTextRequestsBinding(
            @Qualifier("processTextExchange") DirectExchange processTextExchange,
            @Qualifier("processTextRequestsQueue") Queue processTextRequestsQueue) {
        return BindingBuilder.bind(processTextRequestsQueue)
                .to(processTextExchange)
                .with(REQUEST_KEY);
    }

    @Bean
    public Binding similarityMatrixResponsesBinding(
            @Qualifier("similarityMatrixExchange") DirectExchange similarityMatrixExchange,
            @Qualifier("matrixSimilarityResponsesQueue") Queue similarityMatrixResponsesQueue) {
        return BindingBuilder.bind(similarityMatrixResponsesQueue)
                .to(similarityMatrixExchange)
                .with(RESPONSE_KEY);
    }

    @Bean
    public Binding similarityMatrixRequestsBinding(
            @Qualifier("similarityMatrixExchange") DirectExchange similarityMatrixExchange,
            @Qualifier("matrixSimilarityRequestsQueue") Queue similarityMatrixRequestsQueue) {
        return BindingBuilder.bind(similarityMatrixRequestsQueue)
                .to(similarityMatrixExchange)
                .with(REQUEST_KEY);
    }
}

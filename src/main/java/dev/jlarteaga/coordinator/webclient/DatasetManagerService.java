package dev.jlarteaga.coordinator.webclient;

import dev.jlarteaga.coordinator.configuration.ConfigurationService;
import dev.jlarteaga.coordinator.model.TextProcessingStatus;
import dev.jlarteaga.coordinator.webclient.dto.PatchResponseDTO;
import dev.jlarteaga.coordinator.webclient.dto.auth.LoginRequestDTO;
import dev.jlarteaga.coordinator.webclient.dto.auth.LoginResponseDTO;
import dev.jlarteaga.coordinator.webclient.dto.question.GetQuestionSummarizedDTO;
import dev.jlarteaga.coordinator.webclient.dto.text.GetTextMetaDetailedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class DatasetManagerService {

    Logger logger = LoggerFactory.getLogger(DatasetManagerService.class);

    private final Flux<String> tokenFlux;
    private final ConfigurationService configurationService;

    private final WebClient unauthorizedWebClient;
    private final Mono<WebClient> authorizedWebClient;

    public DatasetManagerService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.unauthorizedWebClient = WebClient.builder()
                .baseUrl(this.configurationService.getDatasetManagerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.tokenFlux = generateAuthenticationFlux();
        this.tokenFlux.subscribe();
        this.authorizedWebClient = Mono.defer(() -> this.tokenFlux.next().map(token -> WebClient.builder()
                .baseUrl(this.configurationService.getDatasetManagerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build()));
    }


    private Flux<String> generateAuthenticationFlux() {
        return Flux.interval(Duration.ZERO, Duration.ofHours(1L))
                .flatMap((value) -> this.login("joedon@incognito.org", "secret123.")
                        .retryWhen(Retry.backoff(5, Duration.ofSeconds(5L)))
                        .map(LoginResponseDTO::getToken)
                )
                .cache(1);
    }

    public Mono<LoginResponseDTO> login(String email, String password) {
        return unauthorizedWebClient.post().uri("auth/login")
                .bodyValue(new LoginRequestDTO(email, password))
                .retrieve()
                .bodyToMono(LoginResponseDTO.class);
    }

    public Flux<GetQuestionSummarizedDTO> getQuestions() {
        return unauthorizedWebClient.get().uri("questions")
                .retrieve()
                .bodyToFlux(GetQuestionSummarizedDTO.class);
    }

    public Mono<GetTextMetaDetailedDTO> getText(String uuid) {
        return unauthorizedWebClient.get().uri("texts/{uuid}", uuid)
                .retrieve()
                .bodyToMono(GetTextMetaDetailedDTO.class);
    }

    public Mono<PatchResponseDTO> updateTextProcessingStatus(String uuid, TextProcessingStatus status, boolean silent) {
        return authorizedWebClient.flatMap(webClient -> webClient.patch()
                        .uri("texts/{uuid}", uuid)
                        .bodyValue(Map.of("processingStatus", status.getText()))
                        .headers(httpHeaders -> {
                            if (silent) httpHeaders.add("x-silent", "true");
                        })
                        .retrieve()
                        .bodyToMono(PatchResponseDTO.class))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        logger.error(((WebClientResponseException) error).getResponseBodyAsString());
                    }
                });
    }

    public Mono<PatchResponseDTO> updateProcessedText(String uuid, String processedText, boolean silent) {
        return authorizedWebClient.flatMap(webClient -> webClient.patch()
                        .uri("texts/{uuid}", uuid)
                        .bodyValue(Map.of(
                                "processingStatus", TextProcessingStatus.Processed.getText(),
                                "processed", processedText))
                        .headers(httpHeaders -> {
                            if (silent) httpHeaders.add("x-silent", "true");
                        })
                        .retrieve()
                        .bodyToMono(PatchResponseDTO.class))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        logger.error(((WebClientResponseException) error).getResponseBodyAsString());
                    }
                });
    }
}

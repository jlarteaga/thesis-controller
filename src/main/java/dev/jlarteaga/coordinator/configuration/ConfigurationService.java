package dev.jlarteaga.coordinator.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Getter
    @Value("${dataset-manager.url}")
    private String datasetManagerUrl;
}

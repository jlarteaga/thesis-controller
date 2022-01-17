package dev.jlarteaga.coordinator.security;

import dev.jlarteaga.coordinator.security.auth.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;

@EnableWebFluxSecurity
public class CoordinatorSecurityConfiguration {

    private final SecurityContextRepository securityContextRepository;

    public CoordinatorSecurityConfiguration(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .cors().configurationSource(exchange -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("OPTIONS", "POST"));
                    corsConfiguration.setMaxAge(600L);
                    return corsConfiguration;
                }).and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .securityContextRepository(securityContextRepository)
                .authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll().and()
                .authorizeExchange().pathMatchers("/operations/**").authenticated().and()
                .authorizeExchange().anyExchange().permitAll().and()
                .build();

    }
}

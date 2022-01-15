package dev.jlarteaga.coordinator.security.auth;

import dev.jlarteaga.coordinator.security.AuthenticationManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;

    public SecurityContextRepository(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String authToken;
        String authHeader;
        Authentication auth;

        if (
                (authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)) != null && authHeader.startsWith("Bearer ")
        ) {
            authToken = authHeader.substring(7);
        } else {
            return Mono.empty();
        }
        auth = new UsernamePasswordAuthenticationToken(authToken, request.getURI().getPath());
        return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
    }
}

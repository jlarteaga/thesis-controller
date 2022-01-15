package dev.jlarteaga.coordinator.security;

import com.nimbusds.jwt.JWTClaimsSet;
import dev.jlarteaga.coordinator.security.auth.jwt.JwtUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtils jwtUtils;

    public AuthenticationManager(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String authToken = authentication.getPrincipal().toString();

        String uuid;
        try {
            uuid = jwtUtils.getUuidFromToken(authToken);
        } catch (Exception e) {
            uuid = null;
        }

        if (uuid != null && jwtUtils.validateToken(authToken)) {
            try {
                JWTClaimsSet claimsSet = jwtUtils.getAllClaimsFromToken(authToken);
                List<String> rolesMap;
                if (claimsSet.getClaim("roles") == null || !(claimsSet.getClaim("roles") instanceof List)) {
                    rolesMap = new ArrayList<>();
                } else {
                    //noinspection unchecked
                    rolesMap = (List<String>) claimsSet.getClaim("roles");
                }
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        uuid,
                        null,
                        rolesMap.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
                return Mono.just(auth);
            } catch (Exception e) {
                return Mono.empty();
            }
        } else {
            return Mono.empty();
        }
    }
}

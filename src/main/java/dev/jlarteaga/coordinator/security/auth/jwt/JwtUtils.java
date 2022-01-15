package dev.jlarteaga.coordinator.security.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import dev.jlarteaga.coordinator.configuration.ConfigurationService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Period;
import java.util.Date;

@Service
public class JwtUtils {

    private final ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor;

    public JwtUtils(ConfigurationService configurationService) {
        jwtProcessor = new DefaultJWTProcessor<>();
        String secret = configurationService.getSecuritySecret();
        JWKSource<SimpleSecurityContext> key = new ImmutableSecret<>(secret.getBytes());
        JWSKeySelector<SimpleSecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.HS256, key);
        jwtProcessor.setJWSKeySelector(keySelector);
    }

    public static Long getExpiration(int days) {
        return new Date().toInstant().plus(Period.ofDays(days)).toEpochMilli();
    }

    public final JWTClaimsSet getAllClaimsFromToken(String token) throws ParseException, BadJOSEException, JOSEException {
        return jwtProcessor.process(token, null);
    }

    public final Date getExpirationDateFromToken(String token) throws ParseException, BadJOSEException, JOSEException {
        return getAllClaimsFromToken(token).getExpirationTime();
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public final String getUuidFromToken(String token) throws ParseException, BadJOSEException, JOSEException {
        return getAllClaimsFromToken(token).getStringClaim("uuid");
    }
}

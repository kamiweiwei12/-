package edu.sysu.snowfield.identity.infrastructure.security;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityConfigTest {

    private static final String VALID_SECRET = "unit-test-security-secret-at-least-32-bytes";

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void acceptsValidSecuritySettings() {
        var key = config.jwtSecretKey(properties(
                VALID_SECRET, "https://snowfield.test", Duration.ofMinutes(30), "http://localhost:5173"));

        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
    }

    @Test
    void rejectsMissingShortOrPlaceholderSecret() {
        assertInvalid(properties(null, "https://snowfield.test", Duration.ofMinutes(30), "http://localhost:5173"),
                "SNOWFIELD_JWT_SECRET");
        assertInvalid(properties("too-short", "https://snowfield.test", Duration.ofMinutes(30),
                        "http://localhost:5173"),
                "SNOWFIELD_JWT_SECRET");
        assertInvalid(properties("REPLACE_WITH_AT_LEAST_32_RANDOM_BYTES", "https://snowfield.test",
                        Duration.ofMinutes(30), "http://localhost:5173"),
                "SNOWFIELD_JWT_SECRET");
    }

    @Test
    void rejectsMissingRelativeOrNonHttpIssuer() {
        assertInvalid(properties(VALID_SECRET, null, Duration.ofMinutes(30), "http://localhost:5173"),
                "issuer");
        assertInvalid(properties(VALID_SECRET, "snowfield.test", Duration.ofMinutes(30),
                        "http://localhost:5173"),
                "issuer");
        assertInvalid(properties(VALID_SECRET, "file:///tmp/issuer", Duration.ofMinutes(30),
                        "http://localhost:5173"),
                "issuer");
    }

    @Test
    void rejectsMissingZeroOrExcessiveTokenTtl() {
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", null, "http://localhost:5173"),
                "有效期");
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", Duration.ZERO,
                        "http://localhost:5173"),
                "有效期");
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", Duration.ofMinutes(31),
                        "http://localhost:5173"),
                "有效期");
    }

    @Test
    void rejectsWildcardNonHttpOrPathBearingCorsOrigin() {
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", Duration.ofMinutes(30), "*"),
                "CORS allowed origin");
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", Duration.ofMinutes(30),
                        "file:///tmp/frontend"),
                "CORS allowed origin");
        assertInvalid(properties(VALID_SECRET, "https://snowfield.test", Duration.ofMinutes(30),
                        "https://frontend.example/app"),
                "CORS allowed origin");
    }

    private static SnowfieldSecurityProperties properties(
            String secret, String issuer, Duration ttl, String origin) {
        return new SnowfieldSecurityProperties(secret, issuer, ttl, origin);
    }

    private void assertInvalid(SnowfieldSecurityProperties properties, String message) {
        assertThatThrownBy(() -> config.jwtSecretKey(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(message);
    }
}

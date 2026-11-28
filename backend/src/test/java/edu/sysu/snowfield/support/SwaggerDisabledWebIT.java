package edu.sysu.snowfield.support;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
@ActiveProfiles("test")
class SwaggerDisabledWebIT {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.create("http://127.0.0.1:" + port);
    }

    @Test
    void openApiDocumentIsNotExposedWhenDisabled() {
        assertNotFound("/v3/api-docs");
    }

    @Test
    void swaggerUiIsNotExposedWhenDisabled() {
        assertNotFound("/swagger-ui/index.html");
    }

    private void assertNotFound(String path) {
        assertThatThrownBy(() -> client().get().uri(path).retrieve().toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.NotFound.class)
                .satisfies(thrown -> assertThat(((HttpClientErrorException) thrown).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }
}

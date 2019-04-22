package ttyppo.weatherservice.backend.controller;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;

import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestExpectationCallback implements ExpectationCallback {

    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.getPath().getValue().endsWith("/wfs")) {
            try {
                String body = new String(Files.readAllBytes(
                        Paths.get(getClass().getClassLoader()
                                        .getResource("fmi-test-data.xml")
                                        .toURI())));
                return response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", MediaType.TEXT_XML_VALUE)
                        .withBody(body);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                return response().withStatusCode(400);
            }
        } else {
            return notFoundResponse();
        }
    }
}
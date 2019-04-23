package ttyppo.weatherservice.backend.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;

import java.net.URL;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.backend.datasource.fmi.FMI;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherRequest;
import ttyppo.weatherservice.model.WeatherResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherAPIControllerIT {

    private static final int mockServerPort = 8082;

    @TestConfiguration
    static class FMITestConfig {
        @Bean
        public FMI fmi(RestTemplate restTemplate) {
            return new FMI("http://localhost:" + mockServerPort, restTemplate);
        }
    }

    @LocalServerPort
    private int port;

    private URL base;

    private ClientAndServer mockServer;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        mockServer = startClientAndServer(mockServerPort);
        base = new URL("http://localhost:" + port + "/weather/api");
    }

    @After
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void postWeather() throws Exception {
        mockServer
                .when(
                        request().withPath("/wfs"))
                .callback(
                        callback()
                                .withCallbackClass(
                                        TestExpectationCallback.class.getName())
                );
        Location location = new Location();
        location.setName("Some Town");
        WeatherRequest request = new WeatherRequest();
        request.setLocation(location);
        ResponseEntity<WeatherResponse> response = template.postForEntity(base.toURI(),
                request, WeatherResponse.class);
        assertThat(response.getBody().getWeatherForecast(), notNullValue());
        assertThat(response.getBody().getWeatherForecast().getWeatherServiceName(),
                equalToIgnoringCase("FMI (Ilmatieteenlaitos)"));
        assertThat(response.getBody().getWeatherForecast().getLocation().getName(),
                equalToIgnoringCase("Some Town"));
        assertThat(response.getBody().getWeatherForecast().getCurrentWeather().getTemperature().toString(),
                equalToIgnoringCase("3.51"));
    }
}
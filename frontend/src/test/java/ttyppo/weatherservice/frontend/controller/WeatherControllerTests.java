package ttyppo.weatherservice.frontend.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;
import ttyppo.weatherservice.model.WeatherResponse;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static ttyppo.weatherservice.frontend.controller.WeatherController.BACKEND_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(properties = "logging.level.org.springframework.web=DEBUG")
@AutoConfigureMockMvc
public class WeatherControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void rendersForm() throws Exception {
        // expect
        mockMvc.perform(get("/weather"))
                .andExpect(content().string(containsString("Search weather")));
    }

    @Test
    public void submitsForm() throws Exception {
        // given
        Location location = new Location();
        location.setName("Some Town");
        WeatherForecast forecast = new WeatherForecast();
        forecast.setCurrentTemperature("21.4");
        forecast.setWeatherServiceName("Test weather service");
        WeatherResponse response = new WeatherResponse();
        response.setWeatherForecast(forecast);
        Mockito.when(restTemplate.postForObject(eq(BACKEND_URL), any(), eq(WeatherResponse.class)))
                .thenReturn(response);

        //expect
        mockMvc.perform(post("/weather").param("name", "Some Town"))
                .andExpect(content().string(containsString("Result")))
                .andExpect(content().string(containsString("Location: Some Town")))
                .andExpect(content().string(containsString("Weather service provider: Test weather service")))
                .andExpect(content().string(containsString("Current temperature: 21.4")));
    }

}

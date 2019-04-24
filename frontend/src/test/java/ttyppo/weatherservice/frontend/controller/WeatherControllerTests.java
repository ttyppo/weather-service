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
import ttyppo.weatherservice.model.WeatherCondition;
import ttyppo.weatherservice.model.WeatherForecast;
import ttyppo.weatherservice.model.WeatherResponse;

import java.time.ZonedDateTime;
import java.util.Collections;

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
        WeatherCondition currentWeather = new WeatherCondition();
        currentWeather.setIconId(1);
        currentWeather.setTemperature(21.4f);
        currentWeather.setTime(ZonedDateTime.now());
        WeatherCondition forecastWeather = new WeatherCondition();
        forecastWeather.setIconId(2);
        forecastWeather.setTemperature(22.4f);
        forecastWeather.setTime(ZonedDateTime.now().plusDays(1));
        WeatherForecast forecast = new WeatherForecast();
        forecast.setCurrentWeather(currentWeather);
        forecast.setWeatherServiceName("Test weather service");
        forecast.setForecastConditions(Collections.singletonList(forecastWeather));
        WeatherResponse response = new WeatherResponse();
        response.setWeatherForecast(forecast);
        Mockito.when(restTemplate.postForObject(eq(BACKEND_URL), any(), eq(WeatherResponse.class)))
                .thenReturn(response);

        //expect
        mockMvc.perform(post("/weather").param("name", "Some Town"))
                .andExpect(content().string(containsString("Weather forecast")))
                .andExpect(content().string(containsString("Location: Some Town")))
                .andExpect(content().string(containsString("Weather service provider: Test weather service")))
                .andExpect(content().string(containsString("Current weather")))
                .andExpect(content().string(containsString("21.4 °C")))
                .andExpect(content().string(containsString("symbols/1.svg")))
                .andExpect(content().string(containsString("Forecast")))
                .andExpect(content().string(containsString("22.4 °C")))
                .andExpect(content().string(containsString("symbols/2.svg")));
    }

}

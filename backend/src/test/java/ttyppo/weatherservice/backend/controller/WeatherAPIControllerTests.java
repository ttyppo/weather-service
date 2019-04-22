package ttyppo.weatherservice.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ttyppo.weatherservice.backend.model.WeatherServices;
import ttyppo.weatherservice.backend.model.WeatherService;
import ttyppo.weatherservice.model.WeatherForecast;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
public class WeatherAPIControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherServices weatherServices;

    @Test
    public void noBodyWeatherRequestShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(post("/weather/api")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void jsonBodyWeatherShouldReturnWeatherForecast() throws Exception {
        List<WeatherService> testWeatherServiceList = Collections.singletonList(location -> {
            WeatherForecast forecast = new WeatherForecast();
            forecast.setWeatherServiceName("Test weather API service");
            forecast.setLocation(location);
            forecast.setCurrentTemperature("21.1");
            return forecast;
        });
        Mockito.when(weatherServices.getServiceProviders()).thenReturn(testWeatherServiceList);
        this.mockMvc.perform(post("/weather/api").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"location\":{\"name\":\"Some Town\"}}")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.weatherForecast.weatherServiceName")
                        .value("Test weather API service"))
                .andExpect(jsonPath("$.weatherForecast.location.name").value("Some Town"))
                .andExpect(jsonPath("$.weatherForecast.currentTemperature").value("21.1"));
    }

    @Test
    public void jsonBodyWeatherWithNonExistingLocationShouldReturnEmptyList() throws Exception {
        List<WeatherService> testWeatherServiceList = Collections.singletonList(location -> null);
        Mockito.when(weatherServices.getServiceProviders()).thenReturn(testWeatherServiceList);
        this.mockMvc.perform(post("/weather/api").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"location\":{\"name\":\"Some Non Existing Place\"}}")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"weatherForecast\":null}"));
    }
}

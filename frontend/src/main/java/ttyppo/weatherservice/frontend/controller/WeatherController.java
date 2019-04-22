package ttyppo.weatherservice.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;
import ttyppo.weatherservice.model.WeatherRequest;
import ttyppo.weatherservice.model.WeatherResponse;

@Controller
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    static final String BACKEND_URL = "http://localhost:8081/weather/api";

    private RestTemplate restTemplate;

    public WeatherController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/weather")
    public String weatherForm(Model model) {
        model.addAttribute("location", new Location());
        return "weather";
    }

    @PostMapping("/weather")
    public String weatherSubmit(Model model, @ModelAttribute Location location) {
        model.addAttribute("location", location);
        WeatherRequest request = new WeatherRequest();
        request.setLocation(location);
        WeatherForecast forecast = null;
        try {
            WeatherResponse response = restTemplate.postForObject(BACKEND_URL,
                    request, WeatherResponse.class);
            if (response != null) {
                forecast = response.getWeatherForecast();
            }
        } catch (RestClientException e) {
            log.error("Weather request failed!", e);
        }
        model.addAttribute("weather", forecast);
        return "result";
    }

}

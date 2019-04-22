package ttyppo.weatherservice.backend.controller;

import org.springframework.web.bind.annotation.*;
import ttyppo.weatherservice.backend.model.WeatherServices;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;
import ttyppo.weatherservice.model.WeatherRequest;
import ttyppo.weatherservice.model.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WeatherAPIController {

    private WeatherServices weatherServices;

    WeatherAPIController(WeatherServices weatherServices) {
        this.weatherServices = weatherServices;
    }

    @PostMapping("/weather/api")
    public WeatherResponse weatherAPI(@RequestBody WeatherRequest request) {
        final List<WeatherForecast> forecasts = getWeatherForecasts(request.getLocation());
        WeatherResponse response = new WeatherResponse();
        if (forecasts.size() > 0) {
            response.setWeatherForecast(forecasts.get(0));
        }
        return response;
    }

    private List<WeatherForecast> getWeatherForecasts(final Location location) {
        final List<WeatherForecast> forecasts = new ArrayList<>();
        weatherServices.getServiceProviders().forEach(provider -> {
                WeatherForecast forecast = provider.fetchForecast(location);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
        });
        return forecasts;
    }
}

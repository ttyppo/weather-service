package ttyppo.weatherservice.backend.controller;

import org.springframework.web.bind.annotation.*;
import ttyppo.weatherservice.backend.model.WeatherServices;
import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;
import ttyppo.weatherservice.model.WeatherRequest;
import ttyppo.weatherservice.model.WeatherResponse;

import java.util.Objects;
import java.util.Optional;

@RestController
public class WeatherAPIController {

    private WeatherServices weatherServices;

    WeatherAPIController(WeatherServices weatherServices) {
        this.weatherServices = weatherServices;
    }

    @PostMapping("/weather/api")
    public WeatherResponse weatherAPI(@RequestBody WeatherRequest request) {
        final Optional<WeatherForecast> forecasts = getWeatherForecast(request.getLocation());
        WeatherResponse response = new WeatherResponse();
        response.setWeatherForecast(forecasts.orElse(null));
        return response;
    }

    private Optional<WeatherForecast> getWeatherForecast(final Location location) {
        return weatherServices.getServiceProviders()
                .parallelStream()
                .map(provider -> provider.fetchForecast(location))
                .filter(Objects::nonNull)
                .findAny();
    }
}

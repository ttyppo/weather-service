package ttyppo.weatherservice.backend.model;

import ttyppo.weatherservice.model.Location;
import ttyppo.weatherservice.model.WeatherForecast;

public interface WeatherService {

    WeatherForecast fetchForecast(Location location);
}

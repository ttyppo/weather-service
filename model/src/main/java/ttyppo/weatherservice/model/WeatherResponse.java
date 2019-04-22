package ttyppo.weatherservice.model;

public class WeatherResponse {

    private WeatherForecast weatherForecast;

    public WeatherForecast getWeatherForecast() {
        return weatherForecast;
    }

    public void setWeatherForecast(WeatherForecast weatherForecast) {
        this.weatherForecast = weatherForecast;
    }
}

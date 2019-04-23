package ttyppo.weatherservice.model;

public class WeatherForecast {

    private String weatherServiceName;
    private Location location;
    private WeatherCondition currentWeather;

    public String getWeatherServiceName() {
        return weatherServiceName;
    }

    public void setWeatherServiceName(String weatherServiceName) {
        this.weatherServiceName = weatherServiceName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public WeatherCondition getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(WeatherCondition currentWeather) {
        this.currentWeather = currentWeather;
    }
}

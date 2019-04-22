package ttyppo.weatherservice.model;

public class WeatherForecast {

    private String weatherServiceName;
    private Location location;
    private String currentTemperature;

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

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }
}

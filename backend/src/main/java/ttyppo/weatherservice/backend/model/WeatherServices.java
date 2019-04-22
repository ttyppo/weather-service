package ttyppo.weatherservice.backend.model;

import java.util.List;

public class WeatherServices {

    private List<WeatherService> serviceProviders;

    public WeatherServices(List<WeatherService> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public List<WeatherService> getServiceProviders() {
        return serviceProviders;
    }
}

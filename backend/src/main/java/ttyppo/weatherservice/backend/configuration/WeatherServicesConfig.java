package ttyppo.weatherservice.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ttyppo.weatherservice.backend.model.WeatherService;
import ttyppo.weatherservice.backend.model.WeatherServices;
import ttyppo.weatherservice.backend.datasource.fmi.FMI;

import java.util.Collections;
import java.util.List;

@Configuration
public class WeatherServicesConfig {

    private List<WeatherService> services;

    WeatherServicesConfig(FMI fmi) {
        services = Collections.singletonList(fmi);
    }

    @Bean
    public WeatherServices weatherServices() {
        return new WeatherServices(services);
    }
}
